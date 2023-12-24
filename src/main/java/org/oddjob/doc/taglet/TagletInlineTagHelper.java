package org.oddjob.doc.taglet;

import com.sun.source.doctree.LinkTree;
import com.sun.source.doctree.LiteralTree;
import com.sun.source.doctree.ReferenceTree;
import com.sun.source.doctree.UnknownInlineTagTree;
import com.sun.source.util.DocTrees;
import org.oddjob.arooa.beandocs.element.BeanDocElement;
import org.oddjob.arooa.beandocs.element.LiteralElement;
import org.oddjob.arooa.beandocs.element.StandardElement;
import org.oddjob.doc.util.DocUtil;
import org.oddjob.doc.util.InlineTagHelper;
import org.oddjob.doc.util.LoaderProvider;

import javax.lang.model.element.Element;

/**
 * Process inline tags for the Standard Javadoc Doclet.
 */
public class TagletInlineTagHelper implements InlineTagHelper {

    private final DocTrees docTrees;

    private final LoaderProvider loaderProvider;

    public TagletInlineTagHelper(DocTrees docTrees, LoaderProvider loaderProvider) {
        this.docTrees = docTrees;
        this.loaderProvider = loaderProvider;
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

        return "<code><a href='" + relativePath + "/" + simpleClassName + ".html'>"
                + simpleClassName + "</a></code>";
    }

    @Override
    public BeanDocElement processUnknownInline(UnknownInlineTagTree unknownTag, Element element) {

        return loaderProvider.loaderFor(unknownTag.getTagName())
                .map(loader -> loader.load(DocUtil.unknownTagContent(unknownTag)))
                        .orElseGet(() -> StandardElement.of(unknownTag.toString()));
    }

    @Override
    public BeanDocElement processLiteral(LiteralTree literalTree, Element element) {
        return LiteralElement.of(literalTree.getBody().toString());
    }
}
