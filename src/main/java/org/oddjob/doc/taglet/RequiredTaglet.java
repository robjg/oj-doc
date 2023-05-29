package org.oddjob.doc.taglet;

import org.oddjob.doc.doclet.CustomTagNames;

import java.util.Set;

/**
 * Taglet for the oddjob.required tag.
 * 
 * @author rob
 *
 */
public class RequiredTaglet extends BaseBlockTaglet {

//	public static void register(Map<String, Taglet> tagletMap) {
//	    tagletMap.put(CustomTagNames.REQUIRED_TAG_NAME, new RequiredTaglet());
//	}

	@Override
	public Set<Location> getAllowedLocations() {
		return Set.of(Location.METHOD, Location.FIELD);
	}

	@Override
	public String getName() {
		return CustomTagNames.REQUIRED_TAG_NAME;
	}

	@Override
	public String getTitle() {
		return "Required";
	}
}
