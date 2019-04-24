package org.nuxeo.ecm;

import java.io.InputStream;

import javax.inject.Inject;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.automation.scripting.api.AutomationScriptingService;
import org.nuxeo.ecm.automation.AutomationService;
import org.nuxeo.ecm.automation.OperationContext;
import org.nuxeo.ecm.automation.OperationException;
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

}
