package org.oddjob.doc.util;

import java.util.function.Function;

/**
 * Provides something that can provide relative or absolute paths for linking to
 * the API doc that might be local or on a website.
 */
abstract public class ApiLinkProvider {

    /**
     * Provide a function the can take a file name and create a path either
     * based on the path to the root or not depending on if the provider is local or not.
     *
     * @param pathToRoot The path to the API root. May or may not be used.
     *
     * @return a function that will create a path to the given file name.
     */
    public abstract Function<String, String> apiLinkFor(String pathToRoot);

    public static ApiLinkProvider providerFor(String apiLink) {

        if (apiLink.contains(":")) {
            return new AbsoluteApiLink(apiLink);
        }
        else {
            return new RelativeApiLink(apiLink);
        }
    }

    static class RelativeApiLink extends ApiLinkProvider {

        private final String relativeLink;

        RelativeApiLink(String relativeLink) {
            this.relativeLink = relativeLink;
        }

        @Override
        public Function<String, String> apiLinkFor(String pathToRoot) {
            return fileName -> pathToRoot + "/" + relativeLink + "/" + fileName;
        }
    }

    static class AbsoluteApiLink extends ApiLinkProvider {

        private final String absoluteLink;

        AbsoluteApiLink(String absoluteLink) {
            this.absoluteLink = absoluteLink;
        }

        @Override
        public Function<String, String> apiLinkFor(String pathToRoot) {
            return fileName -> absoluteLink + "/" + fileName;
        }
    }

}
