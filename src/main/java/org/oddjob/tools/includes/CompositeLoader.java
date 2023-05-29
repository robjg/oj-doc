package org.oddjob.tools.includes;

import org.oddjob.doc.doclet.CustomTagNames;

import java.util.HashMap;
import java.util.Map;

/**
 * Loaders for examples. Used from the Manual Doclet.
 * 
 * @author rob
 *
 */
public class CompositeLoader implements IncludeLoader {

	private final Map<String, IncludeLoader> loaders =
			new HashMap<>();
	
	private IncludeLoader selected;
	
	public CompositeLoader() {
		loaders.put(JavaCodeResourceLoader.TAG, new JavaCodeResourceLoader());
		loaders.put(CustomTagNames.XML_RESOURCE_TAG, new XMLResourceLoader());
		loaders.put(CustomTagNames.TEXT_RESOURCE_TAG, new PlainTextResourceLoader());
	}
		
	@Override
	public boolean canLoad(String tag) {
		selected = loaders.get(tag);
		return selected != null;
	}
	
	@Override
	public String load(String path) {
		if (selected == null) {
			throw new IllegalStateException("Check canLoad first.");
		}
		return selected.load(path);		
	}
}
