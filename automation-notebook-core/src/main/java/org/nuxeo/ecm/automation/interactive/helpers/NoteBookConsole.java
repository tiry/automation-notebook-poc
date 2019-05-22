package org.nuxeo.ecm.automation.interactive.helpers;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.nuxeo.automation.scripting.helper.Console;
import org.nuxeo.ecm.webengine.JsonFactoryManager;
import org.nuxeo.runtime.api.Framework;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;

public class NoteBookConsole extends Console {
			
	protected static ThreadLocal<String> traceID = new ThreadLocal<>();
	
	protected static ConcurrentHashMap<String, List<LogEntry>> logs = new ConcurrentHashMap<>();
	
	protected static ThreadLocal<List<String>> childrenTraceIds = new ThreadLocal<>();
	
	public static String getTraceId() {
		if (traceID.get()==null) {
			traceID.set(UUID.randomUUID().toString());
		}
		return traceID.get();
	}
	
	public void iniTraceId(String id) {
		if (traceID.get()==null) {
			setTraceId(id);
		}
	}
	
	public static void setTraceId(String id) {
		traceID.set(id);
	}
	
	public static void register(String id) {
		List<String> children = childrenTraceIds.get();
		if (children==null) {
			children = new ArrayList<>();
			childrenTraceIds.set(children);
		}
		children.add(id);		
	}
	
	public static List<LogEntry> getLogs() {
		
		List<LogEntry> result = logs.get(getTraceId());
		int threadId=0;
		if (result==null) {
			result = new ArrayList<>();
		}
		if (childrenTraceIds.get()!=null) {
			for (String child: childrenTraceIds.get()) {
				threadId++;
				List<LogEntry> childLogs = new ArrayList<>(logs.get(child));
				for (LogEntry entry : childLogs) {
					entry.thread=threadId;
					result.add(entry);
				}
				//if (childLogs!=null) {
				//	result.addAll(childLogs);	
				//}						
			}
		}

		// put back in order
		result.sort(new Comparator<LogEntry>() {

			@Override
			public int compare(LogEntry o1, LogEntry o2) {	
				return Long.compare(o1.ts, o2.ts);
			}
		});
		
		if (result.size()>0) {
			long t0=result.get(0).ts;
			for (LogEntry entry : result) {
				entry.ts-=t0;
			}
		}
		
		return result;
	}
	
	public static void cleanLogs() {
		logs.remove(getTraceId());
		if (childrenTraceIds.get()!=null) {
			for (String child: childrenTraceIds.get()) {
				logs.remove(child);		
			}
		}
	}

    protected static String debugAsJson(Object ob) throws IOException {
    	
    	StringWriter writer = new StringWriter();
		JsonGenerator jg = getFactory().createGenerator(writer);
		
		
		jg.writeObject(ob);
		jg.flush();
		
		return ob.getClass().getName() + ":" + writer.toString();
    }

	
	protected static void log(String level, String message) {
		List<LogEntry> msgs = logs.get(getTraceId());
		if (msgs==null) {
			msgs = new ArrayList<>();
			logs.put(getTraceId(), msgs);
		}		
		msgs.add(new LogEntry(level, message));
	}
	
    public void error(String inWhat) {
        super.error(inWhat);
        log("ERR", inWhat);
    }

    public void warn(String inWhat) {
    	super.warn(inWhat);
    	log("WARN", inWhat);
    }

    public void log(String inWhat) {
       	super.info(inWhat);
       	log("INFO", inWhat);
    }

    public void info(String inWhat) {
        super.info(inWhat);
        log("INFO", inWhat);
    }

    public void trace(String inWhat) {
        super.trace(inWhat);
        log("TRC", inWhat);
    }
    
    protected static JsonFactory jsonFactory;

    protected static JsonFactory getFactory() {
		if (jsonFactory==null) {
	        JsonFactoryManager jsonFactoryManager = Framework.getService(JsonFactoryManager.class);
        	jsonFactory = jsonFactoryManager.getJsonFactory();
		}
		return jsonFactory;
	}
    
    public void logAsJson(Object ob) throws IOException {
    	asJson(ob);
    }
    
    public static void asJson(Object ob) throws IOException {
    	
    	StringWriter writer = new StringWriter();
		JsonGenerator jg = getFactory().createGenerator(writer);
		
		
		jg.writeObject(ob);
		jg.flush();
		
		log("INFO",ob.getClass().getName() + ":" + writer.toString());
    }
}
