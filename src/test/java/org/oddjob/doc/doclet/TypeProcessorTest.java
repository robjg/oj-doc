package org.oddjob.doc.doclet;

import com.sun.source.doctree.DocTree;
import com.sun.source.doctree.LinkTree;
import com.sun.source.doctree.LiteralTree;
import com.sun.source.doctree.UnknownInlineTagTree;
import com.sun.source.util.DocTrees;
import jdk.javadoc.doclet.Doclet;
import jdk.javadoc.doclet.DocletEnvironment;
import jdk.javadoc.doclet.Reporter;
import jdk.javadoc.doclet.Taglet;
import org.hamcrest.io.FileMatchers;
import org.junit.jupiter.api.Test;
import org.oddjob.OurDirs;
import org.oddjob.arooa.beandocs.element.BeanDocElement;
import org.oddjob.arooa.beandocs.element.StandardElement;
import org.oddjob.doc.html.HtmlVisitor;
import org.oddjob.doc.taglet.UnknownInlineLoaderProvider;
import org.oddjob.doc.util.InlineTagHelper;

import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.spi.ToolProvider;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.mock;

class TypeProcessorTest {

    static Reporter reporter;

    static CaptureConsumer.Type beanDocConsumer;

    static InlineTagHelper inlineTagHelper;

    @Test
    void testSingleClass() {

        Path srcPath = OurDirs.relativePath("src/test/java/org/oddjob/doc/doclet/ThingWithSomeDoc.java");
        Path srcPath2 = OurDirs.relativePath("src/test/java/org/oddjob/doc/doclet/ThingWithSomeDocBase.java");

        reporter = mock(Reporter.class);

        beanDocConsumer = new CaptureConsumer.Type();

        inlineTagHelper = new OurHelper();

        ToolProvider toolProvider = ToolProvider.findFirst("javadoc")
                .orElseThrow(() -> new IllegalArgumentException("No JavaDco"));
        int result = toolProvider.run(System.out, System.err,
                "-doclet", TestDoclet.class.getName(),
                srcPath.toString(), srcPath2.toString());

        assertThat(HtmlVisitor.visitAll(beanDocConsumer.description().getFirstSentence()),
                is("First sentence in block tag."));

        assertThat(HtmlVisitor.visitAll(beanDocConsumer.description().getBody()),
                is(
                "First sentence in block tag. Some more stuff in the block tag. LINK: ThingWithSomeDoc.\n" +
                        " ** WORKED **. <p>And some html</p>."));

        CaptureConsumer exampleConsumer = beanDocConsumer.getExample(0);
        assertThat(HtmlVisitor.visitAll(exampleConsumer.getFirstSentence()),
                is("This is an example."));
        assertThat(HtmlVisitor.visitAll(exampleConsumer.getBody()),
                is("This is an example.\n\n Which can go over several lines.\n\n ** WORKED **"));
        assertThat(exampleConsumer.isClosed(), is(true));

        CaptureConsumer somePropConsumer = beanDocConsumer.getProperty("someProp");

        assertThat(HtmlVisitor.visitAll(somePropConsumer.getFirstSentence()),
                is("Some property"));
        assertThat(HtmlVisitor.visitAll(somePropConsumer.getBody()),
                is("Some property"));
        assertThat(somePropConsumer.isClosed(), is(true));

        CaptureConsumer.Property anotherPropConsumer = beanDocConsumer.getProperty("anotherProp");

        assertThat(HtmlVisitor.visitAll(anotherPropConsumer.getFirstSentence()),
                is("Another property"));
        assertThat(HtmlVisitor.visitAll(anotherPropConsumer.getBody()),
                is("Another property"));
        assertThat(anotherPropConsumer.getRequired(), is("Yes"));
        assertThat(anotherPropConsumer.isClosed(), is(true));

        CaptureConsumer.Property superPropConsumer = beanDocConsumer.getProperty("superProp");

        assertThat(HtmlVisitor.visitAll(superPropConsumer.getFirstSentence()),
                is("Property in super class."));
        assertThat(HtmlVisitor.visitAll(superPropConsumer.getBody()),
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

            InlineHelperProvider inlineHelperProvider = typeElement -> inlineTagHelper;

            Processor test = new Processor(environment, inlineHelperProvider, reporter);

            TypeElement element = (TypeElement) new ArrayList<>(environment.getSpecifiedElements()).get(0);

            test.process(element, beanDocConsumer);

            return true;
        }
    }

    static class OurHelper implements InlineTagHelper {

        @Override
        public String processLink(LinkTree linkTag, Element element) {
            return "LINK: " + linkTag.getReference().getSignature();
        }

        @Override
        public BeanDocElement processUnknownInline(UnknownInlineTagTree unknownTag, Element element) {
            if (unknownTag.getTagName().equals("our.inline")) {
                return StandardElement.of("** WORKED **");
            }
            else {
                throw new IllegalStateException("Unexpected: " + unknownTag);
            }
        }

        @Override
        public BeanDocElement processLiteral(LiteralTree literalTree, Element element) {
            throw new IllegalStateException("Unexpected!");
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

        inlineTagHelper = new ReferenceInlineTagHelper(docTrees,
                new UnknownInlineLoaderProvider(getClass().getClassLoader()),
                null, null, null);

        ToolProvider toolProvider = ToolProvider.findFirst("javadoc")
                .orElseThrow(() -> new IllegalArgumentException("No JavaDco"));
        int result = toolProvider.run(System.out, System.err,
                "-doclet", TestDoclet.class.getName(),
                srcPath.toString());

        String html = HtmlVisitor.visitAll(beanDocConsumer.description().getBody());

        assertThat(html, containsString("stuff colour=\"green\""));
    }

    @Test
    void codeAndLiterals() {

        Path srcPath = OurDirs.relativePath("src/test/java/foo/literal/ThingWithSomeLiterals.java");

        assertThat(srcPath.toFile(), FileMatchers.anExistingFile());

        reporter = mock(Reporter.class);

        beanDocConsumer = new CaptureConsumer.Type();

        DocTrees docTrees = mock(DocTrees.class);

        inlineTagHelper = new ReferenceInlineTagHelper(docTrees,
                new UnknownInlineLoaderProvider(getClass().getClassLoader()),
                null, null, null);

        ToolProvider toolProvider = ToolProvider.findFirst("javadoc")
                .orElseThrow(() -> new IllegalArgumentException("No JavaDco"));
        int result = toolProvider.run(System.out, System.err,
                "-doclet", TestDoclet.class.getName(),
                srcPath.toString());

        String html = HtmlVisitor.visitAll(beanDocConsumer.description().getBody());

        assertThat(html, containsString("Some <pre>java.lang.String</pre>s and also x &#62; y."));
    }
}