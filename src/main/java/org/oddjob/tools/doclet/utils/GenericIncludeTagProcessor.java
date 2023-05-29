package org.oddjob.tools.doclet.utils;

import com.sun.source.doctree.DocTree;
import com.sun.source.doctree.UnknownInlineTagTree;
import org.oddjob.doc.util.DocUtil;
import org.oddjob.tools.includes.IncludeLoader;

import javax.lang.model.element.Element;

/**
 * Process Oddjob XML resource tag.
 * 
 * @author rob
 *
 */
class GenericIncludeTagProcessor implements TagProcessor {

	private final String tag;
	
	private final IncludeLoader loader;
	
	public GenericIncludeTagProcessor(String tag, IncludeLoader loader) {
		this.tag = tag;
		this.loader  = loader;
	}
	
	@Override
	public String process(DocTree tag, Element element) {

		if (!(tag instanceof UnknownInlineTagTree)) {
			return null;
		}

		UnknownInlineTagTree unknown = ((UnknownInlineTagTree) tag);

		if (!unknown.getTagName().equals(this.tag)) {
			return null;
		}

		String path = DocUtil.toString(((UnknownInlineTagTree) tag).getContent());
		
		return loader.load(path);		
	}
}
