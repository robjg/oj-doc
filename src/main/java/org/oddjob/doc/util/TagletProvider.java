package org.oddjob.doc.util;

import jdk.javadoc.doclet.Taglet;

import java.util.Optional;

/**
 * Provides a Taglet. Used by the Reference to get the right processing for Oddjobs tags.
 */
public interface TagletProvider {

    /**
     * Provide a Taglet.
     *
     * @param name The name of the tag without the '@'.
     * @return An Optional containing the Taglet or empty if there isn't one for the given Tag.
     */
    Optional<Taglet> tagletFor(String name);
}
