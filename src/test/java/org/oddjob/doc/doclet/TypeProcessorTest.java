package org.oddjob.doc.doclet;

import com.sun.source.doctree.DocTree;
import com.sun.source.util.DocTrees;
import jdk.javadoc.doclet.Doclet;
import jdk.javadoc.doclet.DocletEnvironment;
import jdk.javadoc.doclet.Reporter;
import jdk.javadoc.doclet.Taglet;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.io.FileMatchers;
import org.junit.jupiter.api.Test;
import org.oddjob.OurDirs;
import org.oddjob.arooa.beandocs.element.BeanDocElement;
import org.oddjob.arooa.beandocs.element.LinkElement;
import org.oddjob.arooa.beandocs.element.StandardElement;
import org.oddjob.doc.html.HtmlContext;
import org.oddjob.doc.html.HtmlVisitor;
import org.oddjob.doc.loader.IncludeLoader;
import org.oddjob.doc.taglet.UnknownInlineLoaderProvider;
import org.oddjob.doc.util.LoaderProvider;

import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.nio.file.Path;
import java.util.*;
import java.util.spi.ToolProvider;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.mock;

class TypeProcessorTest {

    static Reporter reporter;

    static CaptureConsumer.Type beanDocConsumer;

    static LoaderProvider loaderProvider;

    @Test
    void testSingleClass() {

        Path srcPath = OurDirs.relativePath("src/test/java/org/oddjob/doc/doclet/ThingWithSomeDoc.java");
        Path srcPath2 = OurDirs.relativePath("src/test/java/org/oddjob/doc/doclet/ThingWithSomeDocBase.java");

        reporter = mock(Reporter.class);

        beanDocConsumer = new CaptureConsumer.Type();

        loaderProvider = new OurLoaderProvider();

        ToolProvider toolProvider = ToolProvider.findFirst("javadoc")
                .orElseThrow(() -> new IllegalArgumentException("No JavaDco"));
        int result = toolProvider.run(System.out, System.err,
                "-doclet", TestDoclet.class.getName(),
                srcPath.toString(), srcPath2.toString());

        assertThat(result, Matchers.is(0));

        HtmlContext context = new OurHtmlContext();

        assertThat(HtmlVisitor.visitAll(beanDocConsumer.description().getFirstSentence(), context),
                is("First sentence in block tag."));

        assertThat(HtmlVisitor.visitAll(beanDocConsumer.description().getBody(), context),
                is(
                "First sentence in block tag. Some more stuff in the block tag. LINK: ThingWithSomeDoc.\n" +
                        " ** WORKED **. <p>And some html</p>."));

        CaptureConsumer exampleConsumer = beanDocConsumer.getExample(0);
        assertThat(HtmlVisitor.visitAll(exampleConsumer.getFirstSentence(), context),
                is("This is an example."));
        assertThat(HtmlVisitor.visitAll(exampleConsumer.getBody(), context),
                is("This is an example.\n\n Which can go over several lines.\n\n ** WORKED **"));
        assertThat(exampleConsumer.isClosed(), is(true));

        CaptureConsumer somePropConsumer = beanDocConsumer.getProperty("someProp");

        assertThat(HtmlVisitor.visitAll(somePropConsumer.getFirstSentence(), context),
                is("Some property"));
        assertThat(HtmlVisitor.visitAll(somePropConsumer.getBody(), context),
                is("Some property"));
        assertThat(somePropConsumer.isClosed(), is(true));

        CaptureConsumer.Property anotherPropConsumer = beanDocConsumer.getProperty("anotherProp");

        assertThat(HtmlVisitor.visitAll(anotherPropConsumer.getFirstSentence(), context),
                is("Another property"));
        assertThat(HtmlVisitor.visitAll(anotherPropConsumer.getBody(), context),
                is("Another property"));
        assertThat(anotherPropConsumer.getRequired(), is("Yes"));
        assertThat(anotherPropConsumer.isClosed(), is(true));

        CaptureConsumer.Property superPropConsumer = beanDocConsumer.getProperty("superProp");

        assertThat(HtmlVisitor.visitAll(superPropConsumer.getFirstSentence(), context),
                is("Property in super class."));
        assertThat(HtmlVisitor.visitAll(superPropConsumer.getBody(), context),
                is("Property in super class."));
        assertThat(superPropConsumer.getRequired(), nullValue());
        assertThat(superPropConsumer.isClosed(), is(true));
    }

