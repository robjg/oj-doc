package org.oddjob.tools.doclet.utils;

import com.sun.source.doctree.DocTree;
import com.sun.source.doctree.UnknownInlineTagTree;
import org.oddjob.doc.util.DocUtil;
import org.oddjob.tools.includes.XMLResourceLoader;

import javax.lang.model.element.Element;

/**
 * Process Oddjob XML resource tag.
 * 
 * @author rob
 *
 */
class XMLResourceTagProcessor implements TagProcessor {

	@Override
	public String process(DocTree tag, Element element) {

		if (!(tag instanceof UnknownInlineTagTree)) {
			return null;
		}

		UnknownInlineTagTree unknown = ((UnknownInlineTagTree) tag);

		if (!XMLResourceLoader.XML_RESOURCE_TAG_NAME.equals(unknown.getTagName())) {
			return null;
		}
		
		String path = DocUtil.toString(unknown.getContent());
		
		return new XMLResourceLoader().load(path);		
	}
}
