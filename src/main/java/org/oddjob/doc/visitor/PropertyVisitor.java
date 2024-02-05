package org.oddjob.doc.visitor;

import com.sun.source.doctree.DocCommentTree;
import com.sun.source.doctree.UnknownBlockTagTree;
import com.sun.source.util.DocTrees;
import org.oddjob.doc.beandoc.BeanDocConsumer;
import org.oddjob.doc.beandoc.TypeConsumers;
import org.oddjob.doc.doclet.CustomTagNames;
import org.oddjob.doc.util.DocUtil;

import java.util.List;
import java.util.Objects;

/**
 * Doc Tree Node Visitor for Oddjob Properties.
 */
public class PropertyVisitor extends NoopVisitor {

    private final DocTrees docTrees;

    private final VisitorContext visitorContext;

    private PropertyVisitor(DocTrees docTrees, VisitorContext visitorContext) {
        this.docTrees = docTrees;
        this.visitorContext = visitorContext;
    }

    public static PropertyVisitor with(DocTrees docTrees, VisitorContext visitorContext) {
        return new PropertyVisitor(docTrees, visitorContext);
    }

    public void visit(DocCommentTree docCommentTree,
                      TypeConsumers typeConsumers,
                      String propertyName) {

        TheVisitor typeVisitor = new TheVisitor(typeConsumers, propertyName);

        docCommentTree.getBlockTags().forEach(node -> node.accept(typeVisitor, visitorContext));

        typeVisitor.close();
    }


    class TheVisitor extends NoopVisitor implements AutoCloseable {

        private final TypeConsumers typeConsumers;

        private String propertyName;

        private BeanDocConsumer.Property propertyConsumer;

        TheVisitor(TypeConsumers typeConsumers,
                   String propertyName) {
            this.typeConsumers = Objects.requireNonNull(typeConsumers);
            this.propertyName = Objects.requireNonNull(propertyName);
        }

        @Override
        public Void visitUnknownBlockTag(UnknownBlockTagTree node, VisitorContext visitorContext) {

            String tagName = node.getTagName();

            if (CustomTagNames.PROPERTY_TAG_NAME.equals(tagName)) {

                String tagContent = DocUtil.toString(node.getContent());

                if (!tagContent.isBlank()) {
                    if (!tagContent.equals(this.propertyName)) {
                        visitorContext.warn("Derived Property name [" + this.propertyName  +
                                "] does not match tag property name [" + tagContent + "]");

                    }
                    this.propertyName = tagContent;
                }
                if (getConsumerButNone()) {
                    return null;
                }
            }
            else if (CustomTagNames.DESCRIPTION_TAG_NAME.equals(tagName)) {

                if (getConsumerButNone()) {
                    return null;
                }

                DocCommentTree docCommentTree = docTrees.getDocTreeFactory().newDocCommentTree(
                        node.getContent(), List.of());

                BlockVisitor.visitAll(docCommentTree.getFirstSentence(),
                        propertyConsumer::acceptFirstSentence, visitorContext);

                BlockVisitor.visitAll(docCommentTree.getFullBody(),
                        propertyConsumer::acceptBodyText, visitorContext);

                propertyConsumer.close();

            } else if (CustomTagNames.REQUIRED_TAG_NAME.equals(tagName)) {

                if (getConsumerButNone()) {
                    return null;
                }

                propertyConsumer.required(DocUtil.toString(node.getContent()));

            } else {

                visitorContext.warn("Ignoring: " + node);
            }

            return null;
        }

        boolean getConsumerButNone() {
            if (propertyConsumer == null) {
                propertyConsumer = typeConsumers.property(propertyName);
            }

            // This will be null if the property is not public. The property tag might be on a
            // protected member of a base class that is exposed in some subclasses but not all
            // The stop property is one such example.
            return propertyConsumer == null;
        }

        @Override
        public void close() {
            if (propertyConsumer != null) {
                propertyConsumer.close();
            }
        }
    }
}
