package org.oddjob.doc.markdown;

import org.oddjob.arooa.beandocs.element.LinkElement;

/**
 * Context for an {@link MdVisitor}.
 */
public interface MdContext {

    String processLink(LinkElement linkElement);

}
