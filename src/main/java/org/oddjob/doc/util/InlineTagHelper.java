package org.oddjob.doc.util;

import com.sun.source.doctree.LinkTree;
import com.sun.source.doctree.LiteralTree;
import com.sun.source.doctree.UnknownInlineTagTree;
import org.oddjob.arooa.beandocs.element.BeanDocElement;

import javax.lang.model.element.Element;

/**
 * Define utilities for processing inline tags
 */
public interface InlineTagHelper {

    String processLink(LinkTree linkTag, Element element);

    BeanDocElement processUnknownInline(UnknownInlineTagTree unknownTag, Element element);

    String processLiteral(LiteralTree literalTree, Element element);
}
