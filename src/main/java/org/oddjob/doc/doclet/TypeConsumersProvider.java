package org.oddjob.doc.doclet;

import org.oddjob.doc.beandoc.TypeConsumers;

import javax.lang.model.element.TypeElement;

/**
 * Something that maybe provide document consumers for a type if we are interested in it.
 * Used by a {@link Processor} to provide what might provide something to consume the documentation.
 */
public interface TypeConsumersProvider {

    /**
     * Provides consumers for the java doc if required for the given type element.
     *
     * @param element The type element.
     * @return Doc Consumers for the type or null if javadoc for the type is not to be processed.
     */
    TypeConsumers typeConsumersFor(TypeElement element);
}
