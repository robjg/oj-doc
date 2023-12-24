package org.oddjob.doc.visitor;

import com.sun.source.doctree.*;
import org.oddjob.arooa.beandocs.element.BeanDocElement;
import org.oddjob.arooa.beandocs.element.StandardElement;

import java.util.List;
import java.util.function.Consumer;

/**
 * Visitor for the contents of a Block Tag. Used to process description, and example tags.
 */
public class BlockVisitor implements DocTreeVisitor<Void, VisitorContext> {

    private final Consumer<? super BeanDocElement> beanDocConsumer;

    private BlockVisitor(Consumer<? super BeanDocElement> beanDocConsumer) {
        this.beanDocConsumer = beanDocConsumer;
    }

    public static void visitAll(List<? extends DocTree> tags,
                                Consumer<? super BeanDocElement> docConsumer,
                                VisitorContext visitorContext) {

        BlockVisitor visitor = new BlockVisitor(docConsumer);
        tags.forEach(tag -> tag.accept(visitor, visitorContext));
    }

    @Override
    public Void visitAttribute(AttributeTree node, VisitorContext visitorContext) {
        beanDocConsumer.accept(StandardElement.of(node.toString()));
        return null;
    }

    @Override
    public Void visitAuthor(AuthorTree node, VisitorContext visitorContext) {
        visitorContext.warn("Ignoring: " + node);
        return null;
    }

    @Override
    public Void visitComment(CommentTree node, VisitorContext visitorContext) {
        visitorContext.warn("Ignoring: " + node);
        return null;
    }

    @Override
    public Void visitDeprecated(DeprecatedTree node, VisitorContext visitorContext) {
        visitorContext.warn("Ignoring: " + node);
        return null;
    }

    @Override
    public Void visitDocComment(DocCommentTree node, VisitorContext visitorContext) {
        visitorContext.fail("Should be impossible: " + node);
        return null;
    }

    @Override
    public Void visitDocRoot(DocRootTree node, VisitorContext visitorContext) {
        visitorContext.fail("Should be impossible: " + node);
        return null;
    }

    @Override
    public Void visitEndElement(EndElementTree node, VisitorContext visitorContext) {
        beanDocConsumer.accept(StandardElement.of(node.toString()));
        return null;
    }

    @Override
    public Void visitEntity(EntityTree node, VisitorContext visitorContext) {
        beanDocConsumer.accept(StandardElement.of(node.toString()));
        return null;
    }

    @Override
    public Void visitErroneous(ErroneousTree node, VisitorContext visitorContext) {
        visitorContext.warn("Erroneous: " + node);
        return null;
    }

    @Override
    public Void visitIdentifier(IdentifierTree node, VisitorContext visitorContext) {
        beanDocConsumer.accept(StandardElement.of(node.toString()));
        return null;
    }

    @Override
    public Void visitInheritDoc(InheritDocTree node, VisitorContext visitorContext) {
        visitorContext.warn("Ignoring: " + node);
        return null;
    }

    @Override
    public Void visitLink(LinkTree node, VisitorContext visitorContext) {
        beanDocConsumer.accept(StandardElement.of(visitorContext.processLink(node)));
        return null;
    }

    @Override
    public Void visitLiteral(LiteralTree node, VisitorContext visitorContext) {
        beanDocConsumer.accept(visitorContext.processLiteral(node));
        return null;
    }

    @Override
    public Void visitParam(ParamTree node, VisitorContext visitorContext) {
        visitorContext.fail("Unexpected: " + node);
        return null;
    }

    @Override
    public Void visitReference(ReferenceTree node, VisitorContext visitorContext) {
        visitorContext.fail("Unexpected: " + node);
        return null;
    }

    @Override
    public Void visitReturn(ReturnTree node, VisitorContext visitorContext) {
        visitorContext.fail("Unexpected: " + node);
        return null;
    }

    @Override
    public Void visitSee(SeeTree node, VisitorContext visitorContext) {
        visitorContext.fail("Unexpected: " + node);
        return null;
    }

    @Override
    public Void visitSerial(SerialTree node, VisitorContext visitorContext) {
        visitorContext.fail("Unexpected: " + node);
        return null;
    }

    @Override
    public Void visitSerialData(SerialDataTree node, VisitorContext visitorContext) {
        visitorContext.fail("Unexpected: " + node);
        return null;
    }

    @Override
    public Void visitSerialField(SerialFieldTree node, VisitorContext visitorContext) {
        visitorContext.fail("Unexpected: " + node);
        return null;
    }

    @Override
    public Void visitSince(SinceTree node, VisitorContext visitorContext) {
        visitorContext.fail("Unexpected: " + node);
        return null;
    }

    @Override
    public Void visitStartElement(StartElementTree node, VisitorContext visitorContext) {
        beanDocConsumer.accept(StandardElement.of(node.toString()));
        return null;
    }

    @Override
    public Void visitText(TextTree node, VisitorContext visitorContext) {
        beanDocConsumer.accept(StandardElement.of(node.toString()));
        return null;
    }

    @Override
    public Void visitThrows(ThrowsTree node, VisitorContext visitorContext) {
        visitorContext.fail("Unexpected: " + node);
        return null;
    }

    @Override
    public Void visitUnknownBlockTag(UnknownBlockTagTree node, VisitorContext visitorContext) {
        visitorContext.fail("Unexpected: " + node);
        return null;
    }

    @Override
    public Void visitUnknownInlineTag(UnknownInlineTagTree node, VisitorContext visitorContext) {
        beanDocConsumer.accept(visitorContext.processUnknownInline(node));
        return null;
    }

    @Override
    public Void visitValue(ValueTree node, VisitorContext visitorContext) {
        visitorContext.fail("Unexpected: " + node);
        return null;
    }

    @Override
    public Void visitVersion(VersionTree node, VisitorContext visitorContext) {
        visitorContext.fail("Unexpected: " + node);
        return null;
    }

    @Override
    public Void visitOther(DocTree node, VisitorContext visitorContext) {
        visitorContext.fail("Unexpected: " + node);
        return null;
    }
}