    public static class TestDoclet implements Doclet {

        @Override
        public void init(Locale locale, Reporter reporter) {

        }

        @Override
        public String getName() {
            return getClass().getSimpleName();
        }

        @Override
        public Set<? extends Option> getSupportedOptions() {
            return Set.of();
        }

        @Override
        public SourceVersion getSupportedSourceVersion() {
            return SourceVersion.latestSupported();
        }

        @Override
        public boolean run(DocletEnvironment environment) {

            Processor test = new Processor(environment, loaderProvider, reporter);

            TypeElement element = (TypeElement) new ArrayList<>(environment.getSpecifiedElements()).get(0);

            test.process(element, beanDocConsumer);

            return true;
        }
    }

    static class OurHtmlContext implements HtmlContext {
        @Override
        public String hyperlinkFor(LinkElement linkElement) {
            return "LINK: " + linkElement.getSignature();
        }
    }

    static class OurLoaderProvider implements LoaderProvider {

        @Override
        public Optional<IncludeLoader> loaderFor(String name) {
            MatcherAssert.assertThat(name, is("our.inline"));

            return Optional.of(new IncludeLoader() {
                @Override
                public BeanDocElement load(String path) {
                    return StandardElement.of("** WORKED **");
                }
            });
        }
    }

    public static class TestTaglet implements Taglet {

        @Override
        public Set<Location> getAllowedLocations() {
            return Set.of(Location.TYPE);
        }

        @Override
        public boolean isInlineTag() {
            return false;
        }

        @Override
        public String getName() {
            return "our.tag";
        }

        @Override
        public String toString(List<? extends DocTree> tags, Element element) {
            return tags.toString();
        }
    }

    @Test
    void testIncludeLoader() {

        Path srcPath = OurDirs.relativePath("src/test/java/foo/includes/WithResourceInclude.java");

        reporter = mock(Reporter.class);

        beanDocConsumer = new CaptureConsumer.Type();

        DocTrees docTrees = mock(DocTrees.class);

        loaderProvider = new UnknownInlineLoaderProvider(
                getClass().getClassLoader());

        ToolProvider toolProvider = ToolProvider.findFirst("javadoc")
                .orElseThrow(() -> new IllegalArgumentException("No JavaDco"));
        int result = toolProvider.run(System.out, System.err,
                "-doclet", TestDoclet.class.getName(),
                srcPath.toString());

        HtmlContext htmlContext = mock(HtmlContext.class);

        String html = HtmlVisitor.visitAll(beanDocConsumer.description().getBody(), htmlContext);

        assertThat(html, containsString("stuff colour=\"green\""));
    }

    @Test
    void codeAndLiterals() {

        Path srcPath = OurDirs.relativePath("src/test/java/foo/literal/ThingWithSomeLiterals.java");

        assertThat(srcPath.toFile(), FileMatchers.anExistingFile());

        reporter = mock(Reporter.class);

        beanDocConsumer = new CaptureConsumer.Type();

        DocTrees docTrees = mock(DocTrees.class);

        loaderProvider = new UnknownInlineLoaderProvider(
                getClass().getClassLoader());

        ToolProvider toolProvider = ToolProvider.findFirst("javadoc")
                .orElseThrow(() -> new IllegalArgumentException("No JavaDco"));
        int result = toolProvider.run(System.out, System.err,
                "-doclet", TestDoclet.class.getName(),
                srcPath.toString());

        HtmlContext htmlContext = mock(HtmlContext.class);

        String html = HtmlVisitor.visitAll(beanDocConsumer.description().getBody(), htmlContext);

        assertThat(html, containsString("Some <code>java.lang.String</code>s and also x &#62; y."));
    }
}