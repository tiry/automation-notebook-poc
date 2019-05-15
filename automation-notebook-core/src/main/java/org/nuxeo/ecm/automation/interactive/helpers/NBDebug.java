package org.nuxeo.ecm.automation.interactive.helpers;

import java.util.Map;

import org.nuxeo.automation.scripting.internals.AutomationMapper;
import org.nuxeo.ecm.automation.OperationContext;
import org.nuxeo.ecm.automation.context.ContextHelper;

public class NBDebug  implements ContextHelper {
	
	Map<String, Object> ctx;
		
	public void init(Map<String, Object> ctx) {
		this.ctx=ctx;
	}
	
	public void dump() {
		
		StringBuffer sb = new StringBuffer();
		for (String k : ctx.keySet()) {
			sb.append(k);
			sb.append(" : ");
			Object ob = ctx.get(k);			
			sb.append(ob==null ? " null " :ob.toString());
			sb.append("\n");
		}
		
		NoteBookConsole.debuglog("Dumping ctx");
		NoteBookConsole.debuglog(sb.toString());
	}

}
