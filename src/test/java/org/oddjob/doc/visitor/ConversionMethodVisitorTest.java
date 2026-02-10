package org.oddjob.doc.visitor;

import com.sun.source.doctree.DocCommentTree;
import com.sun.source.util.DocTrees;
import jdk.javadoc.doclet.Doclet;
import jdk.javadoc.doclet.DocletEnvironment;
import jdk.javadoc.doclet.Reporter;
import org.junit.jupiter.api.Test;
import org.oddjob.OurDirs;
import org.oddjob.arooa.convert.doc.MethodIdentifier;
import org.oddjob.doc.beandoc.ExecutableElementIdentifier;
import org.oddjob.doc.beandoc.TypeConsumers;
import org.oddjob.doc.doclet.CaptureConsumer;

import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Set;
import java.util.spi.ToolProvider;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ConversionMethodVisitorTest {

    static CaptureConsumer typeConsumer;
    static CaptureConsumer methodConsumer;

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

            TypeElement element = (TypeElement) new ArrayList<>(environment.getSpecifiedElements())
                    .getFirst();

            DocCommentTree docCommentTree = docTrees.getDocCommentTree(element);

            VisitorContext visitorContext = mock(VisitorContext.class);
            when(visitorContext.getDocTrees()).thenReturn(docTrees);
            when(visitorContext.getElement()).thenReturn(element);

            MethodIdentifier methodIdentifier;
            try {
                methodIdentifier = MethodIdentifier.ofMethod(ThingWithConversion.class.getMethod("toNumber"));
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }

            TypeConsumers typeCapture = mock(TypeConsumers.class);
            when(typeCapture.conversion()).thenReturn(typeConsumer);
            when(typeCapture.conversion(methodIdentifier)).thenReturn(methodConsumer);

            TypeVisitor.with(docTrees, visitorContext)
                    .visit(docCommentTree, typeCapture);

            Element someMethod = element.getEnclosedElements().stream()
                    .filter(ExecutableElement.class::isInstance)
                    .filter(method -> "toNumber".equals(method.getSimpleName().toString()))
                    .findFirst()
                    .orElseThrow();

            ExecutableElementIdentifier executableElementIdentifier = ExecutableElementIdentifier.ofElement(
                    (ExecutableElement) someMethod, environment.getElementUtils());

            DocCommentTree methodComment = docTrees.getDocCommentTree(someMethod);

            VisitorContext innerVisitorContext = mock(VisitorContext.class);
            when(innerVisitorContext.getDocTrees()).thenReturn(docTrees);
            when(innerVisitorContext.getElement()).thenReturn(someMethod);

            ConversionMethodVisitor.with(docTrees, innerVisitorContext)
                    .visit(methodComment, typeCapture, executableElementIdentifier);

            return true;
        }
    }

    @Test
    void methodConversionDoc() {

        Path srcPath = OurDirs.relativePath("src/test/java/org/oddjob/doc/visitor/ThingWithConversion.java");

        typeConsumer = new CaptureConsumer();
        methodConsumer = new CaptureConsumer();

        ToolProvider toolProvider = ToolProvider.findFirst("javadoc")
                .orElseThrow(() -> new IllegalArgumentException("No JavaDco"));
        int result = toolProvider.run(System.out, System.err,
                "-doclet", OurDoclet.class.getName(),
                srcPath.toString());

        assertThat(result, is(0));

        assertThat(typeConsumer.getBody().size(), is(1));
        assertThat(typeConsumer.getFirstSentence().size(), is(1));

        assertThat(methodConsumer.getBody().size(), is(1));
        assertThat(methodConsumer.getFirstSentence().size(), is(1));
    }
}
