/*
 * Copyright (c) 2005, Rob Gordon.
 */
package org.oddjob.doc.doclet;

import jdk.javadoc.doclet.Doclet;
import jdk.javadoc.doclet.DocletEnvironment;
import jdk.javadoc.doclet.Reporter;
import org.oddjob.arooa.ArooaDescriptor;
import org.oddjob.arooa.ArooaType;
import org.oddjob.arooa.beandocs.SessionArooaDocFactory;
import org.oddjob.arooa.beandocs.WriteableArooaDoc;
import org.oddjob.arooa.convert.convertlets.FileConvertlets;
import org.oddjob.arooa.deploy.ClassPathDescriptorFactory;
import org.oddjob.arooa.deploy.LinkedDescriptor;
import org.oddjob.arooa.standard.BaseArooaDescriptor;
import org.oddjob.arooa.standard.StandardArooaSession;
import org.oddjob.arooa.utils.ClassUtils;
import org.oddjob.doc.html.HtmlReferenceWriterFactory;
import org.oddjob.doc.taglet.UnknownInlineLoaderProvider;
import org.oddjob.doc.util.LoaderProvider;

import javax.lang.model.SourceVersion;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.ElementFilter;
import javax.tools.Diagnostic;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

/**
 * The Doclet for creating the Oddjob reference.
 *
 * @author Rob Gordon.
 */
public class ReferenceDoclet implements Doclet {

    private final Options options = new Options();

    private Reporter reporter;

    static ClassLoader classLoaderFor(String classPath) throws MalformedURLException {

        File[] files = FileConvertlets.pathToFiles(classPath);
        URL[] urls = new URL[files.length];
        for (int i = 0; i < files.length; ++i) {
            urls[i] = files[i].toURI().toURL();
        }
        return new URLClassLoader(urls);
    }

    class Main {

        private final JobsAndTypes jats;

        public Main(String classPath, String descriptorResource) throws MalformedURLException {

            SessionArooaDocFactory docsFactory;

            ClassPathDescriptorFactory factory
                    = new ClassPathDescriptorFactory();
            if (descriptorResource != null) {
                factory.setResource(descriptorResource);
            }

            if (classPath == null) {

                ArooaDescriptor descriptor = factory.createDescriptor(
                        getClass().getClassLoader());

                docsFactory = new SessionArooaDocFactory(
                        new StandardArooaSession(descriptor));
            } else {
                ClassLoader classLoader = classLoaderFor(classPath);

                factory.setExcludeParent(true);

                ArooaDescriptor thisDescriptor =
                        factory.createDescriptor(classLoader);

                if (thisDescriptor == null) {
                    throw new NullPointerException("No Descriptor for path " +
                            classPath);
                }

                ArooaDescriptor descriptor =
                        new LinkedDescriptor(
                                thisDescriptor,
                                new BaseArooaDescriptor(classLoader));

                docsFactory = new SessionArooaDocFactory(
                        new StandardArooaSession(), descriptor);
            }

            WriteableArooaDoc jobs =
                    docsFactory.createBeanDocs(ArooaType.COMPONENT);

            WriteableArooaDoc types =
                    docsFactory.createBeanDocs(ArooaType.VALUE);

            this.jats = new JobsAndTypes(jobs, types);
        }

        JobsAndTypes jobsAndTypes() {
            return jats;
        }

        boolean process(DocletEnvironment docEnv, String destination, String title, ClassLoader resourceLoader)
                throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {

            LoaderProvider loaderProvider = new UnknownInlineLoaderProvider(resourceLoader);

            Processor processor = new Processor(docEnv, loaderProvider, reporter);

            final Archiver archiver = new Archiver(jats, processor, reporter);

            boolean result = true;

            for (TypeElement element : ElementFilter.typesIn(docEnv.getIncludedElements())) {

                ElementKind elementKind = element.getKind();

                if (!elementKind.isClass()) {
                    continue;
                }

                try {
                    archiver.archive(element);
                } catch (RuntimeException e) {
                    reporter.print(Diagnostic.Kind.ERROR, element,
                            "Failed Processing " + e);

                    result = false;
                }
            }

            reporter.print(Diagnostic.Kind.NOTE, "Writing Manual with Archive=" + archiver);

            ReferenceWriterFactory writerFactory;
            if (options.writerFactory == null) {
                writerFactory = new HtmlReferenceWriterFactory();
            } else {
                writerFactory = (ReferenceWriterFactory) Class.forName(options.writerFactory)
                        .getConstructor().newInstance();
            }

            writerFactory.setArchive(archiver);
            writerFactory.setDestination(destination);
            writerFactory.setTitle(title);
            writerFactory.setApiLink(Objects.requireNonNullElse(options.apiUrl, "../api"));

            ReferenceWriter referenceWriter = writerFactory.create();
            referenceWriter.createManual(archiver);

            return result;
        }
    }

