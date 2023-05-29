package org.oddjob.doc.taglet;

import jdk.javadoc.doclet.Taglet;
import org.oddjob.doc.doclet.CustomTagNames;

import java.util.Map;
import java.util.Set;

/**
 * Taglet for the oddjob.description tag.
 * 
 * @author rob
 *
 */
public class DescriptionTaglet extends BaseBlockTaglet {

	public static void register(Map<String, Taglet> tagletMap) {
	    tagletMap.put(CustomTagNames.DESCRIPTION_TAG_NAME, new DescriptionTaglet());
	}

	@Override
	public Set<Location> getAllowedLocations() {
		return Set.of(Location.TYPE, Location.METHOD, Location.FIELD);
	}
	
	@Override
	public String getName() {
		return CustomTagNames.DESCRIPTION_TAG_NAME;
	}

	@Override
	public String getTitle() {
		return "Description";
	}
}
