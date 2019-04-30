package org.nuxeo.ecm;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.automation.AutomationService;
import org.nuxeo.ecm.automation.OperationContext;
import org.nuxeo.ecm.automation.interactive.op.AutomationKernelAutocomplete;
import org.nuxeo.ecm.automation.notebook.AutomationNotebookDescriptor;
import org.nuxeo.ecm.automation.notebook.AutomationNotebookService;
import org.nuxeo.ecm.automation.notebook.PreProcessor;
import org.nuxeo.ecm.automation.test.AutomationFeature;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.test.DefaultRepositoryInit;
import org.nuxeo.ecm.core.test.annotations.Granularity;
import org.nuxeo.ecm.core.test.annotations.RepositoryConfig;
import org.nuxeo.runtime.api.Framework;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

@RunWith(FeaturesRunner.class)
@Features(AutomationFeature.class)
@RepositoryConfig(init = DefaultRepositoryInit.class, cleanup = Granularity.METHOD)
@Deploy({"org.nuxeo.ecm.automation-notebook-core", "org.nuxeo.ecm.automation-notebook-core-test:notebook-contrib.xml"})
public class TestAutomationNotebook {

	@Inject
	protected CoreSession session;

	@Inject
	protected AutomationService automationService;

	@Test
	public void verifyServiceExists() throws Exception {
		assertNotNull(Framework.getService(AutomationNotebookService.class));		
	}

	@Test
	public void verifyContribution() throws Exception {
		AutomationNotebookService ans = Framework.getService(AutomationNotebookService.class);
		
		AutomationNotebookDescriptor nb = ans.getNotebook("testNB");
		assertNotNull(nb);
		
		List<PreProcessor.Result> cells = nb.getCells();
		
		assertEquals(3, cells.size());
		assertEquals(1, nb.getSetupCells().size());
		assertEquals(1, nb.getOpCells().size());
		assertEquals(1, nb.getTestCells().size());
		
		assertEquals("SimpleTest", nb.getTestCells().get(0).getId());
		assertEquals("NBTest.ExampleOperation", nb.getOpCells().get(0).getId());
		
	}
	
	@Test
	public void validateNoteBook() throws Exception {
		AutomationNotebookService ans = Framework.getService(AutomationNotebookService.class);
		
		String json = ans.validateNotebook(session, "testNB");
		
		System.out.println(json);		
		
	}

}
