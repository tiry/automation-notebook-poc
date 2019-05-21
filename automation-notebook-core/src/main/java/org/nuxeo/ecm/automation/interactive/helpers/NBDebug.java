package org.nuxeo.ecm.automation.interactive.helpers;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.nuxeo.ecm.automation.context.ContextHelper;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventContext;
import org.nuxeo.ecm.core.event.impl.DocumentEventContext;

import jdk.nashorn.api.scripting.ScriptObjectMirror;

public class NBDebug  implements ContextHelper {
	
	Map<String, Object> ctx;
		
	public void init(Map<String, Object> ctx) {
		
		this.ctx = ctx;
		
	}
	
	public void dumpCtx() throws IOException {		
		dumpCtx(this.ctx);
	}

	
	public void dumpCtx(Map<String, Object> ctx) throws IOException {
		
		
		NoteBookConsole.log("INFO","Dumping 'ctx' variable");
		if (ctx==null) {
			NoteBookConsole.log("INFO","NULL ctx");
			return;
		}
		StringBuffer sb = new StringBuffer();
		sb.append("Execution Context: { ");
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
					if (eCtx instanceof DocumentEventContext) {
					
						DocumentModel srcDoc = ((DocumentEventContext)(eCtx)).getSourceDocument();
						DocumentRef dstRef = ((DocumentEventContext)(eCtx)).getDestination();
						
						sb.append("   \nsrc: " + ((srcDoc==null)? "NULL": srcDoc.toString()));
						sb.append("   \ndst: " + ((dstRef==null)? "NULL": dstRef.toString()));
						
						
					} else {
						sb.append("\n[");
						for (Object arg:eCtx.getArguments()) {
							sb.append(arg==null ? "Null" :arg.toString());
							sb.append(" , ");
						}
						sb.append("]");
					}
					
				} else {
					sb.append(NoteBookConsole.debugAsJson(ob));
				}
			}
			sb.append("\n");
		}
		sb.append("}");
		
		//System.out.println("****" + sb.toString());
		NoteBookConsole.log("INFO", sb.toString());
	}
	
	public void introspect(Object ob) {

		StringBuffer sb = new StringBuffer();
		sb.append("\n Introspecting object ");
		sb.append(ob.getClass().getName());
		if (ob instanceof ScriptObjectMirror) {
			for (Entry<String, Object> entry:((ScriptObjectMirror) ob).entrySet()) {
				sb.append("\n " + entry.getKey());
				sb.append(": " + entry.getValue());				
			}
		} else {	
			sb.append("\n Methods");
			for (Method m : ob.getClass().getMethods()) {
				sb.append("\n  ");
				sb.append(m.getName());
				sb.append("()");			
			}
			sb.append("\n Fields");
			for (Field f : ob.getClass().getFields()) {
				sb.append("\n  ");
				sb.append(f.getName());
			}
		}
		
		NoteBookConsole.log("INFO", sb.toString());
	}

}
