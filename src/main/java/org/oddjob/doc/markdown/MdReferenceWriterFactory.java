package org.oddjob.doc.markdown;

import org.oddjob.arooa.beandocs.BeanDocArchive;
import org.oddjob.arooa.beandocs.element.LinkElement;
import org.oddjob.doc.doclet.ReferenceWriter;
import org.oddjob.doc.doclet.ReferenceWriterFactory;
import org.oddjob.doc.util.ApiLinkProvider;
import org.oddjob.doc.util.LinkProcessor;
import org.oddjob.doc.util.LinkProcessorProvider;

import java.util.Objects;

/**
 * Creates an {@link ReferenceWriter} for Markdown.
 */
public class MdReferenceWriterFactory implements ReferenceWriterFactory {

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

        ApiLinkProvider linkProvider = ApiLinkProvider.providerFor(apiLink);

        MdContextProvider contextProvider = new ContextProviderImpl(linkProvider);

        return new MdReferenceWriter(
                Objects.requireNonNull(destination, "No destination"),
                title,
                contextProvider);
    }


    class ContextProviderImpl implements MdContextProvider {

        private final LinkProcessorProvider linkProcessorProvider;

        ContextProviderImpl(ApiLinkProvider apiLinkProvider) {
            this.linkProcessorProvider = MarkdownLinks.newProcessorProvider(apiLinkProvider, archive);
        }

        @Override
        public MdContext contextFor(String pathToRoot) {

            return new MdContextImpl(linkProcessorProvider.linkProcessorFor(pathToRoot));
        }
    }

    static class MdContextImpl implements MdContext {

        private final LinkProcessor linkProcessor;

        MdContextImpl(LinkProcessor linkProcessor) {
            this.linkProcessor = linkProcessor;
        }

        public String processLink(LinkElement linkElement) {

            return linkProcessor.processLink(linkElement);
        }

    }

}
