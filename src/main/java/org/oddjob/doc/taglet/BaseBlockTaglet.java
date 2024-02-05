package org.oddjob.doc.taglet;

import com.sun.source.doctree.DocTree;
import com.sun.source.doctree.UnknownBlockTagTree;
import com.sun.source.util.DocTrees;
import jdk.javadoc.doclet.Doclet;
import jdk.javadoc.doclet.DocletEnvironment;
import jdk.javadoc.doclet.Reporter;
import jdk.javadoc.doclet.Taglet;
import org.oddjob.arooa.beandocs.element.BeanDocElement;
import org.oddjob.doc.html.HtmlVisitor;
import org.oddjob.doc.util.DocTreesReporter;
import org.oddjob.doc.util.LoaderProvider;
import org.oddjob.doc.visitor.BlockVisitor;
import org.oddjob.doc.visitor.VisitorContext;
import org.oddjob.doc.visitor.VisitorContextBuilder;

import javax.lang.model.element.Element;
import java.util.ArrayList;
import java.util.List;

/**
 * Base class for Taglet functionality for an Oddjob block tag.
 *
 * @author rob
 */
abstract public class BaseBlockTaglet implements Taglet {

    private DocletEnvironment env;

    private LoaderProvider loaderProvider;

    private Reporter reporter;


    @Override
    final public boolean isInlineTag() {
        return false;
    }

    @Override
    public void init(DocletEnvironment env, Doclet doclet) {

        this.env = env;

        loaderProvider = new UnknownInlineLoaderProvider(getClass().getClassLoader());
    }

    @Override
    public String toString(List<? extends DocTree> tags, Element element) {

        DocTrees docTrees = env.getDocTrees();

        Reporter reporter = DocTreesReporter.with(docTrees);

        VisitorContext visitorContext = VisitorContextBuilder
                .create(docTrees, loaderProvider, reporter, element);

        List<BeanDocElement> docElements = new ArrayList<>();
        for (DocTree docTree: tags) {

            if (!(docTree instanceof UnknownBlockTagTree)) {
                throw new IllegalArgumentException("Tag expected to be " + UnknownBlockTagTree.class);
            }

            UnknownBlockTagTree unknownBlockTagTree = (UnknownBlockTagTree) docTree;

            List<? extends DocTree> content = unknownBlockTagTree.getContent();

            BlockVisitor.visitAll(content, docElements::add, visitorContext);

        }

        return "<p><b>" +
                getTitle() +
                "</b></p>" +
                HtmlVisitor.visitAll(docElements, new TagletInlineTagHelper(element));
    }

    /**
     * Used as the title of the Taglet.
     *
     * @return The title. Must not be null.
     */
    abstract public String getTitle();
}
