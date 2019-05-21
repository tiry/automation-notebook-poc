package org.nuxeo.ecm;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.automation.AutomationService;
import org.nuxeo.ecm.automation.OperationContext;
import org.nuxeo.ecm.automation.OperationException;
import org.nuxeo.ecm.automation.OperationType;
import org.nuxeo.ecm.automation.interactive.op.AutomationKernelExecutor;
import org.nuxeo.ecm.automation.test.AutomationFeature;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.test.DefaultRepositoryInit;
import org.nuxeo.ecm.core.test.annotations.Granularity;
import org.nuxeo.ecm.core.test.annotations.RepositoryConfig;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(FeaturesRunner.class)
@Features(AutomationFeature.class)
@RepositoryConfig(init = DefaultRepositoryInit.class, cleanup = Granularity.METHOD)
@Deploy("org.nuxeo.ecm.webengine.core:OSGI-INF/json-service.xml")
@Deploy("org.nuxeo.ecm.automation-notebook-core")
public class TestAutomationKernelExecutor {

    @Inject
    protected CoreSession session;

    @Inject
    protected AutomationService automationService;

    String stupidScript = "function sayyo() {\n" + 
    						"  return 'yo'\n" + 
    					  "}; \n" + 
    					  "sayyo();";

    
    String docScript = "function run(input, params) {\n" + 
						"  var root = Repository.GetDocument(null, {\n" + 
						"        \"value\" : \"/\"\n" + 
						"    });" + 
						"}; \n" + 
						"sayyo();";

    protected String loadScript(String name) throws Exception {
    	return IOUtils.toString(this.getClass().getClassLoader().getResourceAsStream(name));    	
    }
    
    @Test
    public void shouldExecJSAndReturnString() throws OperationException {
        OperationContext ctx = new OperationContext(session);
        ctx.setInput(stupidScript);
        String html = (String) automationService.run(ctx, AutomationKernelExecutor.ID);
        assertTrue(html.contains("yo"));
        assertTrue(html.contains("Execution time"));
    }

    @Test
    public void shouldRaiseError() throws OperationException {
        OperationContext ctx = new OperationContext(session);
        ctx.setInput("IDoNotExist();");
        String html = (String) automationService.run(ctx, AutomationKernelExecutor.ID);

        //System.out.println(html);
        assertTrue(html.contains("Unable to execute Automation Script"));
    }

    @Test
    public void shouldExecJSAndReturnDocRendition() throws Exception {
    	
        OperationContext ctx = new OperationContext(session);

        ctx.setInput(loadScript("docscript.js"));
        String html = (String) automationService.run(ctx, AutomationKernelExecutor.ID);

        assertTrue(html.contains("Domain"));
        assertTrue(html.contains("Execution time"));
        
        //System.out.println(html);
    }

    
    @Test
    public void shouldExecJSAndReturnDocListRendition() throws Exception {
    	
        OperationContext ctx = new OperationContext(session);

        ctx.setInput(loadScript("doclistscript.js"));
        String html = (String) automationService.run(ctx, AutomationKernelExecutor.ID);

        //System.out.println(html);
        assertTrue(html.contains("Execution time"));
        assertTrue(html.contains("Documents"));
        assertTrue(html.contains("/default-domain/workspaces/test"));
        assertTrue(html.contains("/default-domain/templates"));
    }


    @Test
    public void shouldRegisterOperation() throws Exception {
    	
        OperationContext ctx = new OperationContext(session);

        // run the script with annotations
        ctx.setInput(loadScript("opscript.js"));
        String html = (String) automationService.run(ctx, AutomationKernelExecutor.ID);

        System.out.println(html);
        assertTrue(html.contains("Scripting.GetRoot"));
        assertTrue(html.contains("compiled"));
        
        // check that the script was deployed as an operation
        OperationType type = automationService.getOperation("Scripting.GetRoot");
        assertNotNull(type);
        
        // check impl
        DocumentModel doc = (DocumentModel) automationService.run(ctx, "Scripting.GetRoot");
        assertEquals("Domain", doc.getType());
     
        // update 
        ctx.setInput(loadScript("opscript2.js"));
        html = (String) automationService.run(ctx, AutomationKernelExecutor.ID);
        assertTrue(html.contains("Scripting.GetRoot"));
        assertTrue(html.contains("compiled"));
                
        // check impl
        doc = (DocumentModel) automationService.run(ctx, "Scripting.GetRoot");
        // FAIL!
        assertEquals("Root", doc.getType());
     
        
    }
    
