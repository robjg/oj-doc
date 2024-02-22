package org.oddjob.doc.doclet;

import org.oddjob.arooa.beandocs.BeanDocArchive;

/**
 * Creates an {@link ReferenceWriter}.
 */
public interface ReferenceWriterFactory {

    void setArchive(BeanDocArchive archive);

    void setDestination(String destination);

    void setTitle(String title);

    void setApiLink(String apiLink);

    ReferenceWriter create();
}
