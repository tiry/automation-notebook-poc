package org.nuxeo.ecm.automation.interactive.helpers;

import java.util.ArrayList;
import java.util.List;

import org.nuxeo.ecm.automation.context.ContextHelper;

public class AssertHelper implements ContextHelper {
	
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
	
	protected boolean record(boolean result, String title) {
		String logEntry = "<div ";
		
		if (result) {
			logEntry = logEntry + " style='background-color:#99FF99; margin:1px;font-weight:bold;'> PASS: ";
		} else {
			logEntry = logEntry + " style='background-color:#FF4444; margin:1px;font-weight:bold;'  > FAIL: ";
		}
		if (title!=null) {
			logEntry = logEntry + title;
		}
		logEntry = logEntry + "</div>";
			
		List<String> msgs = memoryLog.get();
		if (msgs==null) {
			msgs = initMemoryLog();
		}
		msgs.add(logEntry);
		
		return result;
	}
	
	public boolean assertEquals(Object expected, Object actual, String title) {
		return record(expected.equals(actual),title); 
	}

	public boolean assertTrue(boolean condition) {
		int idx = 0;
		if(memoryLog.get()!=null) {
			idx = memoryLog.get().size();
		}
		return assertTrue(condition, "Assertion #" + (idx+1));
	}

	public boolean assertTrue(boolean condition, String title) {
		return record(condition, title);
	}
	
	public String sayHello() {
		return "Hello";
	}
}