    @Test
    public void shouldRegisterAndUpdateOperation() throws Exception {
    	
        OperationContext ctx = new OperationContext(session);
        Map<String, Object> params = new HashMap<>();
        String opId = "Scripting.fromJUnit";
        
        // check that the operation does not exist
        assertFalse(automationService.hasOperation(opId));
        
        // run the script with annotations
        ctx.setInput("@Operation(id = \""+ opId +"\")\nfunction run(input,params){ return 'v1';};");
        String html = (String) automationService.run(ctx, AutomationKernelExecutor.ID);

        assertTrue(html.contains(opId));
        assertTrue(html.contains("compiled"));
        
        // check that the script was deployed as an operation
        assertTrue(automationService.hasOperation(opId));
        OperationType type = automationService.getOperation(opId);
        assertNotNull(type);
        
        // check impl
        ctx = new OperationContext(session);
        String res = (String) automationService.run(ctx, opId,params);
        assertEquals("v1", res);
     
        // update 
        ctx = new OperationContext(session);
        ctx.setInput("@Operation(id = \""+ opId +"\")\nfunction run(input,params){ return 'v2';};");
        html = (String) automationService.run(ctx, AutomationKernelExecutor.ID);
        assertTrue(html.contains(opId));
        assertTrue(html.contains("compiled"));
                
        // check impl
        ctx = new OperationContext(session);
        //ScriptingOperationTypeImpl sopt = (ScriptingOperationTypeImpl) automationService.getOperation(opId);        
        //ScriptingOperationImpl sop = (ScriptingOperationImpl) sopt.newInstance(ctx, params);
        res = (String) automationService.run(ctx, opId, params);
        assertEquals("v2", res);
     
        
    }
    
    @Test
    public void shouldExecuteAsserts() throws Exception {
    	
        OperationContext ctx = new OperationContext(session);

        ctx.setInput(loadScript("asserts.js"));
        String html = (String) automationService.run(ctx, AutomationKernelExecutor.ID);

        assertTrue(html.contains("parrot"));
        assertTrue(html.contains("Assertion #1"));
        assertTrue(html.contains("PASS: Assertion #3"));
        assertTrue(html.contains("FAIL: Stupid"));

    }

    
    @Test
    public void shouldLog() throws Exception {
    	
        OperationContext ctx = new OperationContext(session);

        ctx.setInput(loadScript("logs.js"));
        String html = (String) automationService.run(ctx, AutomationKernelExecutor.ID);
        
        assertTrue(html.contains("This is an information"));
        assertTrue(html.contains("This is a warning"));
        assertTrue(html.contains("DocumentScriptingWrapper"));
        assertTrue(html.contains("dc:modified"));

    }

    @Test
    public void shouldExecJSAndReturnDocListRenditionJSON() throws Exception {
    	
        OperationContext ctx = new OperationContext(session);

        Map<String, Object> params = new HashMap<>();

        params.put("format", "json");
        
        ctx.setInput(loadScript("doclistscriptjson.js"));
        String json = (String) automationService.run(ctx, AutomationKernelExecutor.ID, params);
        
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode payload = objectMapper.readTree(json);
        
        assertTrue(payload.get("exec_time").asLong() > 0);
        assertTrue(payload.get("type").asText().equals("documents"));        
        assertTrue(payload.get("uids").isArray());
        assertTrue(payload.get("asserts").isArray());
        assertTrue(payload.get("logs").isArray());
        
        assertTrue(payload.get("logs").get(0).get("message").asText().equals("Yo"));
        assertTrue(payload.get("asserts").get(0).get("message").asText().equals("MyAssert"));
        //System.out.println(json);        
    }


    @Test
    public void shouldRegisterAndDebugSyncListener() throws Exception {
    	
        OperationContext ctx = new OperationContext(session);

        ctx.setInput(loadScript("eventlistener.js"));
        String html = (String) automationService.run(ctx, AutomationKernelExecutor.ID);        
        assertTrue(html.contains("Scripting.Listener"));
        assertTrue(html.contains("compiled"));

                
        ctx.setInput(loadScript("listenertest.js"));
        html = (String) automationService.run(ctx, AutomationKernelExecutor.ID);
        
        System.out.println(html);
        assertTrue(html.contains("from js listener"));
        assertTrue(html.contains("EventImpl"));
        assertTrue(html.contains("\"newDoc\""));        
    }

    @Test
    public void shouldRegisterAndDebugAsyncListener() throws Exception {
    	
        OperationContext ctx = new OperationContext(session);

        ctx.setInput(loadScript("eventlistener.js"));
        String html = (String) automationService.run(ctx, AutomationKernelExecutor.ID);        
        assertTrue(html.contains("Scripting.Listener"));
        assertTrue(html.contains("compiled"));
                
        ctx.setInput(loadScript("asynclistenertest.js"));
        html = (String) automationService.run(ctx, AutomationKernelExecutor.ID);

        //System.out.println(html);

        // verify that we get all the logs
        assertTrue(html.contains("starting listener testing"));
        assertTrue(html.contains("from js listener"));
        assertTrue(html.contains("EventImpl"));
        assertTrue(html.contains("\"newDoc\""));
        assertTrue(html.contains("end of listener testing"));
        
        // verify that we can get info from an other thread!
        assertTrue(html.contains("[0]"));
        assertTrue(html.contains("[1]"));
        
        
    }

}
