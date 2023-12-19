package org.oddjob.doc.taglet;

import com.sun.source.doctree.DocTree;
import com.sun.source.doctree.UnknownInlineTagTree;
import jdk.javadoc.doclet.Taglet;
import org.oddjob.doc.doclet.CustomTagNames;
import org.oddjob.tools.includes.XMLResourceLoader;

import javax.lang.model.element.Element;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Loads and XML resource and formats it for the Javadoc. Resources must be on the classpath of the Taglet.
 */
public class XmlResourceTaglet implements Taglet {

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
        return CustomTagNames.XML_RESOURCE_TAG_NAME;
    }

    @Override
    public String toString(List<? extends DocTree> tags, Element element) {

        UnknownInlineTagTree inlineTagTree = (UnknownInlineTagTree) tags.get(0);

        String resource = inlineTagTree.getContent().get(0).toString();

        ClassLoader classLoader = Objects.requireNonNullElseGet(
                Thread.currentThread().getContextClassLoader(),
                () -> getClass().getClassLoader());

        return XMLResourceLoader.loadXml(resource, classLoader);
    }
}
