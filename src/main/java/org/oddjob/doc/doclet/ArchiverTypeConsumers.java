package org.oddjob.doc.doclet;

import jdk.javadoc.doclet.Reporter;
import org.oddjob.arooa.beandocs.WriteableBeanDoc;
import org.oddjob.doc.beandoc.BeanDocCollector;
import org.oddjob.doc.beandoc.TypeConsumers;
import org.oddjob.doc.beandoc.TypeElementIdentifier;

import javax.tools.Diagnostic;
import java.util.function.Consumer;

/**
 * Manages Documentation in progress for the {@link Archiver}. Implements {@link TypeConsumersProvider} so the
 * decision to document can be deferred down to nested type hierarchy.
 */
public class ArchiverTypeConsumers implements TypeConsumersProvider {

    private final JobsAndTypes jats;

    private final Conversions conversions;

    private final Reporter reporter;

    public ArchiverTypeConsumers(JobsAndTypes jats,
                                 Conversions conversions,
                                 Reporter reporter) {
        this.jats = jats;
        this.conversions = conversions;
        this.reporter = reporter;
    }

    @Override
    public TypeConsumers typeConsumersFor(TypeElementIdentifier typeIdentifier) {

        WriteableBeanDoc beanDoc = jats.docFor(typeIdentifier.getClassName());

        Conversions.As conversionDoc = conversions.docByType(typeIdentifier);

        if (beanDoc == null && conversionDoc == null) {
            return null;
        }

        Consumer<String> warningHandler = message -> {
            reporter.print(Diagnostic.Kind.WARNING,
                    typeIdentifier.getModelElement(), message);
        };

        return new BeanDocCollector(beanDoc, conversionDoc, warningHandler);
    }
}
