package org.oddjob.doc.visitor;

import com.sun.source.doctree.LinkTree;
import com.sun.source.doctree.LiteralTree;
import com.sun.source.doctree.UnknownInlineTagTree;

/**
 * Used for visiting Doc Tree Nodes.
 *
 * @see BlockVisitor
 */
public interface VisitorContext {

    void warn(String msg);

    void fail(String msg);

    String processLink(LinkTree linkTag);

    String processUnknownInline(UnknownInlineTagTree unknownTag);

    String processLiteral(LiteralTree literalTree);
}
