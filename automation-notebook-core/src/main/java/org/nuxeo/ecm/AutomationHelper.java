package org.nuxeo.ecm;

import java.util.ArrayList;
import java.util.List;

import org.nuxeo.automation.scripting.internals.AutomationScriptingComponent;
import org.nuxeo.automation.scripting.internals.ScriptingOperationDescriptor;
import org.nuxeo.ecm.automation.AutomationAdmin;
import org.nuxeo.ecm.automation.AutomationService;
import org.nuxeo.runtime.api.Framework;
import org.nuxeo.runtime.model.ComponentInstance;

public class AutomationHelper {

	public static final String CID = "org.nuxeo.automation.scripting.internals.AutomationScriptingComponent";

	protected static List<ScriptingOperationDescriptor> descs = new ArrayList<>();

	public static void register(String opId, String code) {

		ScriptingOperationDescriptor desc = new TemporaryScriptingOperationDescriptor(opId, code);

		AutomationScriptingComponent component = (AutomationScriptingComponent) Framework.getRuntime()
				.getComponent(CID);
		ComponentInstance ci = Framework.getRuntime().getComponentInstance(CID);

		boolean alreadyexist = Framework.getService(AutomationService.class).hasOperation(opId);
		// unregister first
		if (alreadyexist) {
			System.out.println("Unregister previously registered operation");

			// Automation registry does not use 'equal' to compare descriptors
			// => need to provide the exact same object descriptor
			ScriptingOperationDescriptor prev = null;
			for (ScriptingOperationDescriptor d : descs) {
				if (d.getId().equals(desc.getId())) {
					prev = d;
					break;
				}
			}
			if (prev == null) {
				System.out.println("Unable to find previous desc");
			} else {
				descs.remove(prev);
				// remove from registry
				component.unregisterContribution(prev, "operation", ci);
				// flush cache of compiled chains!
				AutomationAdmin aa = Framework.getService(AutomationAdmin.class);
				aa.flushCompiledChains();
			}
		}
		// register
		component.registerContribution(desc, "operation", ci);
		descs.add(desc);
	}

}
