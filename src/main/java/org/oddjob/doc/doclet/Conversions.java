package org.oddjob.doc.doclet;

import org.oddjob.arooa.beandocs.ConversionDoc;
import org.oddjob.arooa.beandocs.WriteableConversionDoc;
import org.oddjob.arooa.beandocs.WriteableConversionDocs;
import org.oddjob.arooa.convert.doc.MethodIdentifier;
import org.oddjob.arooa.convert.doc.TypeIdentifier;

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

    public As docByType(TypeIdentifier typeIdentifier) {

        if (conversionsByType.containsDocumentedByType(typeIdentifier)) {
            return new As(typeIdentifier);
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

        private final TypeIdentifier typeIdentifier;

        public As(TypeIdentifier typeIdentifier) {
            this.typeIdentifier = typeIdentifier;
        }

        public WriteableConversionDoc asType() {
            return conversionsByType.conversionDocumentedByType(typeIdentifier);
        }

        public WriteableConversionDoc asMethod(MethodIdentifier methodIdentifier) {
            return conversionsByType.conversionDocumentedByMethod(methodIdentifier);
        }

    }
}
