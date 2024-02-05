package org.oddjob.doc.visitor;

import com.sun.source.doctree.*;
import org.oddjob.arooa.beandocs.element.*;
import org.oddjob.doc.util.DocUtil;
import org.oddjob.doc.util.ElementDissected;

import javax.lang.model.element.Element;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Visitor for the contents of a Block Tag. Used to process description, and example tags.
 */
public class BlockVisitor implements DocTreeVisitor<Void, VisitorContext> {

    private final Consumer<? super BeanDocElement> beanDocConsumer;

    private BlockVisitor(Consumer<? super BeanDocElement> beanDocConsumer) {
        this.beanDocConsumer = Objects.requireNonNull(beanDocConsumer);
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

        LinkElement linkElement = new LinkElement();

        ReferenceTree rtree = node.getReference();

        linkElement.setSignature(rtree.getSignature());
        linkElement.setLabel(DocUtil.toString(node.getLabel()));

        Element refElement = DocUtil.getReferenceElement(
                visitorContext.getDocTrees(), visitorContext.getElement(), rtree);

        // A null element will be one that can't be resolved.
        if (refElement != null) {
            ElementDissected dissected = ElementDissected.from(refElement, visitorContext::fail);

            linkElement.setQualifiedType(dissected.getQualifiedType());
            linkElement.setPropertyName(dissected.getPropertyName());
        }

        beanDocConsumer.accept(linkElement);

        return null;
    }

    @Override
    public Void visitLiteral(LiteralTree literalTree, VisitorContext visitorContext) {
        BeanDocElement docElement;
        String text = literalTree.getBody().toString();
        switch (literalTree.getKind()) {
            case CODE:
                docElement = CodeElement.of(text);
                break;
            case LITERAL:
                docElement = LiteralElement.of(text);
                break;
            default:
                docElement =  StandardElement.of("[Unsupported Literal Kind " + literalTree.getKind() + "]: " + text);
        }
        beanDocConsumer.accept(docElement);
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
    public Void visitUnknownInlineTag(UnknownInlineTagTree unknownTag, VisitorContext visitorContext) {
        BeanDocElement docElement =  visitorContext.getLoaderProvider()
                .loaderFor(unknownTag.getTagName())
                .map(loader -> loader.load(DocUtil.unknownTagContent(unknownTag)))
                .orElseGet(() -> StandardElement.of(unknownTag.toString()));
        beanDocConsumer.accept(docElement);
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
