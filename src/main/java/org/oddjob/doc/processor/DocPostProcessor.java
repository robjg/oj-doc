package org.oddjob.doc.processor;

import org.oddjob.arooa.beandocs.element.BeanDocElement;
import org.oddjob.arooa.beandocs.element.DocElementVisitor;
import org.oddjob.doc.beandoc.BeanDocContext;
import org.oddjob.doc.doclet.CustomTagNames;
import org.oddjob.doc.html.HtmlContext;
import org.oddjob.doc.loader.IncludeLoader;
import org.oddjob.doc.loader.JavaCodeLoader;
import org.oddjob.doc.loader.PlainTextLoader;
import org.oddjob.doc.loader.XmlLoader;

import java.io.*;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Post Process a single doc.
 */
public class DocPostProcessor {

    private final Injector[] injectors;

    private DocPostProcessor(Injector[] injectors) {
        this.injectors = injectors;
    }

    public static DocPostProcessor of(Path baseDir) {
        return of(baseDir, null);
    }

    public static DocPostProcessor of(Path baseDir, ClassLoader classLoader) {

        classLoader = Objects.requireNonNullElse(classLoader,
                DocPostProcessor.class.getClassLoader());

        Injector[] injectors = new Injector[]{
                new JavaCodeInjector(baseDir),
                new XMLResourceInjector(classLoader),
                new XMLFileInjector(baseDir),
                new GenericInjector(CustomTagNames.TEXT_FILE_TAG,
                        PlainTextLoader.fromFile(baseDir)),
                new GenericInjector(CustomTagNames.TEXT_RESOURCE_TAG,
                        PlainTextLoader.fromResource(classLoader))
        };

        return new DocPostProcessor(injectors);
    }

    public void process(InputStream input, OutputStream output) throws IOException {

        process(input, output, HtmlContext.noLinks());
    }

    public <C extends BeanDocContext<C>> void process(
            InputStream input, OutputStream output, C beanDocContext) throws IOException {

        DocElementVisitor<C, String> htmlVisitor = beanDocContext.docElementVisitor();

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(input));

        PrintWriter writer = new PrintWriter(
                new OutputStreamWriter(output));

        try {
            while (true) {
                String line = reader.readLine();
                if (line == null) {
                    break;
                }

                boolean replaced = false;
                for (Injector injector : injectors) {

                    Optional<BeanDocElement> optionalElement = injector.parse(line);
                    if (optionalElement.isPresent()) {
                        String html = optionalElement.get()
                                .accept(htmlVisitor, beanDocContext);
                        writer.println(html);
                        replaced = true;
                        break;
                    }
                }

                if (!replaced) {
                    writer.println(line);
                }
            }
        } finally {
            try {
                reader.close();
            } finally {
                writer.close();
            }
        }
    }


    interface Injector {

        Optional<BeanDocElement> parse(String line);
    }

    static class JavaCodeInjector implements Injector {

        final Pattern pattern = Pattern.compile("\\{\\s*" +
                CustomTagNames.JAVA_FILE_TAG + "\\s*(\\S+)\\s*\\}");

        private final JavaCodeLoader javaCodeLoader;

        JavaCodeInjector(Path baseDir) {
            this.javaCodeLoader = JavaCodeLoader.fromFile(baseDir);
        }

        @Override
        public Optional<BeanDocElement> parse(String line) {

            Matcher matcher = pattern.matcher(line);

            if (!matcher.find()) {
                return Optional.empty();
            }

            return Optional.of(javaCodeLoader.load(matcher.group(1)));
        }
    }

    static class XMLResourceInjector implements Injector {

        final static Pattern pattern = Pattern.compile("\\{\\s*" +
                CustomTagNames.XML_RESOURCE_TAG + "\\s*(\\S+)\\s*\\}");

        private final XmlLoader resourceLoader;

        XMLResourceInjector(ClassLoader classLoader) {
            this.resourceLoader = XmlLoader.fromResource(classLoader);
        }

        @Override
        public Optional<BeanDocElement> parse(String line) {

            Matcher matcher = pattern.matcher(line);

            if (!matcher.find()) {
                return Optional.empty();
            }

            return Optional.of(resourceLoader.load(matcher.group(1)));
        }
    }

    static class XMLFileInjector implements Injector {

        final Pattern pattern = Pattern.compile("\\{\\s*" +
                CustomTagNames.XML_FILE_TAG + "\\s*(\\S+)\\s*\\}");

        private final XmlLoader xmlLoader;

        XMLFileInjector(Path baseDir) {
            this.xmlLoader = XmlLoader.fromFile(baseDir);
        }

        @Override
        public Optional<BeanDocElement> parse(String line) {

            Matcher matcher = pattern.matcher(line);

            if (!matcher.find()) {
                return Optional.empty();
            }

            return Optional.of(xmlLoader.load(matcher.group(1)));
        }
    }

    static class GenericInjector implements Injector {

        final Pattern pattern;

        private final IncludeLoader loader;

        public GenericInjector(String tag, IncludeLoader loader) {
            this.pattern = Pattern.compile("\\{\\s*" +
                    tag + "\\s*(\\S+)\\s*\\}");
            this.loader = loader;
        }

        @Override
        public Optional<BeanDocElement> parse(String line) {

            Matcher matcher = pattern.matcher(line);

            if (!matcher.find()) {
                return Optional.empty();
            }

            return Optional.of(loader.load(matcher.group(1)));
        }
    }
}
