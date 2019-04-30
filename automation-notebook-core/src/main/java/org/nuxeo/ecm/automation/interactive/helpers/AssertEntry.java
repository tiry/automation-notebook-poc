package org.nuxeo.ecm.automation.interactive.helpers;

public class AssertEntry {

	protected final boolean success;
	
	protected final String message;

	public AssertEntry(boolean success, String message) {
		this.success=success;
		this.message = message;
	}
	
	public boolean isSuccess() {
		return success;
	}
	
	public String value() {
		if (success) {
			return "true";
		}
		return "false";
	}

	public String getMessage() {
		return message;
	}
	
}
