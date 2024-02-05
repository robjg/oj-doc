package org.oddjob.doc.html;

import org.oddjob.arooa.beandocs.element.LinkElement;

/**
 * A Context used in a Bean Doc Element Visitor.
 */
public interface HtmlContext {

    static HtmlContext noLinks() {
        return new HtmlContext() {

            @Override
            public String hyperlinkFor(LinkElement linkElement) {
                throw new UnsupportedOperationException();
            }
        };
    }

    String hyperlinkFor(LinkElement linkElement);

}
