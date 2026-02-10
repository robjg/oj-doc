package org.oddjob.doc.markdown;

import org.oddjob.arooa.beandocs.ConversionArchive;
import org.oddjob.arooa.beandocs.ConversionDoc;
import org.oddjob.arooa.beandocs.element.BeanDocElement;
import org.oddjob.arooa.utils.AppendablePrinter;

import java.util.List;
import java.util.Objects;

/**
 * Writes Conversion Doc in Markdown.
 */
public class MdConversionsWriter {

    private final MdContext mdContext;

    private final AppendablePrinter out;

    private MdConversionsWriter(MdContext mdContext,
                                Appendable out) {
        this.mdContext = mdContext;
        this.out = new AppendablePrinter(out);
    }

    public static class Settings {

        private final MdContext mdContext;

        Settings(MdContext mdContext) {
            this.mdContext = mdContext;
        }

        public MdConversionsWriter to(Appendable appendable) {

            return new MdConversionsWriter(mdContext, appendable);
        }
    }

    public static Settings forContext(MdContext mdContext) {
        return new Settings(mdContext);
    }

    String toLine(List<? extends BeanDocElement> elements) {
        return MdVisitor.visitAsLine(elements, mdContext);
    }

    public void write(ConversionArchive conversionArchive) {

        out.println("# Conversions");
        out.println();
        out.println("Conversion documentation is a Work in Progress.");
        out.println();
        out.println("| From | To | Description |");
        out.println("| -------- | ----------- | ----------- |");
        for (ConversionDoc doc : conversionArchive.allConversionDoc()) {
            String toText = Objects.requireNonNullElse(doc.getToType(), "*Various*");
            String description = toLine(doc.getAllText());
            if (description.isEmpty() && doc.getTypeOrMethod() != null) {
                description = "Undocumented by " + doc.getTypeOrMethod();
            }
            out.println("| " + doc.getFromType() + " | "
                    + toText + " | " + description + " | ");
        }
        out.println();
    }
}
