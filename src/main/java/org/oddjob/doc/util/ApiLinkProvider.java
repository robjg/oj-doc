package org.oddjob.doc.util;

/**
 * Provides something that can provide relative or absolute paths for linking to
 * the API doc that might be local or on a website.
 */
abstract public class ApiLinkProvider implements LinkResolverProvider {


    public static LinkResolverProvider relativeLinkProvider(String apiLink) {
        return new RelativeApiLink(apiLink);
    }

    public static LinkResolverProvider absoluteLinkProvider(String apiLink) {
        return new AbsoluteApiLink(apiLink);
    }

    static class RelativeApiLink extends ApiLinkProvider {

        private final String relativeLink;

        RelativeApiLink(String relativeLink) {
            this.relativeLink = relativeLink;
        }

        @Override
        public LinkResolver apiLinkFor(String pathToRoot) {

            return (qualifiedType, extension) -> {
                String fileName = DocUtil.fileNameFor(qualifiedType, extension);
                return pathToRoot + "/" + relativeLink + "/" + fileName;
            };
        }
    }

    static class AbsoluteApiLink extends ApiLinkProvider {

        private final String absoluteLink;

        AbsoluteApiLink(String absoluteLink) {
            this.absoluteLink = absoluteLink;
        }

        @Override
        public LinkResolver apiLinkFor(String pathToRoot) {
            return (qualifiedType, extension) -> {
                String fileName = DocUtil.fileNameFor(qualifiedType, extension);
                return absoluteLink + "/" + fileName;
            };
        }
    }

}
