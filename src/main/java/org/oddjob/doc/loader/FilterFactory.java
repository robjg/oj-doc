package org.oddjob.doc.loader;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Provide a {@link InputStreamToText} loader that will filter out
 * a snippet from the section of an Input Stream.
 * <p>
 * The snippet is identified by appending a # to the resource.
 * 
 * @author rob
 *
 */
public class FilterFactory {

	public final static Pattern PATTERN = Pattern.compile("([^#]+)(?:#(.*))?");
	
	private final InputStreamToText textLoader;
	
	private final String resourcePath;
	
	public FilterFactory(String path) {
		
		Matcher matcher = PATTERN.matcher(path);
		
		if (!matcher.matches()) {
			throw new IllegalArgumentException(
					"Invalid Include Path: " + path);
		}
		
		this.resourcePath = matcher.group(1);
		
		String snippet = matcher.group(2);
		
		if (snippet == null) {
			textLoader = new PlainInputStreamToText();
		}
		else {
			textLoader = new SnippetFilter(snippet);
		}
	}
	
	public InputStreamToText getTextLoader() {
		return textLoader;
	}
	
	/**
	 * The resource part of the path (i.e. before the #).
	 * 
	 * @return The path. Never null.
	 */
	public String getResourcePath() {
		return resourcePath;
	}
}
