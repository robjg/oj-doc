package org.oddjob.doc.util;

import org.oddjob.arooa.utils.EtcUtils;

import javax.lang.model.element.*;
import java.util.function.Consumer;

/**
 * Dissects an {@link Element} into the parts so that a Bean Doc Reference may link to another page in a reference.
 */
public class ElementDissected {

    /** The fully qualified type name. This will be used to match an element in an
     * {@link org.oddjob.arooa.ArooaDescriptor}. */
    private String qualifiedType;

    /** The property name extracted from either the method or field. */
    private String propertyName;

    /**
     * Dissect the given element.
     *
     * @param element The element, never null.
     * @param error Consumer of error messages.
     *
     * @return A dissected element. Never null but may be empty depending on the type of element passed. If it
     * is empty an error should have been reported.
     */
    public static ElementDissected from(Element element, Consumer<? super String> error) {

        ElementDissected elementDissected = new ElementDissected();

        return element.accept(new Visitor(), new Context(elementDissected, error));
    }

    public String getQualifiedType() {
        return qualifiedType;
    }

    public String getPropertyName() {
        return propertyName;
    }

    static class Context {

        private final Consumer<? super String> error;

        private final ElementDissected elementDissected;

        Context(ElementDissected elementDissected, Consumer<? super String> error) {
            this.elementDissected = elementDissected;
            this.error = error;
        }

        ElementDissected unexpected(Element element) {
            error.accept("Unexpected element " + element);
            return elementDissected;
        }

    }

    static class Visitor implements ElementVisitor<ElementDissected, Context> {

        @Override
        public ElementDissected visit(Element e, Context context) {

            return context.unexpected(e);
        }

        @Override
        public ElementDissected visitPackage(PackageElement e, Context context) {

            return context.unexpected(e);
        }

        @Override
        public ElementDissected visitType(TypeElement e, Context context) {

            context.elementDissected.qualifiedType = e.getQualifiedName().toString();
            return context.elementDissected;
        }

        @Override
        public ElementDissected visitVariable(VariableElement e, Context context) {

            context.elementDissected.propertyName = e.getSimpleName().toString();
            Element enclosing = e.getEnclosingElement();
            return enclosing.accept(this, context);
        }

        @Override
        public ElementDissected visitExecutable(ExecutableElement e, Context context) {

            EtcUtils.propertyFromMethodName(e.getSimpleName().toString())
                    .ifPresent(prop -> context.elementDissected.propertyName = prop);

            Element enclosing = e.getEnclosingElement();
            return enclosing.accept(this, context);
        }

        @Override
        public ElementDissected visitTypeParameter(TypeParameterElement e, Context context) {

            return context.unexpected(e);
        }

        @Override
        public ElementDissected visitUnknown(Element e, Context context) {

            return context.unexpected(e);
        }
    }
}
