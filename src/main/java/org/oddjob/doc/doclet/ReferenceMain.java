package org.oddjob.doc.doclet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
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

    private String classPath;

    private String docletPath;

    private String loaderPath;

    private String writerFactory;

    private boolean verbose;

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
            if ("-loaderpath".equals(arg)) {
                main.setLoaderPath(args[++i]);
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

        List<String> args = new ArrayList<>();
        Optional.ofNullable(this.classPath).ifPresent(dp -> {
            args.add("-classpath");
            args.add(classPath);
        });
        args.add("-doclet");
        args.add(ReferenceDoclet.class.getName());
        Optional.ofNullable(this.docletPath).ifPresent(dp -> {
            args.add("-docletpath");
            args.add(dp);
        });
        args.add("-sourcepath");
        args.add(sourcepath);
        args.add("--ignore-source-errors");
        args.add("-d");
        args.add(dest);
        args.add("-private");
        args.add("-subpackages");
        args.add(packages);
        if (this.verbose) {
            args.add("-verbose");
        }
        Optional.ofNullable(this.loaderPath).ifPresent(lp -> {
            args.add("-loaderpath");
            args.add(lp);
        });
        Optional.ofNullable(this.writerFactory).ifPresent(wf -> {
            args.add("-writerfactory");
            args.add(wf);
        });

        logger.info("Running javadoc with {}", args);

        return toolProvider.run(System.out, System.err, args.toArray(new String[0]));
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

    public String getClassPath() {
        return classPath;
    }

    public void setClassPath(String classPath) {
        this.classPath = classPath;
    }

    public String getDocletPath() {
        return docletPath;
    }

    public void setDocletPath(String docletPath) {
        this.docletPath = docletPath;
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

    public String getLoaderPath() {
        return loaderPath;
    }

    public void setLoaderPath(String loaderPath) {
        this.loaderPath = loaderPath;
    }

    public String getWriterFactory() {
        return writerFactory;
    }

    public void setWriterFactory(String writerFactory) {
        this.writerFactory = writerFactory;
    }

    public boolean isVerbose() {
        return verbose;
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    @Override
    public String toString() {
        return Objects.requireNonNullElseGet(this.name, () -> getClass().getSimpleName());
    }
}
