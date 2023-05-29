package org.oddjob.doc.taglet;

import org.oddjob.doc.doclet.CustomTagNames;

import java.util.Set;

/**
 * Taglet for the oddjob.property tag.
 * 
 * @author rob
 *
 */
public class PropertyTaglet extends BaseBlockTaglet {

//	public static void register(Map<String, Taglet> tagletMap) {
//	    tagletMap.put(CustomTagNames.PROPERTY_TAG_NAME, new PropertyTaglet());
//	}

	@Override
	public Set<Location> getAllowedLocations() {
		return Set.of(Location.METHOD, Location.FIELD);
	}

	@Override
	public String getName() {
		return CustomTagNames.PROPERTY_TAG_NAME;
	}

	@Override
	public String getTitle() {
		return "Property";
	}
}
