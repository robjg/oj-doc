package org.oddjob.doc.doclet;

import org.oddjob.doc.beandoc.TypeConsumers;
import org.oddjob.doc.beandoc.TypeElementIdentifier;

/**
 * Something that maybe provide document consumers for a type if we are interested in it.
 * Used by a {@link Processor} to provide what might provide something to consume the documentation.
 */
public interface TypeConsumersProvider {

    /**
     * Provides consumers for the java doc if required for the given type element.
     *
     * @param typeIdentifier The type element identifier.
     * @return Doc Consumers for the type or null if javadoc for the type is not to be processed.
     */
    TypeConsumers typeConsumersFor(TypeElementIdentifier typeIdentifier);
}
