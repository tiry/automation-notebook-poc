package org.nuxeo.ecm.automation.interactive.helpers;

public class LogEntry {

	protected long ts;
	
	protected final String level;
	
	protected final String message;

	protected int thread = 0;
	
	public LogEntry(String level, String message) {
		this.level = level;
		this.message = message;
		this.ts = System.currentTimeMillis();
	}
	
	public String getLevel() {
		return level;
	}

	public String getMessage() {
		return message;
	}
	
	public long getTS() {
		return ts;
	}

	public long getTs() {
		return ts;
	}

	public int getThread() {
		return thread;
	}
	
	
}
