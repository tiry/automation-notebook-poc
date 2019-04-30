package org.nuxeo.ecm.automation.notebook;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

import org.nuxeo.common.xmap.annotation.XNode;
import org.nuxeo.common.xmap.annotation.XObject;
import org.nuxeo.runtime.model.Descriptor;
import org.nuxeo.runtime.model.RuntimeContext;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@XObject("automationNotebook")
public class AutomationNotebookDescriptor  implements Descriptor{

    @XNode("@name")
    public String name;

    @XNode("@src")
    public String src;

    RuntimeContext context;

    String json;
    
    ObjectMapper objectMapper;
    
    protected List<PreProcessor.Result> code_cells = new ArrayList<>();
    
	@Override
	public String getId() {		
		return name;
	}    
	
	protected ObjectMapper getMapper() {
		if (objectMapper==null) {
			objectMapper = new ObjectMapper();
		}
		return objectMapper;
	}
	
	void parse() throws IOException {
		PreProcessor processor = new PreProcessor();
        ObjectMapper objectMapper = getMapper();
        JsonNode nb = objectMapper.readTree(json);
        
        JsonNode cells = nb.get("cells");
        for (int i = 0; i< cells.size(); i++) {
        	JsonNode cell = cells.get(i);
        	if (cell.get("cell_type").asText().equals("code")) {
        		JsonNode codeCell = cell.get("source");
        		StringJoiner code = new StringJoiner("");
        		for (int j = 0; j< codeCell.size(); j++) {
        			code.add(codeCell.get(j).asText());
        		}
        		code_cells.add(processor.preprocessCode(code.toString()));
        	}
        }
	}
	
	public List<PreProcessor.Result> getCells() {
		return code_cells;
	}
	
	public List<PreProcessor.Result> getSetupCells() {
		List<PreProcessor.Result> cells = new ArrayList<>();
		for (PreProcessor.Result result: getCells()) {
			if (result.getType().equals(PreProcessor.BlocType.SETUP)) {
				cells.add(result);
			}
		}
		return cells;
	}
	
	public List<PreProcessor.Result> getTestCells() {
		List<PreProcessor.Result> cells = new ArrayList<>();
		for (PreProcessor.Result result: getCells()) {
			if (result.getType().equals(PreProcessor.BlocType.TEST)) {
				cells.add(result);
			}
		}
		return cells;
	}
	
	public List<PreProcessor.Result> getOpCells() {
		List<PreProcessor.Result> cells = new ArrayList<>();
		for (PreProcessor.Result result: getCells()) {
			if (result.getType().equals(PreProcessor.BlocType.OPERATION)) {
				cells.add(result);
			}
		}
		return cells;
	}
}
