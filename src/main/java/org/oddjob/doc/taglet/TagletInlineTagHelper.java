package org.oddjob.doc.taglet;

import com.sun.source.doctree.LinkTree;
import com.sun.source.doctree.LiteralTree;
import com.sun.source.doctree.ReferenceTree;
import com.sun.source.doctree.UnknownInlineTagTree;
import com.sun.source.util.DocTrees;
import org.oddjob.doc.util.DocUtil;
import org.oddjob.doc.util.HtmlUtil;
import org.oddjob.doc.util.InlineTagHelper;
import org.oddjob.doc.util.TagletProvider;

import javax.lang.model.element.Element;
import java.util.Collections;

/**
 * Process inline tags for the Standard Javadoc Doclet.
 */
public class TagletInlineTagHelper implements InlineTagHelper {

    private final DocTrees docTrees;

    private final TagletProvider tagletProvider;

    public TagletInlineTagHelper(DocTrees docTrees, TagletProvider tagletProvider) {
        this.docTrees = docTrees;
        this.tagletProvider = tagletProvider;
    }

    @Override
    public String processLink(LinkTree linkTag, Element element) {

        ReferenceTree rtree = linkTag.getReference();

        Element refElement = DocUtil.getReferenceElement(docTrees, element, rtree);

        if (refElement == null) {
            return "<code>" + rtree.getSignature() + "</code>";
        }

        String qualifiedName = refElement.toString();

        String simpleClassName = DocUtil.simpleName(qualifiedName);
        String relativePath = DocUtil.relativePath(element, qualifiedName);

        return "<code><a href='" + relativePath  + "/" + simpleClassName + ".html'>"
                + simpleClassName + "</a></code>";
    }

    @Override
    public String processUnknownInline(UnknownInlineTagTree unknownTag, Element element) {

        return tagletProvider.tagletFor(unknownTag.getTagName())
                .map(taglet -> taglet.toString(Collections.singletonList(unknownTag), element))
                .orElseGet(unknownTag::toString);
    }

    @Override
    public String processLiteral(LiteralTree literalTree, Element element) {
        String allText = literalTree.getBody().toString();
        return HtmlUtil.escapeHtml(allText);
    }
}
