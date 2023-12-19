package org.oddjob.doc.doclet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.spi.ToolProvider;

/**
 * Main to run the Reference doclet with default options.
 */
public class ReferenceMain implements Callable<Integer> {

    private static final Logger logger = LoggerFactory.getLogger(ReferenceDoclet.class);

    private String name;

    private String sourcepath;

    private String directory;

    private String packages;

    private ClassLoader classLoader;


    public static void main(String... args) {

        int result = mainCall(args);
        System.exit(result);
    }

    public static int mainCall(String... args) {

        ReferenceMain main = new ReferenceMain();

        for (int i = 0; i < args.length; ++i) {
            String arg = args[i];
            if ("-sourcepath".equals(arg)) {
                main.setSourcepath(args[++i]);
                continue;
            }
            if ("-d".equals(arg)) {
                main.setDirectory(args[++i]);
                continue;
            }
            if ("-xcp".equals(arg)) {
                String xcp = args[++i];
                String[] cps = xcp.split(File.pathSeparator);
                URL[] urls = new URL[cps.length];
                for (int j = 0; j < urls.length; ++j) {
                    try {
                        urls[j] = Path.of(cps[j]).toUri().toURL();
                    } catch (MalformedURLException e) {
                        throw new IllegalArgumentException("Failed creating Extra Classpath from " + xcp, e);
                    }
                }
                main.setClassLoader(URLClassLoader.newInstance(urls));
                continue;
            }
            if (i == args.length - 1) {
                main.setPackages(args[i]);
            }
        }

        return main.call();
    }

    @Override
    public Integer call() {

        String sourcepath = Objects.requireNonNullElse(this.sourcepath, "src/main/java");

        String dest = Objects.requireNonNullElse(this.directory, "docs/reference");

        String packages = Objects.requireNonNullElse(this.packages, "org.oddjob");

        ToolProvider toolProvider = ToolProvider.findFirst("javadoc")
                .orElseThrow(() -> new IllegalArgumentException("No JavaDco"));

        String[] args = { "-doclet", ReferenceDoclet.class.getName(),
                "-sourcepath", sourcepath,
                "-d", dest,
                "-private",
                packages };

        logger.info("Running javadoc with {}", Arrays.toString(args));

        ClassLoader classLoader = Objects.requireNonNullElse(this.classLoader, getClass().getClassLoader());

        ClassLoader existing = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(classLoader);

            return toolProvider.run(System.out, System.err, args);
        }
        finally {
            Thread.currentThread().setContextClassLoader(existing);
        }

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSourcepath() {
        return sourcepath;
    }

    public void setSourcepath(String sourcepath) {
        this.sourcepath = sourcepath;
    }

    public String getDirectory() {
        return directory;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }

    public String getPackages() {
        return packages;
    }

    public void setPackages(String packages) {
        this.packages = packages;
    }

    public ClassLoader getClassLoader() {
        return classLoader;
    }

    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    public String toString() {
        return Objects.requireNonNullElseGet(this.name, () -> getClass().getSimpleName());
    }
}
