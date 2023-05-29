package org.oddjob.doc.doclet;

import org.oddjob.doc.beandoc.TypeConsumers;

import javax.lang.model.element.TypeElement;

/**
 * Something that can process a Type Element (i.e. for a class).
 */
public interface ElementProcessor {

    /**
     * Process a Type Element.
     *
     * @param element The Type Element.
     * @param typeConsumers The Consumer for a Type
     */
    void process(TypeElement element, TypeConsumers typeConsumers);

}
