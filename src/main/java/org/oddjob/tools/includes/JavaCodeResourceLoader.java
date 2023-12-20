package org.oddjob.tools.includes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;

/**
 * Creates text that can be inserted into JavaDoc or another XML document from
 * a Java Source Code File.
 * 
 * 
 * @author rob
 *
 */
public class JavaCodeResourceLoader implements IncludeLoader {
	
	private static final Logger logger = LoggerFactory.getLogger(JavaCodeResourceLoader.class);
	
	public String load(String path) {
		
		try {			
			FilterFactory filterFactory = new FilterFactory(path);
			
			String resource = filterFactory.getResourcePath();
			
			InputStream input = getClass().getClassLoader().getResourceAsStream(path);
			if (input == null) {
				throw new IOException("No Resource Found: path");
			}

			logger.info("Reading resource " + resource);
			
			String result = filterFactory.getTextLoader().load(
					input);
			
			Java2HTML java2html = new Java2HTML();

			return java2html.convert(result);
		}
		catch (Exception e) {
			return "<p><em>" + e + "</em></p>\n";
		}
	}
}
