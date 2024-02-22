package org.oddjob.doc.markdown;

/**
 * Provides a Context for an {@link MdVisitor}.
 */
public interface MdContextProvider {

    MdContext contextFor(String pathToRoot);
}
