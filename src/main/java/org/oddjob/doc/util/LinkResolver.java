package org.oddjob.doc.util;

import java.util.Optional;

/**
 * Resolve a link to external documentation.
 */
@FunctionalInterface
public interface LinkResolver {

    Optional<String> resolve(String qualifiedName, String extension);
}
