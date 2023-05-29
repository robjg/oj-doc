package org.oddjob.tools.doclet.utils;

import com.sun.source.doctree.DocTree;

import javax.lang.model.element.Element;

/**
 * A {@link TagProcessor} that tries a number of different processors
 * until it finds one that works.
 * 
 * @author rob
 *
 */
class CompositeTagProcessor implements TagProcessor {

	private final TagProcessor[] processors;
	
	/**
	 * Constructor.
	 * 
	 * @param processors Processors to try.
	 */
	public CompositeTagProcessor(TagProcessor... processors) {
		this.processors = processors;
	}
	
	@Override
	public String process(DocTree tag, Element element) {
		for (TagProcessor processor : processors) {
			String snipet = processor.process(tag, element);
			if (snipet != null) {
				return snipet;
			}
		}
		return null;
	}
}
