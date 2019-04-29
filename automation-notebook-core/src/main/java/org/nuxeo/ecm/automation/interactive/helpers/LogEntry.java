package org.nuxeo.ecm.automation.interactive.helpers;

public class LogEntry {

	protected final String level;
	
	protected final String message;

	public LogEntry(String level, String message) {
		this.level = level;
		this.message = message;
	}
	
	public String getLevel() {
		return level;
	}

	public String getMessage() {
		return message;
	}
	
}
