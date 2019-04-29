package org.nuxeo.ecm.automation.interactive;

import java.io.File;
import java.io.StringWriter;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import org.nuxeo.ecm.platform.rendering.api.RenderingException;
import org.nuxeo.ecm.platform.rendering.api.ResourceLocator;
import org.nuxeo.ecm.platform.rendering.fm.FreemarkerEngine;
import org.nuxeo.runtime.api.Framework;

public class FMRenderer {

	protected FreemarkerEngine engine;

	public FMRenderer() {
		engine = new FreemarkerEngine();
		engine.setResourceLocator(new CLResourceLocator());
	}

	public String render(String templateName, Map<String, Object> params) throws RenderingException {
		StringWriter writer = new StringWriter();

		if (params == null) {
			params = new HashMap<String, Object>();
		}

		engine.render(templateName, params, writer);
		return writer.toString();
	}

	protected class CLResourceLocator implements ResourceLocator {
		public File getResourceFile(String key) {
			return null;
		}

		public URL getResourceURL(String key) {
			return this.getClass().getClassLoader().getResource(key);
		}
	}
}
