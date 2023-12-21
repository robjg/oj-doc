package org.oddjob.doc.visitor;

import com.sun.source.doctree.LinkTree;
import com.sun.source.doctree.LiteralTree;
import com.sun.source.doctree.UnknownInlineTagTree;
import org.oddjob.arooa.beandocs.element.BeanDocElement;

/**
 * Used for visiting Doc Tree Nodes.
 *
 * @see BlockVisitor
 */
public interface VisitorContext {

    void info(String msg);

    void warn(String msg);

    void fail(String msg);

    void fail(String msg, Exception e);

    String processLink(LinkTree linkTag);

    BeanDocElement processUnknownInline(UnknownInlineTagTree unknownTag);

    String processLiteral(LiteralTree literalTree);
}
