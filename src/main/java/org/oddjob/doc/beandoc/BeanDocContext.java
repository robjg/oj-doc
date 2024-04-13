package org.oddjob.doc.beandoc;

import org.oddjob.arooa.beandocs.element.DocElementVisitor;

/**
 * A Context capable of providing a visitor for itself.
 *
 * @param <C> The type of this context
 */
public interface BeanDocContext<C extends BeanDocContext<C>> {

    DocElementVisitor<C, String> docElementVisitor();
}
