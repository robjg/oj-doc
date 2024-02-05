package org.oddjob.doc.util;

import com.sun.source.doctree.DocCommentTree;
import com.sun.source.doctree.DocTree;
import com.sun.source.doctree.ReferenceTree;
import com.sun.source.doctree.UnknownInlineTagTree;
import com.sun.source.util.DocTreePath;
import com.sun.source.util.DocTrees;
import com.sun.source.util.TreePath;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

/**
 *  * Utility methods for working with the Doc Tree and Source Elements.
 */
public class DocUtil {

    public static String toString(List<? extends DocTree> tags) {

        StringBuilder builder = new StringBuilder();
        tags.forEach(builder::append);
        return builder.toString();
    }

    public static String relativePath(Element element, String targetSignature ) {
        if (element instanceof TypeElement) {
            return relativePath((TypeElement) element, targetSignature);
        }
        return relativePath(element.getEnclosingElement(), targetSignature);
    }

    /**
     * from aa.bb.cc.X to ss.tt.Y is ../../../ss/tt
     * from aa.bb.cc.X to aa.bb.oo.Y is ../../oo
     * from aa.bb.cc.X to aa.bb.cc.Y is ""
     *
     * @param element
     * @param targetSignature
     * @return The relative path
     */
    public static String relativePath(TypeElement element, String targetSignature ) {

        Path targetDir = Paths.get(targetSignature.replace('.', '/'))
                .getParent();

        Path srcDir = Paths.get(element.getQualifiedName().toString().replace('.', '/'))
                .getParent();

        return srcDir.relativize(targetDir).toString()
                .replace('\\', '/');
    }

    public static String simpleName(String qualifiedName) {
        int pos = qualifiedName.lastIndexOf('.');
        return pos < 0 ? qualifiedName : qualifiedName.substring(pos + 1);
    }

    /**
     * Resolve a reference tree from a link.
     * Most of this comes from {@code jdk.javadoc.internal.doclets.toolkit.CommentHelper}
     *
     * @param docTrees The utilities.
     * @param element The element to resolve from.
     * @param rtree The reference.
     *
     * @return The element or null if the element can't be found withing the tree.
     */
    public static Element getReferenceElement(DocTrees docTrees, Element element, ReferenceTree rtree) {

        TreePath path = docTrees.getPath(element);

        DocCommentTree docTree = docTrees.getDocCommentTree(element);

        DocTreePath docTreePath = DocTreePath.getPath(path, docTree, rtree);

        if (docTreePath == null) {
            // The referenced element is not on the classpath.
            return null;
        }

        return docTrees.getElement(docTreePath);
    }

    public static String pathToRoot(TypeElement typeElement) {

        return pathToRoot(typeElement.getQualifiedName());
    }

    public static String pathToRoot(CharSequence qualifiedName) {

        return qualifiedName.chars()
                .filter(c -> c == '.')
                .mapToObj(ignore -> "..")
                .collect(Collectors.joining("/"));
    }

    public static String fqcnFor(TypeElement classDoc) {
        return classDoc.getQualifiedName().toString();
    }

    /**
     * For Unknown tags such as the loader tags where we just expect the file name.
     *
     * @param inlineTagTree The Unknown tag.
     * @return The contents.
     */
    public static String unknownTagContent(UnknownInlineTagTree inlineTagTree) {

        return inlineTagTree.getContent().get(0).toString();
    }
}
