package org.oddjob.doc.util;

import org.oddjob.doc.loader.IncludeLoader;

import java.util.Optional;

/**
 * Provides an {@link org.oddjob.doc.loader.IncludeLoader}. Used by the Reference to get the right processing for
 * Oddjob's tags.
 */
public interface LoaderProvider {

    /**
     * Provide an  Include Loader.
     *
     * @param name The name of the tag without the '@'.
     * @return An Optional containing the Inlucde Loader or empty if there isn't one for the given Tag.
     */
    Optional<IncludeLoader> loaderFor(String name);
}
