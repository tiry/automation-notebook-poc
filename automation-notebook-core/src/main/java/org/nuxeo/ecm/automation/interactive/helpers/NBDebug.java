package org.nuxeo.ecm.automation.interactive.helpers;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.nuxeo.ecm.automation.context.ContextHelper;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventContext;

public class NBDebug  implements ContextHelper {
	
	Map<String, Object> ctx;
		
	public void init(Map<String, Object> ctx) {
		
		this.ctx = ctx;
		
	}
	
	public void dumpCtx() throws IOException {		
		dumpCtx(this.ctx);
	}

	
	public void dumpCtx(Map<String, Object> ctx) throws IOException {
		
		NoteBookConsole.debuglog("Dumping ctx");
		if (ctx==null) {
			NoteBookConsole.debuglog("NULL ctx");
			return;
		}
		StringBuffer sb = new StringBuffer();
		for (String k : ctx.keySet()) {
			sb.append(k);
			sb.append(" : ");
			Object ob = ctx.get(k);			
			sb.append(ob==null ? " null " : "(" + ob.getClass().getName() + ")");
			if (ob!=null) {
				if (ob instanceof Event) {													
					String eventName = ((Event)ob).getName();
					EventContext eCtx = ((Event)ob).getContext();
					
					sb.append("   \neventName: " + eventName);
					sb.append("   \nproperties: ");
					sb.append(NoteBookConsole.debugAsJson(eCtx.getProperties()));
				} else {
					sb.append(NoteBookConsole.debugAsJson(ob));
				}
			}
			sb.append("\n");
		}
		
		
		//System.out.println("****" + sb.toString());
		NoteBookConsole.debuglog(sb.toString());
	}

}
