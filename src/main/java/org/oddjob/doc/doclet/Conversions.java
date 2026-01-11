package org.oddjob.doc.doclet;

import org.oddjob.arooa.beandocs.ConversionDoc;
import org.oddjob.arooa.beandocs.WriteableConversionDoc;
import org.oddjob.arooa.beandocs.WriteableConversionDocs;

/**
 * Collects Conversion Docs in a way that makes them accessible during
 * Doclet processing. Analogous to {@link JobsAndTypes}.
 *
 * @see WriteableConversionDocs
 */
public class Conversions {

    private final WriteableConversionDocs conversionsByType;

    public Conversions(WriteableConversionDocs conversionsByType) {
        this.conversionsByType = conversionsByType;
    }

    public As docByType(String typeName) {

        if (conversionsByType.containsDocumentedByType(typeName)) {
            return new As(typeName);
        }
        else {
            return null;
        }
    }

    public ConversionDoc[] getConversionDocs() {
        return conversionsByType.getConversionDocs();
    }

    public ConversionDoc[] getConversionDocsFrom(String typeNameFrom) {
        return new ConversionDoc[0];
    }

    public class As {

        private final String typeName;

        public As(String typeName) {
            this.typeName = typeName;
        }

        public WriteableConversionDoc asType() {
            return conversionsByType.conversionDocumentedByType(typeName);
        }

        public WriteableConversionDoc asMethod(String methodName) {
            return conversionsByType.conversionDocumentedByMethod(typeName, methodName);
        }

    }
}
