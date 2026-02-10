package org.oddjob.doc.beandoc;

import org.oddjob.arooa.convert.doc.TypeIdentifier;

import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

/**
 * A Conversion doc identifier derived from a java doc type element.
 */
public class TypeElementIdentifier extends TypeIdentifier implements ModelElementIdentifier {

    private final TypeElement typeElement;

    private final Elements  elements;

    protected TypeElementIdentifier(TypeElement typeElement, Elements elements) {
        this.typeElement = typeElement;
        this.elements = elements;
    }

    public static TypeElementIdentifier ofElement(TypeElement typeElement, Elements elements) {
        return new TypeElementIdentifier(typeElement, elements);
    }

    @Override
    public String getName() {
        return typeElement.getQualifiedName().toString();
    }

    @Override
    public String getClassName() {
        return elements.getBinaryName(typeElement).toString();
    }

    @Override
    public TypeElement getModelElement() {
        return typeElement;
    }

}
