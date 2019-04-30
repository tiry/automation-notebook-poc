package org.nuxeo.ecm.automation.interactive;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.nuxeo.automation.scripting.api.AutomationScriptingService;
import org.nuxeo.ecm.automation.OperationContext;
import org.nuxeo.ecm.automation.interactive.helpers.AssertHelper;
import org.nuxeo.ecm.automation.interactive.helpers.NoteBookConsole;
import org.nuxeo.ecm.automation.notebook.PreProcessor;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.platform.rendering.api.RenderingException;
import org.nuxeo.runtime.api.Framework;

public class NBScriptExecutor {

	public String run(CoreSession session, String content, String format) throws Exception {	
		OperationContext ctx = new OperationContext(session);
		return run(ctx, content, format);
	}
	
	public String run(OperationContext ctx, String content, String format) throws Exception {

		// init NB specific Helpers
		NoteBookConsole.initMemoryLog();
		AssertHelper.initMemoryLog();

		PreProcessor preprocessor = new PreProcessor();
		try {
			PreProcessor.Result preProcessedCode = preprocessor.preprocessCode(content);

			AutomationScriptingService service = Framework.getService(AutomationScriptingService.class);
			InputStream script = IOUtils.toInputStream(preProcessedCode.getCode(), "UTF-8");

			long t0 = System.currentTimeMillis();
			Object result = null;
			Map<String, Object> params = new HashMap<>();

			try {
				result = service.get(ctx).run(script);
			} catch (NuxeoException e) {
				long t1 = System.currentTimeMillis();
				params.put("t", t1 - t0);
				return render(e, params, format);
			}

			long t1 = System.currentTimeMillis();
			params.put("t", t1 - t0);

			if (preProcessedCode.getId() != null) {
				result = preProcessedCode;
			}

			return render(result, params, format);
		} finally {
			NoteBookConsole.cleanMemoryLog();
			AssertHelper.cleanMemoryLog();
		}
	}

	
	protected String render(Object result, Map<String, Object> params, String format) throws RenderingException {

		FMRenderer renderer = new FMRenderer(format);
		params.put("result", result);
		params.put("logs", NoteBookConsole.getMemoryLog());
		params.put("asserts", AssertHelper.getMemoryLog());

		if (result == null) {
			return renderer.render("null.ftl", params);
		} else if (result instanceof DocumentModel) {
			params.put("doc", result);
			return renderer.render("doc.ftl", params);
		} else if (result instanceof DocumentModelList) {
			params.put("docs", result);
			return renderer.render("docs.ftl", params);
		} else if (result instanceof PreProcessor.Result) {
			params.put("opId", ((PreProcessor.Result) result).getId());
			return renderer.render("opregister.ftl", params);
		} else if (result instanceof NuxeoException) {
			NuxeoException e = (NuxeoException) result;
			params.put("e", e);
			return renderer.render("error.ftl", params);
		} else {
			params.put("res_type", result.getClass().getName());
			params.put("res_str", result.toString());
			return renderer.render("default.ftl", params);
		}
	}

}
