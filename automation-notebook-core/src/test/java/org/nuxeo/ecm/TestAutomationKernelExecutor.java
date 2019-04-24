package org.nuxeo.ecm;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import javax.inject.Inject;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.automation.AutomationService;
import org.nuxeo.ecm.automation.OperationContext;
import org.nuxeo.ecm.automation.OperationException;
import org.nuxeo.ecm.automation.OperationType;
import org.nuxeo.ecm.automation.test.AutomationFeature;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.test.DefaultRepositoryInit;
import org.nuxeo.ecm.core.test.annotations.Granularity;
import org.nuxeo.ecm.core.test.annotations.RepositoryConfig;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

@RunWith(FeaturesRunner.class)
@Features(AutomationFeature.class)
@RepositoryConfig(init = DefaultRepositoryInit.class, cleanup = Granularity.METHOD)
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
    public void shouldExecJSAndReturnDocRendition() throws Exception {
    	
        OperationContext ctx = new OperationContext(session);

        ctx.setInput(loadScript("docscript.js"));
        String html = (String) automationService.run(ctx, AutomationKernelExecutor.ID);

        System.out.println(html);
        assertTrue(html.contains("Domain"));
        assertTrue(html.contains("Execution time"));
    }

    
    @Test
    public void shouldExecJSAndReturnDocListRendition() throws Exception {
    	
        OperationContext ctx = new OperationContext(session);

        ctx.setInput(loadScript("doclistscript.js"));
        String html = (String) automationService.run(ctx, AutomationKernelExecutor.ID);

        System.out.println(html);
        assertTrue(html.contains("Execution time"));
    }


    @Test
    public void shouldRegisterOperation() throws Exception {
    	
        OperationContext ctx = new OperationContext(session);

        // run the script with annotations
        ctx.setInput(loadScript("opscript.js"));
        String html = (String) automationService.run(ctx, AutomationKernelExecutor.ID);

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
        assertEquals("Root", doc.getType());
     
        
    }


}
