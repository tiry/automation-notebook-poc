package org.nuxeo.ecm;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.automation.scripting.api.AutomationScriptingService;
import org.nuxeo.ecm.automation.AutomationService;
import org.nuxeo.ecm.automation.OperationContext;
import org.nuxeo.ecm.automation.OperationException;
import org.nuxeo.ecm.automation.OperationType;
import org.nuxeo.ecm.automation.test.AutomationFeature;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.test.DefaultRepositoryInit;
import org.nuxeo.ecm.core.test.annotations.Granularity;
import org.nuxeo.ecm.core.test.annotations.RepositoryConfig;
import org.nuxeo.runtime.api.Framework;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

@RunWith(FeaturesRunner.class)
@Features(AutomationFeature.class)
@RepositoryConfig(init = DefaultRepositoryInit.class, cleanup = Granularity.METHOD)
public class TestAutomationScripting {

    @Inject
    protected CoreSession session;

    @Inject
    AutomationScriptingService scripting;

    @Inject
    protected AutomationService automationService;

    @Test
    public void shouldCallScript() throws OperationException {
        OperationContext ctx = new OperationContext(session);

        String content ="function run(input, params) {\n" + 
        		"\n" + 
        		"    var root = Repository.GetDocument(null, {\n" + 
        		"        \"value\" : \"/\"\n" + 
        		"    });\n" + 
        		"    return root;    \n" + 
        		"}\n" + 
        		"run()";
        
    	AutomationScriptingService service = Framework.getService(AutomationScriptingService.class);    	
    	InputStream script = IOUtils.toInputStream(content);
    	
    	Object result = service.get(ctx).run(script);
        
    }

    @Test
    public void shouldCallScriptWithList() throws OperationException {
        OperationContext ctx = new OperationContext(session);

        String content ="function run(input, params) {\n" + 
        		"\n" + 
        		"    return Document.Query(null,{\"query\" : \"select * from Document\"})\n" + 
        		"}\n" + 
        		"run()";
        
    	AutomationScriptingService service = Framework.getService(AutomationScriptingService.class);    	
    	InputStream script = IOUtils.toInputStream(content);
    	
    	Object result = service.get(ctx).run(script);
        
    }

    @Test
    public void shouldRegisterAndUpdateScript() throws Exception {
    
    	String opId = "Scripting.fromJUnit";
        OperationContext ctx = new OperationContext(session);
        Map<String, Object> params = new HashMap<>();
        
        // check op does not exist
    	assertFalse(automationService.hasOperation(opId));
    	
    	// register op v1
    	String oldcode = "function run(input,params){ return 'v1';};";
    	AutomationHelper.register(opId, oldcode);
    	
    	// check op v1
    	assertTrue(automationService.hasOperation(opId));    	
        String result = (String) automationService.run(ctx, opId, params);
        assertEquals("v1", result);
        
    	// register op v2
        String newcode = "function run(input,params){ return 'v2';};";
    	AutomationHelper.register(opId, newcode);
    	
    	// check op v2
    	assertTrue(automationService.hasOperation(opId));    	
        result = (String) automationService.run(ctx, opId, params);
        assertEquals("v2", result);                    
    }
}
