package org.oddjob.doc.doclet;

import org.oddjob.arooa.beandocs.BeanDocArchive;

/**
 * Something that can create the reference manual from a Bean Doc Archive.
 */
public interface ReferenceWriter {

    void createManual(BeanDocArchive archive);

}
