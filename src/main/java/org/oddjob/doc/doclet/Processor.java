/*
 * Copyright (c) 2005, Rob Gordon.
 */
package org.oddjob.doc.doclet;

import com.sun.source.doctree.DocCommentTree;
import com.sun.source.util.DocTrees;
import jdk.javadoc.doclet.DocletEnvironment;
import jdk.javadoc.doclet.Reporter;
import org.oddjob.arooa.utils.EtcUtils;
import org.oddjob.doc.beandoc.ExecutableElementIdentifier;
import org.oddjob.doc.beandoc.TypeConsumers;
import org.oddjob.doc.beandoc.TypeElementIdentifier;
import org.oddjob.doc.util.LoaderProvider;
import org.oddjob.doc.visitor.*;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import java.util.*;

/**
 * A Processor is capable of processing a java ClassDoc object into
 * a reference PageData object.
 *
 * @author Rob Gordon.
 */
public class Processor implements ElementProcessor {

    private final DocletEnvironment docEnv;

    private final LoaderProvider loaderProvider;

    private final Reporter reporter;

    /**
     * Create a processor.
     *
     * @param docEnv         The utility class for access comments.
     * @param loaderProvider Helper for inline include tags.
     * @param reporter       The javadoc Reporter.
     */
    public Processor(DocletEnvironment docEnv,
                     LoaderProvider loaderProvider,
                     Reporter reporter) {

        this.docEnv = docEnv;
        this.loaderProvider = loaderProvider;
        this.reporter = reporter;
    }

    @Override
    public void process(TypeElement element,
                        TypeConsumersProvider typeConsumersProvider) {

        processType(element, typeConsumersProvider, new HashSet<>());
    }

    public void processType(TypeElement element,
                            TypeConsumersProvider typeConsumersProvider,
                            Set<TypeElement> seenAlready) {

        seenAlready.add(element);

        List<Element> enclosed = enclosedElements(element, new ArrayList<>());

        TypeElementIdentifier typeIdentifier = TypeElementIdentifier.ofElement(element, docEnv.getElementUtils());

        TypeConsumers typeConsumers = typeConsumersProvider.typeConsumersFor(typeIdentifier);

        if (typeConsumers != null) {

            reporter.print(Diagnostic.Kind.NOTE, "Processing " + element);

            DocTrees docTrees = docEnv.getDocTrees();

            DocCommentTree docCommentTree = docTrees.getDocCommentTree(element);

            if (docCommentTree != null) {

                VisitorContext visitorContext = VisitorContextBuilder.create(
                        docTrees, loaderProvider, reporter, element);

                TypeVisitor.with(docTrees, visitorContext)
                        .visit(docCommentTree, typeConsumers);

                MemberProcessor memberProcessor = new MemberProcessor(typeConsumers,
                        docTrees);

                for (Element enclosedElement : enclosed) {

                    memberProcessor.process(enclosedElement);
                }

                typeConsumers.close();
            }
        }

        for (Element enclosedElement : enclosed) {

            if (enclosedElement instanceof TypeElement typeElement) {

                if (seenAlready.contains(typeElement)) {
                    continue;
                }
                processType(typeElement, typeConsumersProvider, seenAlready);
            }
        }

    }

    /**
     * Find all the members and methods including those for super classes.
     *
     * @param element     The Type Element.
     * @param accumulator Capture all elements.
     * @return List of all enclosed elements.
     */
    List<Element> enclosedElements(TypeElement element, List<Element> accumulator) {

        if (Object.class.getName().equals(element.getQualifiedName().toString())) {
            return accumulator;
        }

        accumulator.addAll(element.getEnclosedElements());

        TypeMirror typeMirror = element.getSuperclass();

        if (typeMirror.getKind() == TypeKind.NONE) {
            return accumulator;
        }

        return enclosedElements(
                (TypeElement) docEnv.getTypeUtils().asElement(typeMirror), accumulator);
    }

    /**
     * Process fields and method elements and ignore others.
     *
     */
    class MemberProcessor {

        private final TypeConsumers typeConsumers;
        private final DocTrees docTrees;

        MemberProcessor(TypeConsumers typeConsumers,
                        DocTrees docTrees) {
            this.typeConsumers = typeConsumers;
            this.docTrees = docTrees;
        }

        void process(Element memberElement) {

            DocCommentTree docCommentTree = docTrees.getDocCommentTree(memberElement);

            if (docCommentTree == null) {
                return;
            }

            VisitorContext visitorContext = VisitorContextBuilder.create(docTrees,
                    loaderProvider, reporter, memberElement);

            maybeProcessProperty(memberElement,
                    docCommentTree, visitorContext);

            maybeProcessMethodConversion(memberElement, docCommentTree, visitorContext);


        }

        void maybeProcessProperty(Element memberElement,
                                  DocCommentTree docCommentTree,
                                  VisitorContext visitorContext) {

            Optional<String> optionalPropertyName = toProp(memberElement);

            if (optionalPropertyName.isEmpty()) {
                return;
            }

            String propertyName = optionalPropertyName.get();

            PropertyVisitor.with(docTrees, visitorContext)
                    .visit(docCommentTree, typeConsumers, propertyName);

        }

        void maybeProcessMethodConversion(Element memberElement,
                                          DocCommentTree docCommentTree,
                                          VisitorContext visitorContext) {

            if (memberElement.getKind() != ElementKind.METHOD) {
                return;
            }

            ConversionMethodVisitor.with(docTrees, visitorContext)
                    .visit(docCommentTree, typeConsumers,
                            ExecutableElementIdentifier.ofElement((ExecutableElement) memberElement,
                                    docEnv.getElementUtils()));
        }

    }

    static Optional<String> toProp(Element element) {

        if (element.getKind() == ElementKind.METHOD) {
            return EtcUtils.propertyFromMethodName(element.getSimpleName().toString());
        } else if (element.getKind() == ElementKind.FIELD) {
            return Optional.of(element.getSimpleName().toString());
        } else {
            return Optional.empty();
        }
    }
}
