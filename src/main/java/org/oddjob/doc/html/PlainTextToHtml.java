package org.oddjob.doc.html;

import org.oddjob.arooa.beandocs.element.PreformattedBlock;

/**
 * Converts Plain Text to HTML. At the moment this just wraps the text in
 * a <code>pre</code> tags.
 * 
 * @author rob
 *
 */
public class PlainTextToHtml {

	public static String toHtml(PreformattedBlock block) {

        return "<pre>\n" +
				block.getText() +
				"</pre>\n";
	}
}
