package org.oddjob.doc.taglet;

import com.sun.source.doctree.DocTree;
import com.sun.source.doctree.UnknownInlineTagTree;
import jdk.javadoc.doclet.Taglet;
import org.oddjob.doc.doclet.CustomTagNames;
import org.oddjob.doc.html.HtmlContext;
import org.oddjob.doc.html.HtmlVisitor;
import org.oddjob.doc.loader.XmlLoader;
import org.oddjob.doc.util.DocUtil;

import javax.lang.model.element.Element;
import java.util.List;
import java.util.Set;

/**
 * Loads and XML resource and formats it for the Javadoc. Resources must be on the classpath of the Taglet.
 * Note that this isn't actually used at the moment as we process the description tags using the
 * {@link DescriptionTaglet} which doesn't defer to this which it probably should do.
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

        String resource = DocUtil.unknownTagContent(inlineTagTree);

        XmlLoader loader = XmlLoader.fromResource(getClass().getClassLoader());

        return loader.load(resource)
                .accept(HtmlVisitor.instance(), HtmlContext.noLinks());
    }
}
