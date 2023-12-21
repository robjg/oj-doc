package org.oddjob.doc.taglet;

import com.sun.source.doctree.DocTree;
import com.sun.source.doctree.UnknownBlockTagTree;
import com.sun.source.util.DocTrees;
import jdk.javadoc.doclet.Doclet;
import jdk.javadoc.doclet.DocletEnvironment;
import jdk.javadoc.doclet.Reporter;
import jdk.javadoc.doclet.Taglet;
import org.oddjob.doc.util.DocTreesReporter;
import org.oddjob.doc.util.InlineTagHelper;
import org.oddjob.doc.visitor.BlockVisitor;
import org.oddjob.doc.visitor.VisitorContext;
import org.oddjob.doc.visitor.VisitorContextBuilder;

import javax.lang.model.element.Element;
import java.util.List;
import java.util.Objects;

/**
 * Base class for Taglet functionality for an Oddjob block tag.
 *
 * @author rob
 */
abstract public class BaseBlockTaglet implements Taglet {

    private DocletEnvironment env;

    private InlineTagHelper inlineTagHelper;

    private Reporter reporter;


    @Override
    final public boolean isInlineTag() {
        return false;
    }

    @Override
    public void init(DocletEnvironment env, Doclet doclet) {

        this.env = env;

        ClassLoader classLoader = Objects.requireNonNullElseGet(
                Thread.currentThread().getContextClassLoader(),
                () -> getClass().getClassLoader());

        inlineTagHelper = new TagletInlineTagHelper(
                env.getDocTrees(), new UnknownInlineLoaderProvider(classLoader));
    }

    @Override
    public String toString(List<? extends DocTree> tags, Element element) {

        DocTrees docTrees = env.getDocTrees();

        Reporter reporter = DocTreesReporter.with(docTrees);

        VisitorContext visitorContext = VisitorContextBuilder
                .create(inlineTagHelper, reporter, element);

        StringBuilder stringBuilder = new StringBuilder();

        for (DocTree docTree: tags) {

            if (!(docTree instanceof UnknownBlockTagTree)) {
                throw new IllegalArgumentException("Tag expected to be " + UnknownBlockTagTree.class);
            }

            UnknownBlockTagTree unknownBlockTagTree = (UnknownBlockTagTree) docTree;

            List<? extends DocTree> content = unknownBlockTagTree.getContent();

            stringBuilder.append("<p><b>")
                    .append(getTitle())
                    .append("</b></p>");

            BlockVisitor.visitAll(content, stringBuilder::append, visitorContext);

        }

        return stringBuilder.toString();
    }

    /**
     * Used as the title of the Taglet.
     *
     * @return The title. Must not be null.
     */
    abstract public String getTitle();
}
