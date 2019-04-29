package org.nuxeo.ecm;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.nuxeo.ecm.automation.interactive.op.AutomationKernelAutocomplete;

public class TestParsing extends AutomationKernelAutocomplete {
	
	
    protected String loadScript(String name) throws Exception {
    	return IOUtils.toString(this.getClass().getClassLoader().getResourceAsStream(name));    	
    }

	@Test
	public void testJSParsing() throws Exception {
		String js = loadScript("test.js");
		List<String> names = parseJS(js);
		
		assertTrue(names.contains("a"));
		assertTrue(names.contains("b"));
		assertTrue(names.contains("toto"));
		assertTrue(names.contains("bitou"));
		assertTrue(names.contains("i"));
		
	}
	
}
