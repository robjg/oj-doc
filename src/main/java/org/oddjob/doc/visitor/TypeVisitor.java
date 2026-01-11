package org.oddjob.doc.visitor;

import com.sun.source.doctree.DocCommentTree;
import com.sun.source.doctree.UnknownBlockTagTree;
import com.sun.source.util.DocTrees;
import org.oddjob.doc.beandoc.BeanDocConsumer;
import org.oddjob.doc.beandoc.TypeConsumers;
import org.oddjob.doc.doclet.CustomTagNames;

import java.util.List;
import java.util.Objects;

/**
 * Visits the Doc for a Type.
 */
public class TypeVisitor {

    private final DocTrees docTrees;

    private final VisitorContext visitorContext;

    private TypeVisitor(DocTrees docTrees, VisitorContext visitorContext) {
        this.docTrees = docTrees;
        this.visitorContext = visitorContext;
    }

    public static TypeVisitor with(DocTrees docTrees, VisitorContext visitorContext) {
        return new TypeVisitor(docTrees, visitorContext);
    }

    public void visit(DocCommentTree docCommentTree,
                      TypeConsumers beanDocConsumer) {

        TheVisitor theVisitor = new TheVisitor(beanDocConsumer);

        docCommentTree.getBlockTags().forEach(node -> node.accept(theVisitor, visitorContext));
    }

    class TheVisitor extends NoopVisitor {

        private final TypeConsumers beanDocConsumer;

        TheVisitor(TypeConsumers beanDocConsumer) {
            this.beanDocConsumer = Objects.requireNonNull(beanDocConsumer);
        }

        @Override
        public Void visitUnknownBlockTag(UnknownBlockTagTree node, VisitorContext visitorContext) {

            String tagName = node.getTagName();

            BeanDocConsumer docConsumer;
            switch (tagName) {
                case CustomTagNames.DESCRIPTION_TAG_NAME -> docConsumer = beanDocConsumer.description();
                case CustomTagNames.EXAMPLE_TAG_NAME -> docConsumer = beanDocConsumer.example();
                case CustomTagNames.CONVERSION_TAG_NAME -> docConsumer = beanDocConsumer.conversion();
                case null, default -> {

                    visitorContext.warn("Ignoring: " + node);

                    return null;
                }
            }

            // The Type Consumer will have warned doc is in the wrong place.
            if (docConsumer == null) {
                return null;
            }


            DocCommentTree docCommentTree = docTrees.getDocTreeFactory().newDocCommentTree(
                    node.getContent(), List.of());

            BlockVisitor.visitAll(docCommentTree.getFirstSentence(),
                    docConsumer::acceptFirstSentence, visitorContext);

            BlockVisitor.visitAll(docCommentTree.getFullBody(),
                    docConsumer::acceptBodyText, visitorContext);

            docConsumer.close();

            return null;
        }
    }

}
