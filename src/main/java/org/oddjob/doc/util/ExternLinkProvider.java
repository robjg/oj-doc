package org.oddjob.doc.util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Finds links for packages. Unfortunately we can't get at the JDK implementation of
 * this that is in the {@code jdk.javadoc.internal.doclets.toolkit.util.Extern} so we have to
 * write our own.
 * <p>
 * Given a URL this will check the package-list or element-list and accumulate a
 * directory of packages to {@link LinkPaths}.
 * </p>
 */
public class ExternLinkProvider implements LinkResolverProvider {

    private final List<LinkPaths> linkProviders = new LinkedList<>();

    private final Map<String, Integer> packageLinkProviders = new HashMap<>();

    private final Consumer<? super String> errorReporter;

    private ExternLinkProvider(Consumer<? super String> errorReporter) {
        this.errorReporter = errorReporter;
    }

    public static ExternLinkProvider throwingException() {
        return new ExternLinkProvider(msg -> {
            throw new IllegalArgumentException(msg);
        });
    }

    public static ExternLinkProvider withErrorReporter(Consumer<? super String> errorReporter) {
        return new ExternLinkProvider(errorReporter);
    }

    @Override
    public LinkResolver apiLinkFor(String pathToRoot) {

        List<LinkPaths.Func> linkResolvers = linkProviders.stream()
                .map(linkProvider -> linkProvider.apiLinkFor(pathToRoot))
                .collect(Collectors.toList());

        Map<String, LinkPaths.Func> packageResolvers =
                packageLinkProviders.entrySet().stream()
                        .collect(Collectors.toMap(Map.Entry::getKey, e -> linkResolvers.get(e.getValue())));

        return new Processor(packageResolvers);
    }

    public ExternLinkProvider addLinks(Iterable<? extends String> links, Path relativeTo) {
        for (String link : links) {
            if (isRelative(link)) {
                addRelativeLink(link, relativeTo);
            }
            else {
                addLink(link);
            }
        }
        return this;
    }

    protected boolean isRelative(String apiLink) {

        return !apiLink.contains(":");
    }

    public void addLink(String url) {

        int index = linkProviders.size();
        Map<String, Integer> packages;
        try {
            try {
                packages = loadUri(URI.create(url + "/package-list"), index);
            } catch (FileNotFoundException e) {
                packages = loadUri(URI.create(url + "/element-list"), index);
            }
        } catch (IOException e2) {
            errorReporter.accept("Failed to find package/element list at " + url);
            return;
        }

        packageLinkProviders.putAll(packages);
        linkProviders.add(LinkPaths.absoluteLinkProvider(url));
    }

    public void addRelativeLink(String relativePath, Path relativeTo) {

        int index = linkProviders.size();
        Map<String, Integer> packages;
        try {
            Path apiPath = relativeTo.resolve(relativePath);

            try {
                packages = loadUri(apiPath.resolve("element-list").toUri(), index);
            } catch (FileNotFoundException e) {
                packages = loadUri(apiPath.resolve("package-list").toUri(), index);
            }
        } catch (IOException e2) {
            errorReporter.accept("Failed to find package/element list at " +
                    relativePath + " relative to " + relativeTo);
            return;
        }

        packageLinkProviders.putAll(packages);
        linkProviders.add(LinkPaths.relativeLinkProvider(relativePath));
    }

    protected Map<String, Integer> loadUri(URI uri, Integer index) throws IOException {

        return new BufferedReader(new InputStreamReader(
                uri.toURL().openStream(), StandardCharsets.UTF_8))
                .lines()
                .filter(s -> !s.isBlank())
                .collect(Collectors.toMap(Function.identity(), s -> index));
    }

    static class Processor implements LinkResolver {

        private final Map<String, LinkPaths.Func> packageResolvers;

        Processor(Map<String, LinkPaths.Func> packageResolvers) {
            this.packageResolvers = packageResolvers;
        }

        @Override
        public Optional<String> resolve(String qualifiedName, String extension) {

            return Optional.ofNullable(packageResolvers.get(DocUtil.packageName(qualifiedName)))
                    .map(linkResolver -> linkResolver.resolve(qualifiedName, extension));
        }
    }
}
