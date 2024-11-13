package org.oddjob.doc.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
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

    private static final Logger logger = LoggerFactory.getLogger(ExternLinkProvider.class);

    static final String MODULE_START = "module:";

    private final Map<String, LinkPaths> packageLinkProviders = new HashMap<>();

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

        Map<String, LinkPaths.Func> packageResolvers =
                packageLinkProviders.entrySet().stream()
                        .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().apiLinkFor(pathToRoot)));

        return new Processor(packageResolvers);
    }

    public ExternLinkProvider addLinks(Iterable<? extends String> links, Path relativeTo) {
        for (String link : links) {
            if (isRelative(link)) {
                addRelativeLink(link, relativeTo);
            } else {
                addLink(link);
            }
        }
        return this;
    }

    protected boolean isRelative(String apiLink) {

        return !apiLink.contains(":");
    }

    public void addLink(String url) {

        LinkPaths.Provider pathProvider = module -> LinkPaths.absoluteLinkProvider(url, module);

        Map<String, LinkPaths> packages;
        try {
            try {
                packages = loadUri(URI.create(url + "/package-list"), pathProvider);
            } catch (FileNotFoundException e) {
                packages = loadUri(URI.create(url + "/element-list"), pathProvider);
            }
        } catch (IOException e2) {
            errorReporter.accept("Failed to find package/element list at " + url);
            return;
        }

        packageLinkProviders.putAll(packages);
    }

    public void addRelativeLink(String relativePath, Path relativeTo) {

        LinkPaths.Provider pathProvider = module -> LinkPaths.relativeLinkProvider(relativePath, module);

        Map<String, LinkPaths> packages;
        try {
            Path apiPath = relativeTo.resolve(relativePath);

            try {
                packages = loadUri(apiPath.resolve("element-list").toUri(), pathProvider);
            } catch (FileNotFoundException e) {
                packages = loadUri(apiPath.resolve("package-list").toUri(), pathProvider);
            }
        } catch (IOException e2) {
            errorReporter.accept("Failed to find package/element list at " +
                    relativePath + " relative to " + relativeTo);
            return;
        }

        packageLinkProviders.putAll(packages);
    }

    protected Map<String, LinkPaths> loadUri(URI uri, LinkPaths.Provider provider) throws IOException {

        List<String> lines = new BufferedReader(new InputStreamReader(
                uri.toURL().openStream(), StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.toList());

        Map<String, LinkPaths> packagesToPaths = new HashMap<>();

        LinkPaths linkPaths = provider.linkPathsFor("");

        for (String line : lines) {

            // Oracle now just return the api docs for the element-list url
            if (line.contains("<") || line.contains(">")) {
                throw new FileNotFoundException(uri + " contains html. Assuming file not found");
            }

            String trimmed = line.trim();

            if (trimmed.isEmpty()) {
                logger.warn("Unexpected empty line in package list for {}", uri);
                continue;
            }

            if (line.startsWith(MODULE_START)) {
                String module = line.substring(MODULE_START.length());
                linkPaths = provider.linkPathsFor(module);
                continue;
            }

            if (!line.equals(trimmed)) {
                logger.warn("Unexpected whitespace on line [{}] in package list for {}", line, uri);
                continue;
            }

            if (packagesToPaths.containsKey(trimmed)) {
                logger.warn("Unexpected duplicate key for [{}] in package list for {}", trimmed, uri);
            }

            packagesToPaths.put(trimmed, linkPaths);
        }

        return packagesToPaths;
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
