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
 * Doc Tree Node Visitor for a Conversion Method.
 */
public class ConversionMethodVisitor extends NoopVisitor {

    private final DocTrees docTrees;

    private final VisitorContext visitorContext;

    private ConversionMethodVisitor(DocTrees docTrees, VisitorContext visitorContext) {
        this.docTrees = docTrees;
        this.visitorContext = visitorContext;
    }

    public static ConversionMethodVisitor with(DocTrees docTrees, VisitorContext visitorContext) {
        return new ConversionMethodVisitor(docTrees, visitorContext);
    }

    public void visit(DocCommentTree docCommentTree,
                      TypeConsumers typeConsumers) {

        TheVisitor typeVisitor = new TheVisitor(typeConsumers);

        docCommentTree.getBlockTags().forEach(node -> node.accept(typeVisitor, visitorContext));

        typeVisitor.close();
    }


    class TheVisitor extends NoopVisitor implements AutoCloseable {

        private final TypeConsumers typeConsumers;

        TheVisitor(TypeConsumers typeConsumers) {
            this.typeConsumers = Objects.requireNonNull(typeConsumers);
        }

        @Override
        public Void visitUnknownBlockTag(UnknownBlockTagTree node, VisitorContext visitorContext) {

            String tagName = node.getTagName();

            if (CustomTagNames.CONVERSION_TAG_NAME.equals(tagName)) {

                String methodName = String.valueOf(visitorContext.getElement().getSimpleName());
                BeanDocConsumer conversionConsumer = typeConsumers.conversion(methodName);

                if (conversionConsumer == null) {
                    return null;
                }

                DocCommentTree docCommentTree = docTrees.getDocTreeFactory().newDocCommentTree(
                        node.getContent(), List.of());

                BlockVisitor.visitAll(docCommentTree.getFirstSentence(),
                        conversionConsumer::acceptFirstSentence, visitorContext);

                BlockVisitor.visitAll(docCommentTree.getFullBody(),
                        conversionConsumer::acceptBodyText, visitorContext);

                conversionConsumer.close();
            }

            return null;
        }

        @Override
        public void close() {
        }
    }
}
