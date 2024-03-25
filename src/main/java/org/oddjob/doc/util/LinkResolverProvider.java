package org.oddjob.doc.util;

/**
 * Something that can provide a {@link LinkResolver}.
 */
public interface LinkResolverProvider {

    /**
     * Provide a function the can take a file name or url and create a path either
     * based on the path to the root or not depending on if the provider is local or not.
     *
     * @param pathToRoot The path to the API root. May or may not be used.
     *
     * @return a function that will create a path to the given file name.
     */
    LinkResolver apiLinkFor(String pathToRoot);
}
