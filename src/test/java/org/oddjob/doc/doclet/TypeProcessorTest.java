package org.oddjob.doc.doclet;

import com.sun.source.doctree.DocTree;
import com.sun.source.doctree.LinkTree;
import com.sun.source.doctree.LiteralTree;
import com.sun.source.doctree.UnknownInlineTagTree;
import jdk.javadoc.doclet.Doclet;
import jdk.javadoc.doclet.DocletEnvironment;
import jdk.javadoc.doclet.Reporter;
import jdk.javadoc.doclet.Taglet;
import org.junit.jupiter.api.Test;
import org.oddjob.OurDirs;
import org.oddjob.doc.taglet.UnknownInlineTagletProvider;
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
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;

class TypeProcessorTest {

    static Reporter reporter;

    static CaptureConsumer.Type beanDocConsumer;

    static InlineTagHelper inlineTagHelper;

    @Test
    void testSingleClass() {

        Path srcPath = OurDirs.relativePath("src/test/java/org/oddjob/doc/doclet/ThingWithSomeDoc.java");

        reporter = mock(Reporter.class);

        beanDocConsumer = new CaptureConsumer.Type();

        inlineTagHelper = new OurHelper();

        ToolProvider toolProvider = ToolProvider.findFirst("javadoc")
                .orElseThrow(() -> new IllegalArgumentException("No JavaDco"));
        int result = toolProvider.run(System.out, System.err,
                "-doclet", TestDoclet.class.getName(),
                srcPath.toString());

        assertThat(beanDocConsumer.description().getFirstSentence(), is("First sentence in block tag."));

        assertThat(beanDocConsumer.description().getBody(), is(
                "First sentence in block tag. Some more stuff in the block tag. LINK: ThingWithSomeDoc.\n" +
                        " ** WORKED **. <p>And some html</p>."));

        CaptureConsumer exampleConsumer = beanDocConsumer.getExample(0);
        assertThat(exampleConsumer.getFirstSentence(), is("This is an example."));
        assertThat(exampleConsumer.getBody(), is("This is an example.\n\n Which can go over several lines.\n\n ** WORKED **"));
        assertThat(exampleConsumer.isClosed(), is(true));

        CaptureConsumer somePropConsumer = beanDocConsumer.getProperty("someProp");

        assertThat(somePropConsumer.getFirstSentence(), is("Some property"));
        assertThat(somePropConsumer.getBody(), is("Some property"));
        assertThat(somePropConsumer.isClosed(), is(true));

        CaptureConsumer.Property anotherPropConsumer = beanDocConsumer.getProperty("anotherProp");

        assertThat(anotherPropConsumer.getFirstSentence(), is("Another property"));
        assertThat(anotherPropConsumer.getBody(), is("Another property"));
        assertThat(anotherPropConsumer.getRequired(), is("Yes"));
        assertThat(anotherPropConsumer.isClosed(), is(true));

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

            Processor test = new Processor(environment.getDocTrees(), inlineHelperProvider, reporter);

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
        public String processUnknownInline(UnknownInlineTagTree unknownTag, Element element) {
            if (unknownTag.getTagName().equals("our.inline")) {
                return "** WORKED **";
            }
            else {
                throw new IllegalStateException("Unexpected: " + unknownTag);
            }
        }

        @Override
        public String processLiteral(LiteralTree literalTree, Element element) {
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

        DocletEnvironment docletEnvironment = mock(DocletEnvironment.class);
        Doclet doclet = mock(Doclet.class);

        inlineTagHelper = new ReferenceInlineTagHelper(docletEnvironment.getDocTrees(),
                new UnknownInlineTagletProvider(docletEnvironment, doclet),
                null, null, null);

        ToolProvider toolProvider = ToolProvider.findFirst("javadoc")
                .orElseThrow(() -> new IllegalArgumentException("No JavaDco"));
        int result = toolProvider.run(System.out, System.err,
                "-doclet", TestDoclet.class.getName(),
                srcPath.toString());

        assertThat(beanDocConsumer.description().getBody(), containsString("stuff colour=\"green\""));
    }
}