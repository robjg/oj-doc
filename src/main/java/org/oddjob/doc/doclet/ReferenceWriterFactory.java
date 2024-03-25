package org.oddjob.doc.doclet;

import org.oddjob.arooa.beandocs.BeanDocArchive;

import java.util.function.Consumer;

/**
 * Creates an {@link ReferenceWriter}.
 */
public abstract class ReferenceWriterFactory {

    private BeanDocArchive archive;

    private String destination;

    private String title;

    private Iterable<? extends String> apiLinks;

    private Consumer<? super String> errorConsumer;

    public BeanDocArchive getArchive() {
        return archive;
    }

    public void setArchive(BeanDocArchive archive) {
        this.archive = archive;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Iterable<? extends String> getApiLinks() {
        return apiLinks;
    }

    public void setApiLinks(Iterable<? extends String> apiLinks) {
        this.apiLinks = apiLinks;
    }

    public Consumer<? super String> getErrorConsumer() {
        return errorConsumer;
    }

    public void setErrorConsumer(Consumer<? super String> errorConsumer) {
        this.errorConsumer = errorConsumer;
    }

    public abstract ReferenceWriter create();
}
