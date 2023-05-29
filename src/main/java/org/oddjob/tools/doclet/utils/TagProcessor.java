package org.oddjob.tools.doclet.utils;

import com.sun.source.doctree.DocTree;

import javax.lang.model.element.Element;

/**
 * Something that can process a {@link DocTree}.
 * 
 * @author rob
 *
 */
interface TagProcessor {

	/**
	 * Process the tag. Generally the text returned will be HTML
	 * that can be inserted into the Oddjob JavaDoc or the reference
	 * manual.
	 * 
	 * @param tag The tag to process.
	 * @return Text or null if this processor can't process the given
	 * tag.
	 */
	String process(DocTree tag, Element element);
}
