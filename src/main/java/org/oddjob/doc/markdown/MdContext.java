package org.oddjob.doc.markdown;

import org.oddjob.arooa.beandocs.element.DocElementVisitor;
import org.oddjob.doc.beandoc.BeanDocContext;
import org.oddjob.doc.util.LinkProcessor;

/**
 * Context for an {@link MdVisitor}.
 */
public interface MdContext extends LinkProcessor, BeanDocContext<MdContext> {

    static MdContext noLinks() {
        return linkElement -> {
            throw new UnsupportedOperationException();
        };
    }


    @Override
    default DocElementVisitor<MdContext, String> docElementVisitor() {
        return MdVisitor.instance();
    }
}
