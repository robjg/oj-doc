package org.oddjob.doc.util;

import com.sun.source.util.DocTreePath;
import com.sun.source.util.DocTrees;
import com.sun.source.util.TreePath;
import jdk.javadoc.doclet.Reporter;

import javax.lang.model.element.Element;
import javax.tools.Diagnostic;

/**
 * A reporter that uses the {@code DocTrees} utility to print messages.
 */
public class DocTreesReporter implements Reporter {

    private final DocTrees docTrees;

    private DocTreesReporter(DocTrees docTrees) {

        this.docTrees = docTrees;
    }

    public static Reporter with(DocTrees docTrees) {
        return new DocTreesReporter(docTrees);
    }

    @Override
    public void print(Diagnostic.Kind kind, String msg) {

        throw new UnsupportedOperationException("Element required");
    }

    protected void print(Diagnostic.Kind kind, TreePath path, String msg) {

        docTrees.printMessage(kind, msg, path.getLeaf(),
                path.getCompilationUnit());
    }

    @Override
    public void print(Diagnostic.Kind kind, DocTreePath path, String msg) {

        print(kind, path.getTreePath(), msg);
    }

    @Override
    public void print(Diagnostic.Kind kind, Element e, String msg) {

        print(kind, docTrees.getPath(e), msg);
    }
}
