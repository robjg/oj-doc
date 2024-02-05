package org.oddjob.doc.visitor;

import com.sun.source.util.DocTrees;
import org.oddjob.doc.util.LoaderProvider;

import javax.lang.model.element.Element;

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

    LoaderProvider getLoaderProvider();

    Element getElement();

    DocTrees getDocTrees();
}
