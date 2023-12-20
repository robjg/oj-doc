package org.oddjob.doc.loader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Base class with common functionality for Loaders.
 */
abstract public class AbstractLoader implements IncludeLoader {

    private static final Logger logger = LoggerFactory.getLogger(AbstractLoader.class);

    private final Loader loader;

    protected AbstractLoader(Loader loader) {
        this.loader = loader;
    }
    protected String doLoad(String fileName) throws IOException {

        FilterFactory filterFactory = new FilterFactory(fileName);

        String resourcePath = filterFactory.getResourcePath();

        try (InputStream inputStream = loader.provideStream(filterFactory.getResourcePath())) {

            return filterFactory.getTextLoader().load(inputStream);
        }
    }

    protected interface Loader {

        InputStream provideStream(String fileName) throws IOException;
    }

    static class FromFile implements Loader {

        private final Path base;

        public FromFile(Path base) {
            this.base = base;
        }

        @Override
        public InputStream provideStream(String fileName) throws IOException {

            Path file = base.resolve(fileName);

            logger.info("Reading file {}", file);

            return Files.newInputStream(file);
        }
    }

    static class FromResource implements Loader {

        private final ClassLoader classLoader;

        FromResource(ClassLoader classLoader) {
            this.classLoader = classLoader;
        }

        @Override
        public InputStream provideStream(String resource) throws IOException {

            InputStream input = classLoader.getResourceAsStream(resource);

            if (input == null) {
                throw new IOException("No Resource Found: path");
            }

            logger.info("Reading resource {}", resource);

            return input;
        }
    }

}
