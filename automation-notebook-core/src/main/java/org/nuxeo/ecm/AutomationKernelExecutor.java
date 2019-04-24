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

	@Param(name = "path", required = false)
	protected String path;

	
	
	@OperationMethod
	public String run(String content) throws Exception {
		
		PreProcessingResult preProcessedCode = preprocessCode(content);
		
		AutomationScriptingService service = Framework.getService(AutomationScriptingService.class);
		InputStream script = IOUtils.toInputStream(preProcessedCode.code);

		long t0 = System.currentTimeMillis();
		Object result = service.get(ctx).run(script);
		long t1 = System.currentTimeMillis();

		Map<String, Object> params = new HashMap<>();
		params.put("t", t1 - t0);

		
		if (preProcessedCode.opId!=null) {
			result = preProcessedCode;
		}
		
		return renderResult(result, params);
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
        	
        	ScriptingOperationDescriptor desc = new TemporaryScriptingOperationDescriptor(opId, code);
             	
        	String componentId = "org.nuxeo.automation.scripting.internals.AutomationScriptingComponent";
        	AutomationScriptingComponent component = (AutomationScriptingComponent) Framework.getRuntime().getComponent(componentId);
        	
        	
        	// register
        	//component.unregisterContribution(desc, "operation", null);
        	component.registerContribution(desc, "operation", null);
        	result.opId = opId;        	        	        	
        }       	
        
        result.code=code;        
		return result;
	}
	
	protected String renderResult(Object result, Map<String, Object> params) throws RenderingException {

		FMRenderer renderer = new FMRenderer();
		params.put("result", result);

		if (result instanceof DocumentModel) {
			params.put("doc", result);
			return renderer.render("notebook/doc.ftl", params);
		} else if (result instanceof DocumentModelList) {
			params.put("docs", result);
			return renderer.render("notebook/docs.ftl", params);
		} else if (result instanceof PreProcessingResult) {		
			params.put("opId", ((PreProcessingResult)result).opId);
			return renderer.render("notebook/opregister.ftl", params);
		} else {
			return renderer.render("notebook/default.ftl", params);
		}

	}

}
