package org.oddjob.doc.visitor;

import com.sun.source.doctree.DocCommentTree;
import com.sun.source.util.DocTrees;
import jdk.javadoc.doclet.Doclet;
import jdk.javadoc.doclet.DocletEnvironment;
import jdk.javadoc.doclet.Reporter;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.oddjob.OurDirs;
import org.oddjob.arooa.beandocs.element.LinkElement;
import org.oddjob.doc.doclet.CaptureConsumer;
import org.oddjob.doc.doclet.ThingWithSomeDoc;

import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.spi.ToolProvider;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class InlineLinkTest {

    static CaptureConsumer.Type typeCapture;

    public static class OurDoclet implements Doclet {

        @Override
        public void init(Locale locale, Reporter reporter) {

        }

        @Override
        public String getName() {
            return null;
        }

        @Override
        public Set<? extends Option> getSupportedOptions() {
            return Set.of();
        }

        @Override
        public SourceVersion getSupportedSourceVersion() {
            return null;
        }

        @Override
        public boolean run(DocletEnvironment environment) {

            DocTrees docTrees = environment.getDocTrees();

            TypeElement element = (TypeElement) new ArrayList<>(environment.getSpecifiedElements()).get(0);

            DocCommentTree docCommentTree = docTrees.getDocCommentTree(element);

            VisitorContext visitorContext = mock(VisitorContext.class);
            when(visitorContext.getDocTrees()).thenReturn(docTrees);
            when(visitorContext.getElement()).thenReturn(element);

            TypeVisitor.with(docTrees, visitorContext)
                    .visit(docCommentTree, typeCapture);

            Element someProp = element.getEnclosedElements().stream()
                    .filter(VariableElement.class::isInstance)
                    .filter(variable -> "someProp".equals(variable.getSimpleName().toString()))
                    .findFirst()
                    .orElseThrow();

            DocCommentTree somePropComment = docTrees.getDocCommentTree(someProp);

            VisitorContext propVisitorContext = mock(VisitorContext.class);
            when(propVisitorContext.getDocTrees()).thenReturn(docTrees);
            when(propVisitorContext.getElement()).thenReturn(someProp);

            PropertyVisitor.with(docTrees, propVisitorContext)
                    .visit(somePropComment, typeCapture, "someProp");

            return true;
        }
    }

    @Test
    void whenLinksThenProcessedOk() {

        Path srcPath = OurDirs.relativePath("src/test/java/org/oddjob/doc/visitor/ThingWithSomeLinks.java");

        typeCapture = new CaptureConsumer.Type();

        ToolProvider toolProvider = ToolProvider.findFirst("javadoc")
                .orElseThrow(() -> new IllegalArgumentException("No JavaDco"));
        int result = toolProvider.run(System.out, System.err,
                "-doclet", OurDoclet.class.getName(),
                srcPath.toString());

        List<LinkElement> linkElements = typeCapture.description().getBody()
                .stream()
                .filter(LinkElement.class::isInstance)
                .map(LinkElement.class::cast)
                .collect(Collectors.toList());

        assertThat(result, is(0));

        LinkElement link1 = linkElements.get(0);
        assertThat(link1.getSignature(), is("org.oddjob.doc.doclet.ThingWithSomeDoc"));
        assertThat(link1.getQualifiedType(), is(ThingWithSomeDoc.class.getName()));
        assertThat(link1.getPropertyName(), Matchers.nullValue());
        assertThat(link1.getLabel(), Matchers.is(""));

        LinkElement link2 = linkElements.get(1);
        assertThat(link2.getSignature(), is("ThingWithSomeDoc#setAnotherProp()"));
        assertThat(link2.getQualifiedType(), is(ThingWithSomeDoc.class.getName()));
        assertThat(link2.getPropertyName(), Matchers.is("anotherProp"));
        assertThat(link2.getLabel(), Matchers.is(""));

        LinkElement link3 = linkElements.get(2);
        assertThat(link3.getSignature(), is("#someProp"));
        assertThat(link3.getQualifiedType(), is(ThingWithSomeLinks.class.getName()));
        assertThat(link3.getPropertyName(), Matchers.is("someProp"));
        assertThat(link3.getLabel(), Matchers.is("With A Label"));

        LinkElement link4 = linkElements.get(3);
        assertThat(link4.getSignature(), is("org.foo.FruitLoop"));
        assertThat(link4.getQualifiedType(), is(Matchers.nullValue()));
        assertThat(link4.getPropertyName(), Matchers.nullValue());
        assertThat(link4.getLabel(), Matchers.is(""));

        LinkElement link5 = linkElements.get(4);
        assertThat(link5.getSignature(), is("#badLink"));
        assertThat(link5.getQualifiedType(), is(Matchers.nullValue()));
        assertThat(link5.getPropertyName(), Matchers.nullValue());
        assertThat(link5.getLabel(), Matchers.is("A Property That Doesn't Exist"));

        assertThat(linkElements.size(), is(5));

        List<LinkElement> propertyElements = typeCapture.getProperty("someProp").getFirstSentence()
                .stream()
                .filter(LinkElement.class::isInstance)
                .map(LinkElement.class::cast)
                .collect(Collectors.toList());

        LinkElement propertyLink1 = propertyElements.get(0);
        assertThat(propertyLink1.getSignature(), is("org.oddjob.doc.doclet.ThingWithSomeDoc"));
        assertThat(propertyLink1.getQualifiedType(), is(ThingWithSomeDoc.class.getName()));
        assertThat(propertyLink1.getPropertyName(), Matchers.nullValue());
        assertThat(propertyLink1.getLabel(), Matchers.is(""));
    }
}