package org.oddjob.doc.taglet;

import org.oddjob.doc.doclet.CustomTagNames;

import java.util.Set;

/**
 * Taglet for the oddjob.example tag.
 * 
 * @author rob
 *
 */
public class ExampleTaglet extends BaseBlockTaglet {

//	public static void register(Map<String, Taglet> tagletMap) {
//	    tagletMap.put(CustomTagNames.EXAMPLE_TAG_NAME, new ExampleTaglet());
//	}

	@Override
	public Set<Location> getAllowedLocations() {
		return Set.of(Location.TYPE);
	}

	@Override
	public String getName() {
		return CustomTagNames.EXAMPLE_TAG_NAME;
	}

	@Override
	public String getTitle() {
		return "Example";
	}
}
