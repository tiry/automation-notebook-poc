package org.nuxeo.ecm.automation.interactive;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.nuxeo.automation.scripting.api.AutomationScriptingService;
import org.nuxeo.automation.scripting.api.AutomationScriptingService.Session;
import org.nuxeo.automation.scripting.internals.AutomationMapper;
import org.nuxeo.ecm.automation.OperationContext;
import org.nuxeo.ecm.automation.interactive.helpers.AssertHelper;
import org.nuxeo.ecm.automation.interactive.helpers.NoteBookConsole;
import org.nuxeo.ecm.automation.interactive.helpers.TestHarness;
import org.nuxeo.ecm.automation.notebook.PreProcessor;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.platform.rendering.api.RenderingException;
import org.nuxeo.runtime.api.Framework;

public class NBScriptExecutor {

	public String runAndRender(CoreSession session, String content, String format) throws Exception {
		OperationContext ctx = new OperationContext(session);
		return runAndRender(ctx, content, format);
	}

	public class ExecutionResult {

		public static final String LOGS_KEY = "logs";
		public static final String ASSERTS_KEY = "asserts";
		public static final String TIME_KEY = "t";
				
		protected Object result;
		protected Map<String, Object> params;

		ExecutionResult(Object result, Map<String, Object> params) {
			this.result = result;
			this.params = params;
		}
		
		public Object getOutcome() {
			return result;
		}
		
		public Map<String, Object> getParams() {
			return params;
		}
	}

	public ExecutionResult run(CoreSession session, String content) throws Exception {
		OperationContext ctx = new OperationContext(session);
		return run(ctx, content);
	}
	
	protected void fillParams(Map<String, Object> params, long t0) {
		long t1 = System.currentTimeMillis();
		params.put(ExecutionResult.TIME_KEY, t1 - t0);
		params.put(ExecutionResult.LOGS_KEY, NoteBookConsole.getLogs());
		params.put(ExecutionResult.ASSERTS_KEY, AssertHelper.getMemoryLog());
	}
	
	public ExecutionResult run(OperationContext ctx, String content) throws Exception {

		// init NB specific Helpers
		AssertHelper.initMemoryLog();
		TestHarness harness = new TestHarness();
		
		PreProcessor preprocessor = new PreProcessor();
		try {
			PreProcessor.Result preProcessedCode = preprocessor.preprocessCode(content);

			AutomationScriptingService service = Framework.getService(AutomationScriptingService.class);
			InputStream script = IOUtils.toInputStream(preProcessedCode.getCode(), "UTF-8");

			long t0 = System.currentTimeMillis();
			Object result = null;
			Map<String, Object> params = new HashMap<>();

			try {
				Session session = service.get(ctx);
				session.adapt(AutomationMapper.class).put(TestHarness.ID, harness);
				//session.adapt(typeof)
				result = session.run(script);
			} catch (NuxeoException e) {				
				fillParams(params,t0);
				return new ExecutionResult(e, params);
			}

			fillParams(params,t0);			
			if (preProcessedCode.getId() != null) {
				result = preProcessedCode;
			}

			return new ExecutionResult(result, params);
		} finally {
			NoteBookConsole.cleanLogs();
			AssertHelper.cleanMemoryLog();
			harness.cleanup();
		}
	}

	public String runAndRender(OperationContext ctx, String content, String format) throws Exception {
		ExecutionResult res = run(ctx, content);
		return render(res, format);
	}

	public String render(ExecutionResult res, String format) throws RenderingException {

		Object result = res.result;
		Map<String, Object> params = res.params;
		FMRenderer renderer = new FMRenderer(format);

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
