package org.oddjob.doc.visitor;

import com.sun.source.doctree.*;

/**
 * Visitor with no operations intended for subclasses that ignore most things in the
 * document tree.
 */
public class NoopVisitor implements DocTreeVisitor<Void, VisitorContext> {

    @Override
    public Void visitAttribute(AttributeTree node, VisitorContext visitorContext) {
        return null;
    }

    @Override
    public Void visitAuthor(AuthorTree node, VisitorContext visitorContext) {
        return null;
    }

    @Override
    public Void visitComment(CommentTree node, VisitorContext visitorContext) {
        return null;
    }

    @Override
    public Void visitDeprecated(DeprecatedTree node, VisitorContext visitorContext) {
        return null;
    }

    @Override
    public Void visitDocComment(DocCommentTree node, VisitorContext visitorContext) {
        return null;
    }

    @Override
    public Void visitDocRoot(DocRootTree node, VisitorContext visitorContext) {
        return null;
    }

    @Override
    public Void visitEndElement(EndElementTree node, VisitorContext visitorContext) {
        return null;
    }

    @Override
    public Void visitEntity(EntityTree node, VisitorContext visitorContext) {
        return null;
    }

    @Override
    public Void visitErroneous(ErroneousTree node, VisitorContext visitorContext) {
        return null;
    }

    @Override
    public Void visitIdentifier(IdentifierTree node, VisitorContext visitorContext) {
        return null;
    }

    @Override
    public Void visitInheritDoc(InheritDocTree node, VisitorContext visitorContext) {
        return null;
    }

    @Override
    public Void visitLink(LinkTree node, VisitorContext visitorContext) {
        return null;
    }

    @Override
    public Void visitLiteral(LiteralTree node, VisitorContext visitorContext) {
        return null;
    }

    @Override
    public Void visitParam(ParamTree node, VisitorContext visitorContext) {
        return null;
    }

    @Override
    public Void visitReference(ReferenceTree node, VisitorContext visitorContext) {
        return null;
    }

    @Override
    public Void visitReturn(ReturnTree node, VisitorContext visitorContext) {
        return null;
    }

    @Override
    public Void visitSee(SeeTree node, VisitorContext visitorContext) {
        return null;
    }

    @Override
    public Void visitSerial(SerialTree node, VisitorContext visitorContext) {
        return null;
    }

    @Override
    public Void visitSerialData(SerialDataTree node, VisitorContext visitorContext) {
        return null;
    }

    @Override
    public Void visitSerialField(SerialFieldTree node, VisitorContext visitorContext) {
        return null;
    }

    @Override
    public Void visitSince(SinceTree node, VisitorContext visitorContext) {
        return null;
    }

    @Override
    public Void visitStartElement(StartElementTree node, VisitorContext visitorContext) {
        return null;
    }

    @Override
    public Void visitText(TextTree node, VisitorContext visitorContext) {
        return null;
    }

    @Override
    public Void visitThrows(ThrowsTree node, VisitorContext visitorContext) {
        return null;
    }

    @Override
    public Void visitUnknownBlockTag(UnknownBlockTagTree node, VisitorContext visitorContext) {
        return null;
    }

    @Override
    public Void visitUnknownInlineTag(UnknownInlineTagTree node, VisitorContext visitorContext) {
        return null;
    }

    @Override
    public Void visitValue(ValueTree node, VisitorContext visitorContext) {
        return null;
    }

    @Override
    public Void visitVersion(VersionTree node, VisitorContext visitorContext) {
        return null;
    }

    @Override
    public Void visitOther(DocTree node, VisitorContext visitorContext) {
        return null;
    }
}
