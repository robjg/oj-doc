package org.oddjob.tools.includes;

/**
 * Loads a file or resource and converts it into HTML to be included in
 * documentation or javadoc.
 * 
 * @author rob
 *
 */
public interface IncludeLoader {

	/**
	 * Load the resource or file.
	 * 
	 * @param path The resource or file path.
	 * @return HTML text.
	 */
	String load(String path);
}
