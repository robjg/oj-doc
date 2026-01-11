package org.oddjob.doc.markdown;

import org.oddjob.arooa.beandocs.ConversionArchive;
import org.oddjob.arooa.beandocs.ConversionDoc;
import org.oddjob.arooa.beandocs.element.BeanDocElement;
import org.oddjob.arooa.utils.AppendablePrinter;

import java.util.List;

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
        out.println();
        out.println("| From | To | Description |");
        out.println("| -------- | ----------- | ----------- |");
        for (ConversionDoc doc : conversionArchive.allConversionDoc()) {
            out.println("| " + doc.getFromType() + " | "
                    + doc.getToType() + " | "
                    + toLine(doc.getAllText()) + " | ");
        }
        out.println();
    }
}
