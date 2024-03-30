package org.oddjob.doc.html;

import org.oddjob.arooa.beandocs.element.LinkElement;
import org.oddjob.doc.doclet.ReferenceWriter;
import org.oddjob.doc.doclet.ReferenceWriterFactory;
import org.oddjob.doc.util.*;

import java.nio.file.Path;
import java.util.Objects;

/**
 * Creates an {@link ReferenceWriter} for HTML.
 */
public class HtmlReferenceWriterFactory extends ReferenceWriterFactory {

    public static final String REFERENCE_EXTENSION = "html";

    @Override
    public ReferenceWriter create() {

        Path out = Path.of(Objects.requireNonNull(getDestination(), "No destination"));

        LinkResolverProvider linkProvider = ExternLinkProvider.withErrorReporter(getErrorConsumer())
                .addLinks(getApiLinks(), out);

        HtmlContextProvider contextProvider = new ContextProviderImpl(linkProvider);

        return new HtmlReferenceWriter(
                out,
                getTitle(),
                contextProvider);
    }

    class ContextProviderImpl implements HtmlContextProvider {

        private final LinkProcessorProvider linkProcessorProvider;

        ContextProviderImpl(LinkResolverProvider apiLinkProvider) {
            this.linkProcessorProvider = RefFirstLinks.newProcessorProvider(
                    apiLinkProvider, getArchive(),
                    new HtmlLinks(), REFERENCE_EXTENSION);
        }

        @Override
        public HtmlContext contextFor(String pathToRoot) {

            return new HtmlContextImpl(linkProcessorProvider.linkProcessorFor(pathToRoot));
        }
    }
    static class HtmlContextImpl implements HtmlContext {

        private final LinkProcessor linkProcessor;

        HtmlContextImpl(LinkProcessor linkProcessor) {
            this.linkProcessor = linkProcessor;
        }

        @Override
        public String hyperlinkFor(LinkElement linkElement) {

            return linkProcessor.processLink(linkElement);
        }

    }

}
