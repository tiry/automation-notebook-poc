package org.nuxeo.ecm;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;

import org.nuxeo.ecm.automation.AutomationService;
import org.nuxeo.ecm.automation.OperationChain;
import org.nuxeo.ecm.automation.OperationContext;
import org.nuxeo.ecm.automation.OperationType;
import org.nuxeo.ecm.automation.core.Constants;
import org.nuxeo.ecm.automation.core.annotations.Context;
import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
import org.nuxeo.ecm.automation.core.annotations.Param;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.runtime.api.Framework;

import jdk.nashorn.internal.ir.BlockStatement;
import jdk.nashorn.internal.ir.FunctionNode;
import jdk.nashorn.internal.ir.Statement;
import jdk.nashorn.internal.ir.VarNode;
import jdk.nashorn.internal.parser.Parser;
import jdk.nashorn.internal.runtime.ErrorManager;
import jdk.nashorn.internal.runtime.ScriptEnvironment;
import jdk.nashorn.internal.runtime.Source;
import jdk.nashorn.internal.runtime.options.Options;

/**
 *
 */
@Operation(id = AutomationKernelAutocomplete.ID, category = Constants.CAT_DOCUMENT, label = "Automation.KernelAutocomplete", description = "Describe here what your operation does.")
public class AutomationKernelAutocomplete {

	public static final String ID = "Automation.KernelAutocomplete";

	protected static final boolean addSignature = true;

	protected static List<String> jsReservedKeyWords = Arrays.asList("break", "case", "catch", "class", "const",
			"continue", "debugger", "default", "delete", "do", "else", "export", "extends", "finally", "for",
			"function", "if", "import", "in", "instanceof", "new", "return", "super", "switch", "this", "throw", "try",
			"typeof", "var", "void", "while", "with", "yield");

	@Context
	protected CoreSession session;

	@Context
	protected OperationContext ctx;

	@Param(name = "prefix", required = true)
	protected String prefix;

	protected List<String> parseJS(String code) {

		Options options = new Options("");
		options.set("language", "es6");
		PrintWriter out = new PrintWriter(System.out);
		PrintWriter err = new PrintWriter(System.err);

		ScriptEnvironment se = new ScriptEnvironment(options, out, err);
		Source src = Source.sourceFor("some.js", code);
		ErrorManager em = new ErrorManager();
		Parser parser = new Parser(se, src, em);

		FunctionNode node = parser.parse();
		List<String> names = parse(node.getBody().getStatements());
		return names;
	}

	protected List<String> parse(List<Statement> smts) {

		List<String> names = new ArrayList<>();
		for (Statement smt : smts) {
			if (smt instanceof VarNode) {
				names.add(((VarNode) smt).getName().getName());
			} else if (smt instanceof BlockStatement) {
				names.addAll(parse(((BlockStatement) smt).getBlock().getStatements()));
			} else {
				// System.out.println(smt.getClass());
			}
		}
		return names;
	}

	protected String prepopulateSignature(OperationType ot) throws Exception {
		
		StringBuffer sb = new StringBuffer("(");
		
		String input = ot.getInputType();
		if ("document".equals(input)) {
			sb.append("doc");
		} else if ("documents".equals(input)) {
			sb.append("docs");
		} else if ("blob".equals(input)) {
			sb.append("blob");
		} else if ("blobs".equals(input)) {
			sb.append("blobs");
		} else if ("void".equals(input) || input==null) {
			sb.append("null");			
		}

		sb.append(",{");
		
		for (org.nuxeo.ecm.automation.OperationDocumentation.Param param : ot.getDocumentation().getParams()) {
			sb.append("'" + param.getName() + "' : null,");
		}		
		
		sb.append("})");
		return sb.toString();
	}
	
	protected List<String> getOperationNames() throws Exception {

		List<String> names = new ArrayList<>();
		AutomationService service = Framework.getService(AutomationService.class);
		for (OperationType ot : service.getOperations()) {
			if (addSignature) {
				names.add(ot.getId() + prepopulateSignature(ot));	
			} else {
				names.add(ot.getId());				
			}			
		}
		return names;
	}

	@OperationMethod
	public String run(String content) throws Exception {

		StringJoiner suggestions = new StringJoiner("\n");
		
		// keywords
		for (String kw : jsReservedKeyWords) {
			if (kw.startsWith(prefix)) {
				suggestions.add(kw);
			}
		}

		// js parsing
		for (String kw : parseJS(content)) {
			if (kw.startsWith(prefix)) {
				suggestions.add(kw);
			}
		}

		// automation names
		for (String op : getOperationNames()) {
			if (op.startsWith(prefix)) {
				suggestions.add(op);
			}
		}
		
		return suggestions.toString();
	}

}
