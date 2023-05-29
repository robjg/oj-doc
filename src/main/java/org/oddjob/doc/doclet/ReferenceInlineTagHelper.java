package org.oddjob.doc.doclet;

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
import java.util.function.Function;

/**
 * Inline helper for the Reference.
 */
public class ReferenceInlineTagHelper implements InlineTagHelper {

    private final DocTrees docTrees;

    private final TagletProvider tagletProvider;

    private final Function<String, String> refLookup;

    private final Function<String, String> apiDirFunc;

    private final String pathToRoot;

    public ReferenceInlineTagHelper(DocTrees docTrees,
                                    TagletProvider tagletProvider,
                                    Function<String, String> refLookup,
                                    Function<String, String> apiDirFunc,
                                    String pathToRoot) {
        this.docTrees = docTrees;
        this.tagletProvider = tagletProvider;
        this.refLookup = refLookup;
        this.apiDirFunc = apiDirFunc;
        this.pathToRoot = pathToRoot;
    }

    @Override
    public String processLink(LinkTree linkTag, Element element) {

        ReferenceTree rtree = linkTag.getReference();

        Element refElement = DocUtil.getReferenceElement(docTrees, element, rtree);

        if (refElement == null) {
            return "<code>" + rtree.getSignature() + "</code>";
        }

        String ref = refElement.toString();

        String fileName = ref.replace('.', '/') +  ".html";

        String componentName = refLookup.apply(ref);

        if (componentName == null) {
            return "<code><a href='" + apiDirFunc.apply(pathToRoot) + "/" + fileName + "'>"
                    + ref + "</a></code>";
        }
        else {
            return "<a href='" + pathToRoot + "/" + fileName + "'>"
                    + componentName + "</a>";
        }
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
