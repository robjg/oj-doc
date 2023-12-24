package org.oddjob.doc.doclet;

import com.sun.source.doctree.LinkTree;
import com.sun.source.doctree.LiteralTree;
import com.sun.source.doctree.ReferenceTree;
import com.sun.source.doctree.UnknownInlineTagTree;
import com.sun.source.util.DocTrees;
import org.oddjob.arooa.beandocs.element.BeanDocElement;
import org.oddjob.arooa.beandocs.element.CodeElement;
import org.oddjob.arooa.beandocs.element.LiteralElement;
import org.oddjob.arooa.beandocs.element.StandardElement;
import org.oddjob.doc.util.DocUtil;
import org.oddjob.doc.util.InlineTagHelper;
import org.oddjob.doc.util.LoaderProvider;

import javax.lang.model.element.Element;
import java.util.function.Function;

/**
 * Inline helper for the Reference.
 */
public class ReferenceInlineTagHelper implements InlineTagHelper {

    private final DocTrees docTrees;

    private final LoaderProvider loaderProvider;

    private final Function<String, String> refLookup;

    private final Function<String, String> apiDirFunc;

    private final String pathToRoot;

    public ReferenceInlineTagHelper(DocTrees docTrees,
                                    LoaderProvider loaderProvider,
                                    Function<String, String> refLookup,
                                    Function<String, String> apiDirFunc,
                                    String pathToRoot) {
        this.docTrees = docTrees;
        this.loaderProvider = loaderProvider;
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
    public BeanDocElement processUnknownInline(UnknownInlineTagTree unknownTag, Element element) {

        return loaderProvider.loaderFor(unknownTag.getTagName())
                .map(loader -> loader.load(DocUtil.unknownTagContent(unknownTag)))
                .orElseGet(() -> StandardElement.of(unknownTag.toString()));
    }

    @Override
    public BeanDocElement processLiteral(LiteralTree literalTree, Element element) {
        String text = literalTree.getBody().toString();
        switch (literalTree.getKind()) {
            case CODE:
                return CodeElement.of(text);
            case LITERAL:
                return LiteralElement.of(text);
            default:
                return StandardElement.of("[Unsupported Literal Kind " + literalTree.getKind() + "]: " + text);
        }
    }
}
