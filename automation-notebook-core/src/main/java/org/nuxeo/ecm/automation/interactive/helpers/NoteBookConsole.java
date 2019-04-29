package org.nuxeo.ecm.automation.interactive.helpers;

import java.util.ArrayList;
import java.util.List;

import org.nuxeo.automation.scripting.helper.Console;

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
		return memoryLog.get();
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
}
