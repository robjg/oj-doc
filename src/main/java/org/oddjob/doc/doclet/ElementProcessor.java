package org.oddjob.doc.doclet;

import javax.lang.model.element.TypeElement;

/**
 * Something that can process a Type Element (i.e. for a class).
 */
public interface ElementProcessor {

    /**
     * Process a Type Element.
     *
     * @param element The Type Element.
     * @param typeConsumersProvider Provides Consumers for a Type
     */
    void process(TypeElement element,
                 TypeConsumersProvider typeConsumersProvider);

}
