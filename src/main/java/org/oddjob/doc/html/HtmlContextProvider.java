package org.oddjob.doc.html;

/**
 * Provides an {@link HtmlContext}. The context will be different for each class as the
 * relative links will be different.
 */
public interface HtmlContextProvider {

    /**
     * Provide the Context.
     *
     * @param pathToRoot The Path to the root directory for relative links.
     *
     * @return The Context, never null.
     */
    HtmlContext contextFor(String pathToRoot);

}
