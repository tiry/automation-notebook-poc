package org.nuxeo.ecm;

import org.nuxeo.automation.scripting.internals.ScriptingOperationDescriptor;

public class TemporaryScriptingOperationDescriptor extends ScriptingOperationDescriptor {

	public TemporaryScriptingOperationDescriptor(String id, String code) {
		super();
		this.id=id;
		this.source=code;	
		this.aliases = new String[0];
	}
	
}
