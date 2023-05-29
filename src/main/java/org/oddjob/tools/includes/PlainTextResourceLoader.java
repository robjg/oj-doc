package org.oddjob.tools.includes;

import org.oddjob.doc.doclet.CustomTagNames;

import java.io.IOException;
import java.io.InputStream;

/**
 * Creates Plain Text that can be inserted into JavaDoc or another HTML document from
 * an file class path resource.
 * 
 * @author rob
 *
 */
public class PlainTextResourceLoader implements IncludeLoader, CustomTagNames {

	@Override
	public boolean canLoad(String tag) {
		return TEXT_RESOURCE_TAG.equals(tag);
	}
	
	@Override
	public String load(String resource) {

		return loadText(resource);
	}

	public static String loadText(String resource) {

		try {
			InputStream input = PlainTextResourceLoader.class
					.getClassLoader().getResourceAsStream(resource);
			if (input == null) {
				throw new IOException("No Resource Found: path");
			}

			return new PlainTextToHTML().toHTML(input);
		}
		catch (Exception e) {
			return "<p><em>" + e + "</em></p>" + EOL;
		}
	}
}
