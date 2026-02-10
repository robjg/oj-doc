package org.oddjob.doc.beandoc;

import org.oddjob.arooa.convert.doc.MethodIdentifier;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

/**
 * An Identifier for a method element for indexing conversion docs.
 */
public class ExecutableElementIdentifier extends MethodIdentifier implements ModelElementIdentifier {

    private final ExecutableElement executableElement;

    private final Elements elements;

    public ExecutableElementIdentifier(ExecutableElement executableElement, Elements elements) {
        this.executableElement = executableElement;
        this.elements = elements;
    }

    public static ExecutableElementIdentifier ofElement(ExecutableElement element, Elements elements) {
        return new ExecutableElementIdentifier(element, elements);
    }

    @Override
    public String getMethodName() {
        return executableElement.getSimpleName().toString();
    }

    @Override
    public ExecutableElement getModelElement() {
        return executableElement;
    }

    @Override
    public TypeElementIdentifier getTypeIdentifier() {
        return TypeElementIdentifier.ofElement((TypeElement) executableElement.getEnclosingElement(),
                elements);
    }
}
