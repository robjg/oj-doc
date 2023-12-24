package org.oddjob.doc.visitor;

import com.sun.source.doctree.LinkTree;
import com.sun.source.doctree.LiteralTree;
import com.sun.source.doctree.UnknownInlineTagTree;
import jdk.javadoc.doclet.Reporter;
import org.oddjob.arooa.beandocs.element.BeanDocElement;
import org.oddjob.doc.util.InlineTagHelper;

import javax.lang.model.element.Element;
import javax.tools.Diagnostic;

/**
 * Build a Context for Visiting doc nodes.
 */
public class VisitorContextBuilder {

    public static VisitorContext create(InlineTagHelper inlineTagHelper,
                                        Reporter reporter,
                                        Element element) {

        return new Impl(inlineTagHelper, reporter, element);
    }

    private static class Impl implements VisitorContext {

        private final InlineTagHelper inlineTagHelper;

        private final Reporter reporter;

        private final Element element;

        private Impl(InlineTagHelper inlineTagHelper, Reporter reporter, Element element) {
            this.inlineTagHelper = inlineTagHelper;
            this.reporter = reporter;
            this.element = element;
        }

        @Override
        public void info(String msg) {
            reporter.print(Diagnostic.Kind.NOTE, element, msg);
        }

        @Override
        public void warn(String msg) {
            reporter.print(Diagnostic.Kind.WARNING, element, msg);
        }

        @Override
        public void fail(String msg) {
            reporter.print(Diagnostic.Kind.ERROR, element, msg);
            throw new IllegalArgumentException(msg);
        }

        @Override
        public void fail(String msg, Exception e) {
            reporter.print(Diagnostic.Kind.ERROR, element, msg);
            throw new IllegalArgumentException(msg, e);
        }

        @Override
        public String processLink(LinkTree linkTag) {
            return inlineTagHelper.processLink(linkTag, element);
        }

        @Override
        public BeanDocElement processUnknownInline(UnknownInlineTagTree unknownTag) {
            return inlineTagHelper.processUnknownInline(unknownTag, element);
        }

        @Override
        public BeanDocElement processLiteral(LiteralTree literalTree) {
            return inlineTagHelper.processLiteral(literalTree, element);
        }
    }


}
