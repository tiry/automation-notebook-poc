package org.nuxeo.ecm;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.nuxeo.automation.scripting.api.AutomationScriptingService;
import org.nuxeo.automation.scripting.internals.AutomationScriptingComponent;
import org.nuxeo.automation.scripting.internals.ScriptingOperationDescriptor;
import org.nuxeo.ecm.automation.OperationContext;
import org.nuxeo.ecm.automation.core.Constants;
import org.nuxeo.ecm.automation.core.annotations.Context;
import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
import org.nuxeo.ecm.automation.core.annotations.Param;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.platform.rendering.api.RenderingException;
import org.nuxeo.runtime.api.Framework;

/**
 *
 */
@Operation(id = AutomationKernelExecutor.ID, category = Constants.CAT_DOCUMENT, label = "Automation.KernelExecutor", description = "Describe here what your operation does.")
public class AutomationKernelExecutor {

	public static final String ID = "Automation.KernelExecutor";

	@Context
	protected CoreSession session;

	@Context
	protected OperationContext ctx;
	
	@OperationMethod
	public String run(String content) throws Exception {
		
		NoteBookConsole.initMemoryLog();
		
		try {
			PreProcessingResult preProcessedCode = preprocessCode(content);
			
			AutomationScriptingService service = Framework.getService(AutomationScriptingService.class);
			InputStream script = IOUtils.toInputStream(preProcessedCode.code);
	
			long t0 = System.currentTimeMillis();
			Object result=null;
			Map<String, Object> params = new HashMap<>();

			try {
				result = service.get(ctx).run(script);
			} catch (NuxeoException e) {
				long t1 = System.currentTimeMillis();
				params.put("t", t1 - t0);				
				return renderResult(e, params);
			}
			
			long t1 = System.currentTimeMillis();	
			params.put("t", t1 - t0);
				
			if (preProcessedCode.opId!=null) {
				result = preProcessedCode;
			}
			
			return renderResult(result, params);
		} finally {
			NoteBookConsole.cleanMemoryLog();
		}
	}

	protected class PreProcessingResult {
		
		protected String code;
		protected String opId;
				
	}
		
	protected PreProcessingResult preprocessCode(String code) {
		
		PreProcessingResult result = new PreProcessingResult();		
		code = code.trim();
		
        Pattern opPattern = Pattern.compile("@Operation\\(.*id.*=.*\"(.*)\".*\\)");
        Matcher matcher = opPattern.matcher(code);
        
        if (matcher.lookingAt()) {
        	String opId =matcher.group(1);
        	code = matcher.replaceFirst("");        	
        	if (code.startsWith("\n")) {
        		code=code.substring(1);
        	}
        	AutomationHelper.register(opId, code);
        	result.opId = opId;        	        	        	
        }       	
        
        result.code=code;        
		return result;
	}
	
	protected String renderResult(Object result, Map<String, Object> params) throws RenderingException {

		FMRenderer renderer = new FMRenderer();
		params.put("result", result);
		params.put("logs", NoteBookConsole.getMemoryLog());

		if (result instanceof DocumentModel) {
			params.put("doc", result);
			return renderer.render("notebook/doc.ftl", params);
		} else if (result instanceof DocumentModelList) {
			params.put("docs", result);
			return renderer.render("notebook/docs.ftl", params);
		} else if (result instanceof PreProcessingResult) {		
			params.put("opId", ((PreProcessingResult)result).opId);
			return renderer.render("notebook/opregister.ftl", params);
		} else if (result instanceof NuxeoException) {			
			NuxeoException e = (NuxeoException)result;							
			params.put("e", e);			
			return renderer.render("notebook/error.ftl", params);
		} else {
			return renderer.render("notebook/default.ftl", params);
		}
	}

}
