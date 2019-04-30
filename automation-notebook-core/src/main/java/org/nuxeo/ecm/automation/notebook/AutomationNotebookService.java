package org.nuxeo.ecm.automation.notebook;

import org.nuxeo.ecm.core.api.CoreSession;

public interface AutomationNotebookService {

	AutomationNotebookDescriptor getNotebook(String name);
	
	String validateNotebook(CoreSession session, String name) throws Exception;
	
}
