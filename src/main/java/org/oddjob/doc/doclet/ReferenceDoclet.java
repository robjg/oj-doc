/*
 * Copyright (c) 2005, Rob Gordon.
 */
package org.oddjob.doc.doclet;

import jdk.javadoc.doclet.Doclet;
import jdk.javadoc.doclet.DocletEnvironment;
import jdk.javadoc.doclet.Reporter;
import org.oddjob.arooa.ArooaDescriptor;
import org.oddjob.arooa.ArooaType;
import org.oddjob.arooa.beandocs.BeanDoc;
import org.oddjob.arooa.beandocs.SessionArooaDocFactory;
import org.oddjob.arooa.beandocs.WriteableArooaDoc;
import org.oddjob.arooa.convert.convertlets.FileConvertlets;
import org.oddjob.arooa.deploy.ClassPathDescriptorFactory;
import org.oddjob.arooa.deploy.LinkedDescriptor;
import org.oddjob.arooa.standard.BaseArooaDescriptor;
import org.oddjob.arooa.standard.StandardArooaSession;
import org.oddjob.doc.taglet.UnknownInlineTagletProvider;
import org.oddjob.doc.util.TagletProvider;

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
import java.util.Set;

/**
 * The Doclet for creating the Oddjob reference.
 *
 * @author Rob Gordon.
 */
public class ReferenceDoclet implements Doclet {

    private final Options options = new Options();

    private Reporter reporter;

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
                File[] files = new FileConvertlets().pathToFiles(classPath);
                URL[] urls = new URL[files.length];
                for (int i = 0; i < files.length; ++i) {
                    urls[i] = files[i].toURI().toURL();
                }
                URLClassLoader classLoader = new URLClassLoader(urls);

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

        boolean process(DocletEnvironment docEnv, String destination, String title) throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {

            TagletProvider tagletProvider = new UnknownInlineTagletProvider(docEnv, ReferenceDoclet.this);

            InlineHelperProvider inlineHelperProvider = new ReferenceHelperProvider(
                    docEnv.getDocTrees(),
                    tagletProvider,
                    fqn -> {
                        BeanDoc beanDoc = jats.docFor(fqn);
                        if (beanDoc == null) {
                            return null;
                        } else {
                            return beanDoc.getName();
                        }
                    },
                    pathToRefRoot -> pathToRefRoot + "/../api"
            );

            Processor processor = new Processor(docEnv, inlineHelperProvider, reporter);

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

            ManualWriter w = new ManualWriter(destination, title);
            w.createManual(archiver);

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
                        return "The destination directory";
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
                        return "arooa-descriptor-path";
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
                        return "arooa-descriptor-resource";
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
                        return "DocConsumer";
                    }

                    @Override
                    public Kind getKind() {
                        return Kind.STANDARD;
                    }

                    @Override
                    public List<String> getNames() {
                        return List.of("-doc-consumer-class");
                    }

                    @Override
                    public String getParameters() {
                        return "String";
                    }

                    @Override
                    public boolean process(String option, List<String> arguments) {
                        options.docConsumerClass = arguments.get(0);
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

        ClassLoader loader = ReferenceDoclet.class.getClassLoader();

        System.out.println("ClassLoader stack:");
        for (ClassLoader next = loader; next != null; next = next.getParent()) {
            System.out.println("  " + next);
        }


        try {
            Main md = new Main(
                    options.getDescriptorPath(), options.getResource());

            return md.process(environment, options.getDestination(),
                    options.getTitle());

        } catch (ClassNotFoundException | InvocationTargetException | InstantiationException | NoSuchMethodException |
                 IllegalAccessException | MalformedURLException e) {
            reporter.print(Diagnostic.Kind.ERROR, e.getMessage());
            return false;
        }
    }

    private static class Options {

        private String destination;

        private String descriptorPath;

        private String resource;

        private String title;

        private String docConsumerClass;

        private String loaderFactoryClass;

        public String getDestination() {
            return destination;
        }

        public String getDescriptorPath() {
            return descriptorPath;
        }

        public String getTitle() {
            return title;
        }

        public String getResource() {
            return resource;
        }

        public String getDocConsumerClass() {
            return docConsumerClass;
        }

        public String getLoaderFactoryClass() {
            return loaderFactoryClass;
        }
    }
}
