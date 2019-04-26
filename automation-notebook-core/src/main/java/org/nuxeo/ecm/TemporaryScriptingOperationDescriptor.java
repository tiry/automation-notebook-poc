package org.nuxeo.ecm;

import org.nuxeo.automation.scripting.internals.ScriptingOperationDescriptor;

public class TemporaryScriptingOperationDescriptor extends ScriptingOperationDescriptor {

	public TemporaryScriptingOperationDescriptor(String id, String code) {
		super();
		this.id=id;
		this.source=code;	
		this.aliases = new String[0];
	}

	@Override
	public int hashCode() {
		return this.id.hashCode();
	}

	@Override
	public boolean equals(Object obj) {		
		TemporaryScriptingOperationDescriptor other = (TemporaryScriptingOperationDescriptor) obj;		
		if (obj==null) {
			return false;
		}		
		return other.id.equals(this.id);
	}

	@Override
	public String toString() {
		return "id:" + id + "\n" + source;
	}
	
	
	
}
