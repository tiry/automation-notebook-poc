package org.nuxeo.ecm;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.automation.AutomationService;
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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(FeaturesRunner.class)
@Features(AutomationFeature.class)
@RepositoryConfig(init = DefaultRepositoryInit.class, cleanup = Granularity.METHOD)
@Deploy({ "org.nuxeo.ecm.automation-notebook-core",
		"org.nuxeo.ecm.automation-notebook-core-test:notebook-contrib.xml" })
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

		assertEquals(5, cells.size());
		assertEquals(2, nb.getSetupCells().size());
		assertEquals(1, nb.getOpCells().size());
		assertEquals(2, nb.getTestCells().size());

		assertEquals("SimpleTests", nb.getTestCells().get(0).getId());
		assertEquals("NBTest.ExampleOperation", nb.getOpCells().get(0).getId());

	}

	@Test
	public void validateNoteBook() throws Exception {
		AutomationNotebookService ans = Framework.getService(AutomationNotebookService.class);
		ObjectMapper objectMapper = new ObjectMapper();

		String json = ans.validateNotebook(session, "testNB");
		// System.out.println(json);

		JsonNode validation = objectMapper.readTree(json);
		assertTrue(validation.get("summary").get("success").asBoolean());
		assertEquals(0, validation.get("summary").get("failedCount").asInt());
		assertEquals(4, validation.get("summary").get("assertCount").asInt());
		assertEquals(0, validation.get("summary").get("errorCount").asInt());

	}

	@Test
	public void validateNoteBookShouldFail() throws Exception {
		AutomationNotebookService ans = Framework.getService(AutomationNotebookService.class);
		ObjectMapper objectMapper = new ObjectMapper();

		String json = ans.validateNotebook(session, "testNBFail");
		// System.out.println(json);

		JsonNode validation = objectMapper.readTree(json);
		assertFalse(validation.get("summary").get("success").asBoolean());
		assertEquals(1, validation.get("summary").get("failedCount").asInt());
		assertEquals(4, validation.get("summary").get("assertCount").asInt());
		assertEquals(0, validation.get("summary").get("errorCount").asInt());

	}

	@Test
	public void validateNoteBookShouldErr() throws Exception {
		AutomationNotebookService ans = Framework.getService(AutomationNotebookService.class);
		ObjectMapper objectMapper = new ObjectMapper();

		String json = ans.validateNotebook(session, "testNBErr");
		// System.out.println(json);

		JsonNode validation = objectMapper.readTree(json);
		assertFalse(validation.get("summary").get("success").asBoolean());
		assertEquals(0, validation.get("summary").get("failedCount").asInt());
		assertEquals(3, validation.get("summary").get("assertCount").asInt());
		assertEquals(1, validation.get("summary").get("errorCount").asInt());

	}

}
