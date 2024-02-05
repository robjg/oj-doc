package org.oddjob.doc.doclet;

import org.oddjob.doc.util.InlineTagHelper;

/**
 * Provides a processor for inline tags. The intention is to allow different processors for HTML and other formats
 * such as MarkDown.
 */
public interface InlineHelperProvider {

    /**
     * Provide the helper.
     *
     * @param qualifiedClassName The Name of the Components Class that this is a helper for.
     *
     * @return The helper, never null.
     */
    InlineTagHelper forElement(String qualifiedClassName);

}
