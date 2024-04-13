package org.oddjob.doc.html;

import org.oddjob.arooa.beandocs.element.DocElementVisitor;
import org.oddjob.arooa.beandocs.element.LinkElement;
import org.oddjob.doc.beandoc.BeanDocContext;

/**
 * A Context used in a Bean Doc Element Visitor.
 */
public interface HtmlContext extends BeanDocContext<HtmlContext> {

    static HtmlContext noLinks() {
        return linkElement -> {
            throw new UnsupportedOperationException();
        };
    }

    String hyperlinkFor(LinkElement linkElement);

    @Override
    default DocElementVisitor<HtmlContext, String> docElementVisitor() {
        return new HtmlVisitor();
    }
}
