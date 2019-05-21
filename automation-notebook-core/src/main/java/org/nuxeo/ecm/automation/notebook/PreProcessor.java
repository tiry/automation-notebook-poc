package org.nuxeo.ecm.automation.notebook;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.nuxeo.ecm.automation.interactive.reload.AutomationHelper;

public class PreProcessor {

    protected Pattern opPattern = Pattern.compile("@Operation\\(.*id.*=.*\"(.*)\".*\\)");
    protected Pattern setupPattern = Pattern.compile("@Setup");
    protected Pattern testPattern = Pattern.compile("@Test\\(.*id.*=.*\"(.*)\".*\\)");

    public enum BlocType 
    { 
        OPERATION, SETUP, TEST; 
    } 
	
	public class Result {
		
		protected String code;
		protected String id;
		protected BlocType type;
		
		public String getCode() {
			return code;
		}
		public String getId() {
			return id;
		}
		public BlocType getType() {
			return type;
		}
				
	}

	protected String getDebugHelperCode() {

		StringBuffer sb = new StringBuffer();

		// init the Debug Ctx
		sb.append("Debug.init(ctx);\n");
		
		return sb.toString();
	}

	protected String addWrapperCode(String id, String code) {

		StringBuffer sb = new StringBuffer();
		
		sb.append("function run(input, params) {\n");
		sb.append("  Console.iniTraceId('" + id + "');");
		sb.append("  Console.log('*** inside Wrapper ***');\n");		
		sb.append(getDebugHelperCode());
		sb.append(code);
		sb.append("  return run(input,params);\n");
		sb.append("}\n");
		
		return sb.toString();
	}


	public Result preprocessCode(String code) {
		
		Result result = new Result();		
		code = code.trim();
		
		// operation
        Matcher matcher = opPattern.matcher(code);        
        if (matcher.lookingAt()) {
        	String opId =matcher.group(1);
        	code = matcher.replaceFirst("");        	
        	if (code.startsWith("\n")) {
        		code=code.substring(1);
        	}
        	result.id = opId;
        	result.type = BlocType.OPERATION;

        } else {       		
	        // setup
	        matcher = setupPattern.matcher(code);        
	        if (matcher.lookingAt()) {
	        	code = matcher.replaceFirst("");        	
	        	if (code.startsWith("\n")) {
	        		code=code.substring(1);
	        	}
	        	result.type = BlocType.SETUP;
	        } else {       			
		        // test
		        matcher = testPattern.matcher(code);        
		        if (matcher.lookingAt()) {
		        	String tId =matcher.group(1);
		        	code = matcher.replaceFirst("");        	
		        	if (code.startsWith("\n")) {
		        		code=code.substring(1);
		        	}
		        	result.id = tId;
		        	result.type = BlocType.TEST;
		        }       	
	        }
        }

        if (result.type == BlocType.OPERATION) {
        	code=addWrapperCode(result.id, code);
        	AutomationHelper.register(result.id, code);
        	result.code=code;
        } else {
        	result.code=getDebugHelperCode() + code;
        	result.code=code;
        }
		return result;
	}

}
