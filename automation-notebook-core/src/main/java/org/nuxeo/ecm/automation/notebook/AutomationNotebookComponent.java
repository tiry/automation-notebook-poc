package org.nuxeo.ecm.automation.notebook;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.nuxeo.ecm.automation.interactive.NBScriptExecutor;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.runtime.model.ComponentContext;
import org.nuxeo.runtime.model.ComponentInstance;
import org.nuxeo.runtime.model.DefaultComponent;
import org.nuxeo.runtime.model.Descriptor;
import org.nuxeo.runtime.model.RuntimeContext;

public class AutomationNotebookComponent extends DefaultComponent implements AutomationNotebookService {

	public static final String NB_XP = "notebooks";


	@Override
	public void registerContribution(Object contribution, String xp, ComponentInstance component) {
		if (contribution instanceof AutomationNotebookDescriptor && NB_XP.equals(xp)) {
			AutomationNotebookDescriptor nbdesc = (AutomationNotebookDescriptor) contribution;
			register(xp, nbdesc);
		}
	}

	@Override
	public void start(ComponentContext context) {
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
	
	public String validateNotebook(CoreSession session, String name) throws Exception {
		AutomationNotebookDescriptor desc = getDescriptor(NB_XP, name);
		NBScriptExecutor executor = new NBScriptExecutor();
		
		StringBuffer sb = new StringBuffer();
		
		sb.append("{");
		
		for (PreProcessor.Result code: desc.getSetupCells()) {
			String json = executor.run(session, code.getCode(), "json");
			sb.append("\"setup\":" + json);
		}

		for (PreProcessor.Result code: desc.getTestCells()) {
			String json = executor.run(session, code.getCode(), "json");
			sb.append(",\"" + code.getId() + "\":" + json);
		}
		
		sb.append("}");
						
		return sb.toString();
	}
	
}