    @SuppressWarnings("unchecked")
    static <T> T instantiate(String className, Class<? extends T> fallback) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Class<? extends T> use = className == null ? fallback : (Class<? extends T>) Class.forName(className);
        return use.getConstructor().newInstance();
    }

    @Override
    public void init(Locale locale, Reporter reporter) {
        this.reporter = reporter;
    }

    @Override
    public String getName() {
        return getClass().getSimpleName();
    }

    @Override
    public Set<? extends Option> getSupportedOptions() {

        return Set.of(
                new Option() {
                    @Override
                    public int getArgumentCount() {
                        return 1;
                    }

                    @Override
                    public String getDescription() {
                        return "The Destination Directory";
                    }

                    @Override
                    public Kind getKind() {
                        return Kind.STANDARD;
                    }

                    @Override
                    public List<String> getNames() {
                        return List.of("-d");
                    }

                    @Override
                    public String getParameters() {
                        return "directory";
                    }

                    @Override
                    public boolean process(String option, List<String> arguments) {
                        options.destination = arguments.get(0);
                        return true;
                    }
                },
                new Option() {
                    @Override
                    public int getArgumentCount() {
                        return 1;
                    }

                    @Override
                    public String getDescription() {
                        return "Arooa Descriptor Path";
                    }

                    @Override
                    public Kind getKind() {
                        return Kind.STANDARD;
                    }

                    @Override
                    public List<String> getNames() {
                        return List.of("-dp");
                    }

                    @Override
                    public String getParameters() {
                        return "Path";
                    }

                    @Override
                    public boolean process(String option, List<String> arguments) {
                        options.descriptorPath = arguments.get(0);
                        return true;
                    }
                },
                new Option() {
                    @Override
                    public int getArgumentCount() {
                        return 1;
                    }

                    @Override
                    public String getDescription() {
                        return "Arooa Descriptor Resource";
                    }

                    @Override
                    public Kind getKind() {
                        return Kind.STANDARD;
                    }

                    @Override
                    public List<String> getNames() {
                        return List.of("-dr");
                    }

                    @Override
                    public String getParameters() {
                        return "Resource";
                    }

                    @Override
                    public boolean process(String option, List<String> arguments) {
                        options.destination = arguments.get(0);
                        return true;
                    }
                },
                new Option() {
                    @Override
                    public int getArgumentCount() {
                        return 1;
                    }

                    @Override
                    public String getDescription() {
                        return "Title";
                    }

                    @Override
                    public Kind getKind() {
                        return Kind.STANDARD;
                    }

                    @Override
                    public List<String> getNames() {
                        return List.of("-t");
                    }

                    @Override
                    public String getParameters() {
                        return "String";
                    }

                    @Override
                    public boolean process(String option, List<String> arguments) {
                        options.title = arguments.get(0);
                        return true;
                    }
                },
                new Option() {
                    @Override
                    public int getArgumentCount() {
                        return 1;
                    }

                    @Override
                    public String getDescription() {
                        return "Loader Path";
                    }

                    @Override
                    public Kind getKind() {
                        return Kind.STANDARD;
                    }

                    @Override
                    public List<String> getNames() {
                        return List.of("-loaderpath");
                    }

                    @Override
                    public String getParameters() {
                        return "Path";
                    }

                    @Override
                    public boolean process(String option, List<String> arguments) {
                        options.loaderPath = arguments.get(0);
                        return true;
                    }
                },
                new Option() {
                    @Override
                    public int getArgumentCount() {
                        return 1;
                    }

                    @Override
                    public String getDescription() {
                        return "Writer Factory";
                    }

                    @Override
                    public Kind getKind() {
                        return Kind.STANDARD;
                    }

                    @Override
                    public List<String> getNames() {
                        return List.of("-writerfactory");
                    }

                    @Override
                    public String getParameters() {
                        return "Factory Class";
                    }

                    @Override
                    public boolean process(String option, List<String> arguments) {
                        options.writerFactory = arguments.get(0);
                        return true;
                    }
                },
                new Option() {
                    @Override
                    public int getArgumentCount() {
                        return 1;
                    }

                    @Override
                    public String getDescription() {
                        return "URL for the Oddjob API";
                    }

                    @Override
                    public Kind getKind() {
                        return Kind.STANDARD;
                    }

                    @Override
                    public List<String> getNames() {
                        return List.of("-apiurl");
                    }

                    @Override
                    public String getParameters() {
                        return "api-url";
                    }

                    @Override
                    public boolean process(String option, List<String> arguments) {
                        options.apiUrl = arguments.get(0);
                        return true;
                    }
                }
        );
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latest();
    }

    @Override
    public boolean run(DocletEnvironment environment) {

        reporter.print(Diagnostic.Kind.NOTE, "Starting ReferenceDoclet.");

        String loaderPath = options.loaderPath;

        boolean result;

        try {
            ClassLoader resourceClassLoader;

            if (loaderPath == null) {
                resourceClassLoader = getClass().getClassLoader();
            } else {
                resourceClassLoader = classLoaderFor(loaderPath);
            }

            reporter.print(Diagnostic.Kind.NOTE,
                    ClassUtils.classLoaderAndContextLoaderStack(getClass()));
            reporter.print(Diagnostic.Kind.NOTE,
                    ClassUtils.classLoaderStack(resourceClassLoader, "Resource Loader Class Loader"));

            Main md = new Main(
                    options.descriptorPath, options.resource);

            result = md.process(environment, options.destination,
                    options.title, resourceClassLoader);

        } catch (ClassNotFoundException | InvocationTargetException | InstantiationException | NoSuchMethodException |
                 IllegalAccessException | MalformedURLException e) {
            reporter.print(Diagnostic.Kind.ERROR, e.getMessage());
            result = false;
        }

        reporter.print(Diagnostic.Kind.NOTE, "Completed Reference Doclet, result=" + result);

        return result;
    }

    private static class Options {

        private String destination;

        private String descriptorPath;

        private String resource;

        private String title;

        private String loaderPath;

        private String writerFactory;

        private String apiUrl;
    }
}
