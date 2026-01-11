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
import org.oddjob.arooa.beandocs.WriteableConversionDocs;
import org.oddjob.arooa.convert.convertlets.FileConvertlets;
import org.oddjob.arooa.deploy.ClassPathDescriptorFactory;
import org.oddjob.arooa.deploy.ListDescriptor;
import org.oddjob.arooa.deploy.URLDescriptorFactory;
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
import java.net.*;
import java.util.*;

/**
 * The Doclet for creating the Oddjob reference.
 *
 * @author Rob Gordon.
 */
public class ReferenceDoclet implements Doclet {

    public static final String DESTINATION_OPTION = "-d";

    public static final String DESCRIPTOR_URL_OPTION = "-descriptorurl";

    public static final String TITLE_OPTION = "-t";

    public static final String LOADER_PATH_OPTION = "-loaderpath";

    public static final String WRITER_FACTORY_OPTION = "-writerfactory";

    public static final String API_URL_OPTION = "-link";

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

    SessionArooaDocFactory loadDescriptor(List<String> descriptorUrls) throws MalformedURLException, URISyntaxException {

        if (descriptorUrls == null || descriptorUrls.isEmpty()) {

            ClassPathDescriptorFactory factory
                    = new ClassPathDescriptorFactory();

            this.reporter.print(Diagnostic.Kind.NOTE, "Finding Arooa Descriptor " +
                    factory.getResource() + " on doclet classpath");

            ArooaDescriptor descriptor = factory.createDescriptor(
                    ReferenceDoclet.class.getClassLoader());

            return new SessionArooaDocFactory(
                    new StandardArooaSession(descriptor));

        } else {

            this.reporter.print(Diagnostic.Kind.NOTE, "Finding Arooa Descriptor " +
                    descriptorUrls);

            List<ArooaDescriptor> arooaDescriptors = new LinkedList<>();

            for (String descriptorUrl: descriptorUrls) {

                URLDescriptorFactory urlDescriptorFactory = new URLDescriptorFactory(
                        new URI(descriptorUrl).toURL());

                ArooaDescriptor descriptor = urlDescriptorFactory.createDescriptor(
                        getClass().getClassLoader());

                arooaDescriptors.add(descriptor);
            }

            ArooaDescriptor descriptor;
            if (arooaDescriptors.size() == 1) {
                descriptor = arooaDescriptors.getFirst();
            }
            else {
                descriptor = new ListDescriptor(arooaDescriptors);
            }

            return new SessionArooaDocFactory(
                    new StandardArooaSession(), descriptor);
        }
    }

    class Main {

        private final JobsAndTypes jats;

        private final Conversions conversions;

        public Main(List<String> descriptorUrl) throws MalformedURLException, URISyntaxException {

            SessionArooaDocFactory docsFactory = loadDescriptor(descriptorUrl);

            WriteableArooaDoc jobs =
                    docsFactory.createBeanDocs(ArooaType.COMPONENT);

            WriteableArooaDoc types =
                    docsFactory.createBeanDocs(ArooaType.VALUE);

            this.jats = new JobsAndTypes(jobs, types);

            WriteableConversionDocs conversionsByType =
                    docsFactory.createConversionDocs();

            this.conversions = new Conversions(conversionsByType);
        }

        JobsAndTypes jobsAndTypes() {
            return jats;
        }

        boolean process(DocletEnvironment docEnv,
                        String destination,
                        String title,
                        ClassLoader resourceLoader)
                throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {

            LoaderProvider loaderProvider = new UnknownInlineLoaderProvider(resourceLoader);

            Processor processor = new Processor(docEnv, loaderProvider, reporter);

            final Archiver archiver = new Archiver(jats, conversions, processor, reporter);

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

            reporter.print(Diagnostic.Kind.NOTE, "Writing Manual with Archive=" + archiver +
                    " to " + destination);

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
            writerFactory.setApiLinks(options.links.isEmpty() ? List.of("../api") : options.links);
            writerFactory.setErrorConsumer(message -> reporter.print(Diagnostic.Kind.WARNING, message));

            ReferenceWriter referenceWriter = writerFactory.create();
            referenceWriter.createManual(archiver, archiver);

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
                        return List.of(DESTINATION_OPTION);
                    }

                    @Override
                    public String getParameters() {
                        return "directory";
                    }

                    @Override
                    public boolean process(String option, List<String> arguments) {
                        options.destination = arguments.getFirst();
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
                        return "Arooa Descriptor URL";
                    }

                    @Override
                    public Kind getKind() {
                        return Kind.STANDARD;
                    }

                    @Override
                    public List<String> getNames() {
                        return List.of(DESCRIPTOR_URL_OPTION);
                    }

                    @Override
                    public String getParameters() {
                        return "URL";
                    }

                    @Override
                    public boolean process(String option, List<String> arguments) {
                        options.descriptorUrls.add(arguments.getFirst());
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
                        return List.of(TITLE_OPTION);
                    }

                    @Override
                    public String getParameters() {
                        return "String";
                    }

                    @Override
                    public boolean process(String option, List<String> arguments) {
                        options.title = arguments.getFirst();
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
                        return List.of(LOADER_PATH_OPTION);
                    }

                    @Override
                    public String getParameters() {
                        return "Path";
                    }

                    @Override
                    public boolean process(String option, List<String> arguments) {
                        options.loaderPath = arguments.getFirst();
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
                        return List.of(WRITER_FACTORY_OPTION);
                    }

                    @Override
                    public String getParameters() {
                        return "Factory Class";
                    }

                    @Override
                    public boolean process(String option, List<String> arguments) {
                        options.writerFactory = arguments.getFirst();
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
                        return "Links to external Javadoc";
                    }

                    @Override
                    public Kind getKind() {
                        return Kind.STANDARD;
                    }

                    @Override
                    public List<String> getNames() {
                        return List.of(API_URL_OPTION);
                    }

                    @Override
                    public String getParameters() {
                        return "link-url";
                    }

                    @Override
                    public boolean process(String option, List<String> arguments) {
                        options.links.add(arguments.getFirst());
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
                    options.descriptorUrls);

            result = md.process(environment, options.destination,
                    options.title, resourceClassLoader);

        } catch (ClassNotFoundException | InvocationTargetException | InstantiationException | NoSuchMethodException |
                 IllegalAccessException | MalformedURLException | URISyntaxException e) {
            reporter.print(Diagnostic.Kind.ERROR, e.getMessage());
            result = false;
        }

        reporter.print(Diagnostic.Kind.NOTE, "Completed Reference Doclet, result=" + result);

        return result;
    }

    private static class Options {

        private String destination;

        private final List<String> descriptorUrls = new ArrayList<>();

        private String title;

        private String loaderPath;

        private String writerFactory;

        private final List<String> links = new ArrayList<>();
    }
}
