package org.oddjob.doc.taglet;

import com.sun.source.doctree.DocTree;
import com.sun.source.doctree.UnknownInlineTagTree;
import jdk.javadoc.doclet.Taglet;
import org.oddjob.doc.DocContext;
import org.oddjob.doc.doclet.CustomTagNames;
import org.oddjob.doc.html.HtmlVisitor;
import org.oddjob.doc.loader.PlainTextLoader;
import org.oddjob.doc.util.DocUtil;

import javax.lang.model.element.Element;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Loads a plain text resource and formats it for Javadoc.
 */
public class PlainTextResourceTaglet implements Taglet {
    @Override
    public Set<Location> getAllowedLocations() {
        return Set.of(Location.TYPE, Location.METHOD, Location.FIELD);
    }

    @Override
    public boolean isInlineTag() {
        return true;
    }

    @Override
    public String getName() {
        return CustomTagNames.TEXT_RESOURCE_TAG;
    }

    @Override
    public String toString(List<? extends DocTree> tags, Element element) {

        UnknownInlineTagTree inlineTagTree = (UnknownInlineTagTree) tags.get(0);

        String resource = DocUtil.toString(inlineTagTree.getContent());

        ClassLoader classLoader = Objects.requireNonNullElseGet(
                Thread.currentThread().getContextClassLoader(),
                () -> getClass().getClassLoader());

        PlainTextLoader loader = PlainTextLoader.fromResource(classLoader);

        return loader.load(resource)
                .accept(HtmlVisitor.instance(), DocContext.noLinks());
    }
}
