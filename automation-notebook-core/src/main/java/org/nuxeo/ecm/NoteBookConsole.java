package org.nuxeo.ecm;

import java.util.ArrayList;
import java.util.List;

import org.nuxeo.automation.scripting.helper.Console;

public class NoteBookConsole extends Console {

	protected static ThreadLocal<List<String>> memoryLog = new ThreadLocal<>();	
		
	public static List<String> initMemoryLog() {
		memoryLog.set(new ArrayList<>());
		return getMemoryLog();
	}

	public static void cleanMemoryLog() {
		memoryLog.remove();
	}

	public static List<String> getMemoryLog() {
		return memoryLog.get();
	}
	
	protected void log(String level, String message) {
		List<String> msgs = memoryLog.get();
		if (msgs==null) {
			msgs = initMemoryLog();
		}
		
		if ("ERR".equals(level)) {
			message = "<div style=\"color:red\">" + message;
		}
		else if ("WARN".equals(level)) {
			message = "<div style=\"color:orange\">" + message;
		}
		else if ("INFO".equals(level)) {
			message = "<div style=\"color:black\">" + message;
		}
		else if ("TRC".equals(level)) {
			message = "<div style=\"color:#999999\">" + message;
		}
		message = message + "</div>";		
		msgs.add(message);
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
}
