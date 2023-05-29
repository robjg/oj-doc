package org.oddjob.doc.doclet;

import org.oddjob.doc.util.InlineTagHelper;

import javax.lang.model.element.TypeElement;

/**
 * Provides a processor for inline tags. The intention is to allow different processors for HTML and other formats
 * such as MarkDown.
 */
public interface InlineHelperProvider {

    /**
     * Provide the helper.
     * @param typeElement The Type Element this is a helper for.
     * @return The helper, never null.
     */
    InlineTagHelper forElement(TypeElement typeElement);

}
