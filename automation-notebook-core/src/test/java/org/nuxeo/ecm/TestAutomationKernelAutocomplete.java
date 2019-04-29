package org.nuxeo.ecm;

import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.automation.AutomationService;
import org.nuxeo.ecm.automation.OperationContext;
import org.nuxeo.ecm.automation.interactive.op.AutomationKernelAutocomplete;
import org.nuxeo.ecm.automation.test.AutomationFeature;
import org.nuxeo.ecm.core.api.CoreSession;
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
public class TestAutomationKernelAutocomplete {

	@Inject
	protected CoreSession session;

	@Inject
	protected AutomationService automationService;

	protected String loadScript(String name) throws Exception {
		return IOUtils.toString(this.getClass().getClassLoader().getResourceAsStream(name));
	}

	@Test
	public void shouldProvideSuggestions() throws Exception {
		OperationContext ctx = new OperationContext(session);

		ctx.setInput(loadScript("testAutocomplete.js"));
		Map<String, String> params = new HashMap<>();
		params.put("prefix", "Doc");

		String suggestions = (String) automationService.run(ctx, AutomationKernelAutocomplete.ID, params);

		assertTrue(suggestions.contains("Docfun"));
		assertTrue(suggestions.contains("DocA"));
		assertTrue(suggestions.contains("Document.Create"));

	}

	@Test
	public void shouldProvideSuggestionsWithDotedNames() throws Exception {
		OperationContext ctx = new OperationContext(session);

		ctx.setInput(loadScript("testAutocomplete.js"));
		Map<String, String> params = new HashMap<>();
		params.put("prefix", "Document.");

		String suggestions = (String) automationService.run(ctx, AutomationKernelAutocomplete.ID, params);

		assertTrue(suggestions.contains("Document.Query"));
		assertTrue(suggestions.contains("Document.Create"));

	}

}
