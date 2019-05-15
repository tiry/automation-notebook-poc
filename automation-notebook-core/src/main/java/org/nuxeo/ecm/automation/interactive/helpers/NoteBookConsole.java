package org.nuxeo.ecm.automation.interactive.helpers;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.nuxeo.automation.scripting.helper.Console;
import org.nuxeo.ecm.webengine.JsonFactoryManager;
import org.nuxeo.runtime.api.Framework;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;

public class NoteBookConsole extends Console {

	protected static ThreadLocal<List<LogEntry>> memoryLog = new ThreadLocal<>();	
		
	public static List<LogEntry> initMemoryLog() {
		memoryLog.set(new ArrayList<>());
		return getMemoryLog();
	}

	public static void cleanMemoryLog() {
		memoryLog.remove();
	}

	public static List<LogEntry> getMemoryLog() {
		return new ArrayList(memoryLog.get());
	}
	
	protected static void debuglog(String message) {
		List<LogEntry> msgs = memoryLog.get();
		if (msgs==null) {
			msgs = initMemoryLog();
		}		
		msgs.add(new LogEntry("INFO", message));		
	}
	
	protected void log(String level, String message) {
		List<LogEntry> msgs = memoryLog.get();
		if (msgs==null) {
			msgs = initMemoryLog();
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
    
    protected JsonFactory jsonFactory;

    protected JsonFactory getFactory() {
		if (jsonFactory==null) {
	        JsonFactoryManager jsonFactoryManager = Framework.getService(JsonFactoryManager.class);
        	jsonFactory = jsonFactoryManager.getJsonFactory();
		}
		return jsonFactory;
	}
    
    public void asJson(Object ob) throws IOException {
    	
    	StringWriter writer = new StringWriter();
		JsonGenerator jg = getFactory().createGenerator(writer);
		
		
		jg.writeObject(ob);
		jg.flush();
		
		this.log(ob.getClass().getName() + ":" + writer.toString());
    }
}
