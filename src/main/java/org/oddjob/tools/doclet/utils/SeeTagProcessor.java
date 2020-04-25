package org.oddjob.tools.doclet.utils;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.SeeTag;
import com.sun.javadoc.Tag;
import org.oddjob.tools.OjDocLogger;

/**
 * Process see and link tags.
 * 
 * @author rob
 *
 */
public class SeeTagProcessor implements TagProcessor {

	private static final OjDocLogger logger = OjDocLogger.getLogger();

	@Override
	public String process(Tag tag) {
		
		if (! (tag instanceof SeeTag)) {
			return null;
		}
		
		SeeTag seeTag = (SeeTag) tag;

		String linkText;

		ClassDoc referencedClassDoc = seeTag.referencedClass();

		String referencedClassName = seeTag.referencedClassName();

		if (referencedClassDoc == null || referencedClassName == null) {

			linkText = seeTag.text() + " DOES NOT EXIST.";
		}
		else {
			String simpleClassName = referencedClassDoc.name();

			String fileName = referencedClassName.replace('.', '/') + ".html";

			String rootDir = new ClassDocUtils(seeTag.holder()).getRelativeRootDir();

			linkText = "<a href='" + rootDir + "/" + fileName + "'>"
					+ simpleClassName + "</a>";
		}
		return "<code>" + linkText + "</code>";
	}	
}
