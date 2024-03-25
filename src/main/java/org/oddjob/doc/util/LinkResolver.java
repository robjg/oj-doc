package org.oddjob.doc.util;

/**
 * Resolve a link to external documentation.
 */
@FunctionalInterface
public interface LinkResolver {

    String resolve(String qualifiedName, String extension);
}
