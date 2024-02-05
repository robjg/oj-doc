package org.oddjob.doc.taglet;

import org.oddjob.arooa.beandocs.element.LinkElement;
import org.oddjob.doc.html.HtmlContext;
import org.oddjob.doc.util.DocUtil;

import javax.lang.model.element.Element;

/**
 * Process inline tags for BeanDoc Block Tags in the Standard Javadoc Doclet.
 */
public class TagletInlineTagHelper implements HtmlContext {

    private final Element element;

    public TagletInlineTagHelper(Element element) {
        this.element = element;
    }

    @Override
    public String hyperlinkFor(LinkElement linkElement) {

        String qualifiedName = linkElement.getQualifiedType();

        if (qualifiedName == null) {
            String link = linkElement.getSignature();
            if (linkElement.getLabel() != null) {
                link += " " + linkElement.getLabel();
            }
            return "<code>" + link + "</code>";
        }
        String simpleClassName = DocUtil.simpleName(qualifiedName);
        String relativePath = DocUtil.relativePath(element, qualifiedName);

        return "<code><a href='" + relativePath + "/" + simpleClassName + ".html'>"
                + simpleClassName + "</a></code>";
    }

}
