package org.oddjob.doc.util;

/**
 * Provides something that can provide relative or absolute paths for linking to
 * the API doc that might be local or on a website.
 */
abstract public class LinkPaths {

    public interface Func {

        String resolve(String apiLink, String extension);
    }

    public abstract Func apiLinkFor(String pathToRoot);

    public static LinkPaths relativeLinkProvider(String apiLink) {
        return new RelativeLinkPath(apiLink);
    }

    public static LinkPaths absoluteLinkProvider(String apiLink) {
        return new AbsoluteLinkPath(apiLink);
    }

    static class RelativeLinkPath extends LinkPaths {

        private final String relativeLink;

        RelativeLinkPath(String relativeLink) {
            this.relativeLink = relativeLink;
        }

        @Override
        public Func apiLinkFor(String pathToRoot) {

            return (qualifiedType, extension) -> {
                String fileName = DocUtil.fileNameFor(qualifiedType, extension);
                return pathToRoot + "/" + relativeLink + "/" + fileName;
            };
        }
    }

    static class AbsoluteLinkPath extends LinkPaths {

        private final String absoluteLink;

        AbsoluteLinkPath(String absoluteLink) {
            this.absoluteLink = absoluteLink;
        }

        @Override
        public Func apiLinkFor(String pathToRoot) {
            return (qualifiedType, extension) -> {
                String fileName = DocUtil.fileNameFor(qualifiedType, extension);
                return absoluteLink + "/" + fileName;
            };
        }
    }

}
