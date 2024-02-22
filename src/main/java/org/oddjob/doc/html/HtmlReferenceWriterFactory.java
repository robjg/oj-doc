package org.oddjob.doc.html;

import org.oddjob.arooa.beandocs.BeanDoc;
import org.oddjob.arooa.beandocs.BeanDocArchive;
import org.oddjob.doc.doclet.InlineHelperProvider;
import org.oddjob.doc.doclet.ReferenceHelperProvider;
import org.oddjob.doc.doclet.ReferenceWriter;
import org.oddjob.doc.doclet.ReferenceWriterFactory;

import java.util.Objects;

/**
 * Creates an {@link ReferenceWriter} for HTML.
 */
public class HtmlReferenceWriterFactory implements ReferenceWriterFactory {

    private BeanDocArchive archive;

    private String destination;

    private String title;

    private String apiLink;

    @Override
    public void setArchive(BeanDocArchive archive) {
        this.archive = archive;
    }

    @Override
    public void setDestination(String destination) {
        this.destination = destination;
    }

    @Override
    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public void setApiLink(String apiLink) {
        this.apiLink = apiLink;
    }

    @Override
    public ReferenceWriter create() {

        InlineHelperProvider inlineHelperProvider = new ReferenceHelperProvider(
                fqn -> {
                    BeanDoc beanDoc = archive.docFor(fqn);
                    if (beanDoc == null) {
                        return null;
                    } else {
                        return beanDoc.getName();
                    }
                },
                pathToRefRoot -> pathToRefRoot + "/" + apiLink
        );

        return new HtmlReferenceWriter(
                Objects.requireNonNull(destination, "No destination"),
                title,
                inlineHelperProvider);
    }
}
