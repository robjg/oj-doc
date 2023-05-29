package org.oddjob.tools.doclet.utils;

import com.sun.source.doctree.DocTree;

import javax.lang.model.element.Element;

/**
 * A {@link TagProcessor} that just returns the text of the tag.
 * 
 * @author rob
 *
 */
class FallbackTagProcessor implements TagProcessor {

	@Override
	public String process(DocTree tag, Element element) {
		return tag.toString();
	}
}
