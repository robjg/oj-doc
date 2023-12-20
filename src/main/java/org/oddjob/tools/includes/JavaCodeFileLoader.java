package org.oddjob.tools.includes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;

/**
 * Creates text that can be inserted into JavaDoc or another XML document from
 * a Java Source Code File.
 * 
 * 
 * @author rob
 *
 */
public class JavaCodeFileLoader implements IncludeLoader {
	
	private static final Logger logger = LoggerFactory.getLogger(JavaCodeFileLoader.class);
	
	private final File base;
	
	public JavaCodeFileLoader(File base) {
		this.base = base;
	}
		
	public String load(String path) {
		
		try {			
			FilterFactory filterFactory = new FilterFactory(path);
			
			File file = new File(base, filterFactory.getResourcePath());
			
			logger.info("Reading file " + file);
			
			String result = filterFactory.getTextLoader().load(
					new FileInputStream(file));
			
			Java2HTML java2html = new Java2HTML();

			return java2html.convert(result);
		}
		catch (Exception e) {
			return "<p><em>" + e + "</em></p>\n";
		}
	}
}
