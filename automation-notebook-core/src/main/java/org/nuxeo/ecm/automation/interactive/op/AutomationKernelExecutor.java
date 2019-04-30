package org.nuxeo.ecm.automation.interactive.op;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.nuxeo.automation.scripting.api.AutomationScriptingService;
import org.nuxeo.ecm.automation.OperationContext;
import org.nuxeo.ecm.automation.core.Constants;
import org.nuxeo.ecm.automation.core.annotations.Context;
import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
import org.nuxeo.ecm.automation.core.annotations.Param;
import org.nuxeo.ecm.automation.interactive.NBScriptExecutor;
import org.nuxeo.ecm.automation.interactive.helpers.AssertHelper;
import org.nuxeo.ecm.automation.interactive.helpers.NoteBookConsole;
import org.nuxeo.ecm.automation.notebook.PreProcessor;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.runtime.api.Framework;

/**
 *
 */
@Operation(id = AutomationKernelExecutor.ID, category = Constants.CAT_DOCUMENT, label = "Automation.KernelExecutor", description = "Describe here what your operation does.")
public class AutomationKernelExecutor {

	public static final String ID = "Automation.KernelExecutor";

	@Context
	protected CoreSession session;

	@Context
	protected OperationContext ctx;

	@Param(name = "format", required = false)
	protected String format = "html";

	
	@OperationMethod
	public String run(String content) throws Exception {
		return new NBScriptExecutor().run(ctx, content, format);
	}

}
