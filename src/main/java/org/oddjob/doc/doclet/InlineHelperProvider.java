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
     * @param pathToRoot The Path to the root directory for relative links.
     *
     * @return The helper, never null.
     */
    InlineTagHelper forElement(String pathToRoot);

}
