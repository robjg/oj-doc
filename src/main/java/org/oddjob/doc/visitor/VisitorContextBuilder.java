package org.oddjob.doc.visitor;

import com.sun.source.util.DocTrees;
import jdk.javadoc.doclet.Reporter;
import org.oddjob.doc.util.LoaderProvider;

import javax.lang.model.element.Element;
import javax.tools.Diagnostic;

/**
 * Build a Context for Visiting doc nodes.
 */
public class VisitorContextBuilder {

    public static VisitorContext create(DocTrees docTrees,
                                        LoaderProvider loaderProvider,
                                        Reporter reporter,
                                        Element element) {

        return new Impl(docTrees, loaderProvider, reporter, element);
    }

    private static class Impl implements VisitorContext {

        private final DocTrees docTrees;

        private final LoaderProvider loaderProvider;

        private final Reporter reporter;

        private final Element element;

        private Impl(DocTrees docTrees,
                     LoaderProvider loaderProvider,
                     Reporter reporter,
                     Element element) {
            this.docTrees = docTrees;
            this.loaderProvider = loaderProvider;
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
        public LoaderProvider getLoaderProvider() {
            return loaderProvider;
        }

        @Override
        public Element getElement() {
            return element;
        }

        @Override
        public DocTrees getDocTrees() {
            return docTrees;
        }

    }
}
