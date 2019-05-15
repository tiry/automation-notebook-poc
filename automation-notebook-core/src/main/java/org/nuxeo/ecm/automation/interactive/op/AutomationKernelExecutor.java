package org.nuxeo.ecm.automation.interactive.op;

import org.nuxeo.ecm.automation.OperationContext;
import org.nuxeo.ecm.automation.core.Constants;
import org.nuxeo.ecm.automation.core.annotations.Context;
import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
import org.nuxeo.ecm.automation.core.annotations.Param;
import org.nuxeo.ecm.automation.interactive.NBScriptExecutor;
import org.nuxeo.ecm.core.api.CoreSession;

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
		return new NBScriptExecutor().runAndRender(ctx, content, format);
	}

}
