package org.nuxeo.ecm.automation.notebook;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.nuxeo.ecm.automation.interactive.NBScriptExecutor;
import org.nuxeo.ecm.automation.interactive.NBScriptExecutor.ExecutionResult;
import org.nuxeo.ecm.automation.interactive.helpers.AssertEntry;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.webengine.JsonFactoryManager;
import org.nuxeo.runtime.api.Framework;
import org.nuxeo.runtime.model.ComponentContext;
import org.nuxeo.runtime.model.ComponentInstance;
import org.nuxeo.runtime.model.DefaultComponent;
import org.nuxeo.runtime.model.Descriptor;
import org.nuxeo.runtime.model.RuntimeContext;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;

public class AutomationNotebookComponent extends DefaultComponent implements AutomationNotebookService {

	public static final String NB_XP = "notebooks";

	protected JsonFactory jsonFactory; 

	@Override
	public void registerContribution(Object contribution, String xp, ComponentInstance component) {
		if (contribution instanceof AutomationNotebookDescriptor && NB_XP.equals(xp)) {
			AutomationNotebookDescriptor nbdesc = (AutomationNotebookDescriptor) contribution;
			register(xp, nbdesc);
		}
	}

	@Override
	public void start(ComponentContext context) {
		super.start(context);
		List<Descriptor> descs = getDescriptors(NB_XP);
		for (Descriptor desc : descs) {
			AutomationNotebookDescriptor nbdesc = (AutomationNotebookDescriptor) desc;

			try {
				loadNotebook(nbdesc, context.getRuntimeContext());
			} catch (IOException e) {
				throw new NuxeoException("Unable to load Notebook", e);
			}
		}				
	}
	
	protected void loadNotebook(AutomationNotebookDescriptor desc, RuntimeContext ctx) throws IOException {
		if (ctx!=null) {
			desc.context = ctx;
		}
		URL url = desc.context.getLocalResource(desc.src);
		if (url == null) {
			// try asking the class loader
			url = desc.context.getResource(desc.src);
		}
		
		if (url!=null) {
			desc.json = IOUtils.toString(url,"UTF-8");
			desc.parse();
		}
	}

	public AutomationNotebookDescriptor getNotebook(String name) {
		AutomationNotebookDescriptor desc = getDescriptor(NB_XP, name);
		return desc;
	}
	
	protected JsonFactory getFactory() {
		if (jsonFactory==null) {
	        JsonFactoryManager jsonFactoryManager = Framework.getService(JsonFactoryManager.class);
	        jsonFactory = jsonFactoryManager.getJsonFactory();
		}
		return jsonFactory;
	}
	
	public String validateNotebook(CoreSession session, String name) throws Exception {
		AutomationNotebookDescriptor desc = getDescriptor(NB_XP, name);
		NBScriptExecutor executor = new NBScriptExecutor();
	
		StringWriter writer = new StringWriter();
		JsonGenerator jg = getFactory().createGenerator(writer);
				
	
		StringBuffer sb = new StringBuffer();

		jg.writeStartObject();

		List<AssertEntry> failed = new ArrayList<>();
		int nbAsserts = 0;
		int nbErrors = 0;
		
		jg.writeObjectFieldStart("executionStack");
		
		jg.writeArrayFieldStart("setup");		
		for (PreProcessor.Result code: desc.getSetupCells()) {
			
			ExecutionResult result = executor.run(session, code.getCode());
			if (result.getOutcome() instanceof NuxeoException) {
				nbErrors++;
			}
			List<AssertEntry> asserts = (List<AssertEntry>) result.getParams().get(ExecutionResult.ASSERTS_KEY);
			for (AssertEntry assertEntry : asserts) {
				nbAsserts++;
				if (!assertEntry.isSuccess()) {
					failed.add(assertEntry);
				}
			}

			String json = executor.render(result, "json");
			jg.writeRawValue(json);			
		}
		jg.writeEndArray();
		
		jg.writeObjectFieldStart("tests");
		
		for (PreProcessor.Result code: desc.getTestCells()) {
			
			ExecutionResult result = executor.run(session, code.getCode());
			if (result.getOutcome() instanceof NuxeoException) {
				nbErrors++;
			}
			List<AssertEntry> asserts = (List<AssertEntry>) result.getParams().get(ExecutionResult.ASSERTS_KEY);
			for (AssertEntry assertEntry : asserts) {
				nbAsserts++;
				if (!assertEntry.isSuccess()) {
					failed.add(assertEntry);
				}
			}

			String json = executor.render(result, "json");

			jg.writeFieldName(code.getId());
			jg.writeRawValue(json);			
		}
		jg.writeEndObject();
		
		jg.writeEndObject();
		
		jg.writeObjectFieldStart("summary");
		
		jg.writeNumberField("assertCount", nbAsserts);
		jg.writeNumberField("failedCount", failed.size());
		jg.writeNumberField("errorCount", nbErrors);
		
		if (failed.size()==0 && nbErrors==0) {
			jg.writeBooleanField("success", true);
		} else {
			jg.writeBooleanField("success", false);
		}
		
		jg.writeEndObject();
		
		jg.writeEndObject();
		jg.flush();
		return writer.toString();
	}
	
}
