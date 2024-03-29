package org.oddjob.doc.markdown;

import org.oddjob.arooa.beandocs.element.LinkElement;
import org.oddjob.doc.doclet.ReferenceWriter;
import org.oddjob.doc.doclet.ReferenceWriterFactory;
import org.oddjob.doc.util.*;

import java.nio.file.Path;
import java.util.Objects;

/**
 * Creates an {@link ReferenceWriter} for Markdown.
 */
public class MdReferenceWriterFactory extends ReferenceWriterFactory {

    public static final String REFERENCE_EXTENSION = "md";

    @Override
    public ReferenceWriter create() {

        Path out = Path.of(Objects.requireNonNull(getDestination(), "No destination"));

        LinkResolverProvider linkProvider = ExternLinkProvider.withErrorReporter(getErrorConsumer())
                .addLinks(getApiLinks(), out);

        MdContextProvider contextProvider = new ContextProviderImpl(linkProvider);

        return new MdReferenceWriter(
                out,
                getTitle(),
                contextProvider);
    }


    class ContextProviderImpl implements MdContextProvider {

        private final LinkProcessorProvider linkProcessorProvider;

        ContextProviderImpl(LinkResolverProvider apiLinkProvider) {
            this.linkProcessorProvider = RefFirstLinks.newProcessorProvider(
                    apiLinkProvider, getArchive(),
                    new MarkdownLinks(), REFERENCE_EXTENSION);
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
