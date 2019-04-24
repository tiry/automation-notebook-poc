package org.nuxeo.ecm;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.nuxeo.automation.scripting.api.AutomationScriptingService;
import org.nuxeo.ecm.automation.OperationContext;
import org.nuxeo.ecm.automation.core.Constants;
import org.nuxeo.ecm.automation.core.annotations.Context;
import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
import org.nuxeo.ecm.automation.core.annotations.Param;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.impl.blob.StringBlob;
import org.nuxeo.ecm.platform.rendering.api.RenderingException;
import org.nuxeo.ecm.platform.rendering.fm.FreemarkerEngine;
import org.nuxeo.runtime.api.Framework;

/**
 *
 */
@Operation(id=AutomationKernelExecutor.ID, category=Constants.CAT_DOCUMENT, label="Automation.KernelExecutor", description="Describe here what your operation does.")
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
    	
    	System.out.println(content);
    	
    	AutomationScriptingService service = Framework.getService(AutomationScriptingService.class);
    	
    	InputStream script = IOUtils.toInputStream(content);
    	
    	long t0 = System.currentTimeMillis();
    	
    	Object result = service.get(ctx).run(script);
    	
    	long t1 = System.currentTimeMillis();
    	
    	System.out.println(result);
    	Map<String,Object>params = new HashMap<>();
    	
    	params.put("t", t1-t0);
    	
    	return renderResult(result, params);
    }
    
    protected String renderResult(Object result, Map<String,Object>params) throws RenderingException {
    	
    	FMRenderer renderer = new FMRenderer();
    	
    	params.put("result", result);
    	
    	if (result instanceof DocumentModel) {
    		params.put("doc", result);
    		return renderer.render("notebook/doc.ftl", params);
    	} else {
    		return renderer.render("notebook/default.ftl", params);	
    	}
    	
    }
    
}
