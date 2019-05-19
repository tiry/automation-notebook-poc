package org.nuxeo.ecm.automation.interactive.helpers;

import java.util.ArrayList;
import java.util.List;

import org.nuxeo.ecm.automation.context.ContextHelper;
import org.nuxeo.ecm.automation.core.events.EventHandler;
import org.nuxeo.ecm.automation.core.events.EventHandlerRegistry;
import org.nuxeo.ecm.core.event.EventService;
import org.nuxeo.runtime.api.Framework;
import org.nuxeo.runtime.transaction.TransactionHelper;

public class Simulator implements ContextHelper {
	
	public static final String ID = "simulator";
	
	protected List<EventHandler> syncListeners = new ArrayList<>();
	protected List<EventHandler> asyncListeners = new ArrayList<>();	
	
	public void registerListener(String eventName, String script, boolean sync) {
	
		EventHandler descriptor = new EventHandler(eventName, script);		
		EventHandlerRegistry registry = Framework.getService(EventHandlerRegistry.class);
		
		if (sync) {
			registry.putEventHandler(descriptor);
			syncListeners.add(descriptor);
		} else {
			registry.putPostCommitEventHandler(descriptor);
			asyncListeners.add(descriptor);
			// hook on console for the target listener
			NoteBookConsole.register(script);
		}
	}
	
	public void waitForAsync() {
		TransactionHelper.commitOrRollbackTransaction();
		
		Framework.getService(EventService.class).waitForAsyncCompletion();
		
		TransactionHelper.startTransaction();
	}	
	
	public void simulateEvent(String eventName) {
		
	}
	
	public void cleanup() {
		
		EventHandlerRegistry registry = Framework.getService(EventHandlerRegistry.class);		
		for (EventHandler listener : syncListeners) {
			registry.removeEventHandler(listener);
		}
		for (EventHandler listener : asyncListeners) {
			registry.removePostCommitEventHandler(listener);
		}
		
	}
	
	
}
