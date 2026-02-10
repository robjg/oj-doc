package org.oddjob.doc.beandoc;

import org.oddjob.arooa.convert.doc.ElementIdentifier;

import javax.lang.model.element.Element;

/**
 * A Conversion doc identifier derived from a java doc element.
 */
public interface ModelElementIdentifier extends ElementIdentifier {

    Element getModelElement();
}
