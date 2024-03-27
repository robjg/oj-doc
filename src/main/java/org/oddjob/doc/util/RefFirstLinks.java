package org.oddjob.doc.util;

import org.oddjob.arooa.beandocs.BeanDoc;
import org.oddjob.arooa.beandocs.BeanDocArchive;
import org.oddjob.arooa.beandocs.element.LinkElement;

import java.util.function.Function;

/**
 * Link Processor that checks an Archive first to see if a link is for a Reference Page
 * otherwise it defers to an {@link LinkPaths}
 */
public class RefFirstLinks implements LinkProcessorProvider {

    private final LinkResolverProvider apiLinkProvider;

    private final BeanDocArchive archive;

    private final LinkFormatter linkFormatter;

    RefFirstLinks(LinkResolverProvider apiLinkProvider,
                  BeanDocArchive archive,
                  LinkFormatter linkFormatter) {
        this.apiLinkProvider = apiLinkProvider;
        this.archive = archive;
        this.linkFormatter = linkFormatter;
    }

    public static LinkProcessorProvider newProcessorProvider(LinkResolverProvider apiLinkProvider,
                                                             BeanDocArchive archive,
                                                             LinkFormatter linkFormatter) {
        return new RefFirstLinks(apiLinkProvider, archive, linkFormatter);
    }

    @Override
    public LinkProcessor linkProcessorFor(String pathToRoot) {

        LinkResolver apiLinkFor = apiLinkProvider.apiLinkFor(pathToRoot);

        Function<String, String> refLinkFor = fileName -> pathToRoot + "/" + fileName;

        return new ProcessorImpl(archive, apiLinkFor, refLinkFor, linkFormatter);
    }

    static class ProcessorImpl implements LinkProcessor {

        private final BeanDocArchive archive;

        private final LinkResolver apiLinkFor;

        private final Function<String, String> refLinkFor;

        private final LinkFormatter linkFormatter;

        ProcessorImpl(BeanDocArchive archive,
                      LinkResolver apiLinkFor,
                      Function<String, String> refLinkFor,
                      LinkFormatter linkFormatter) {
            this.archive = archive;
            this.apiLinkFor = apiLinkFor;
            this.refLinkFor = refLinkFor;
            this.linkFormatter = linkFormatter;
        }

        @Override
        public String processLink(LinkElement linkElement) {

            String qualifiedType = linkElement.getQualifiedType();

            if (qualifiedType == null) {
                return linkFormatter.noLinkFor(linkElement.getSignature(), linkElement.getLabel());
            }

            String mdFileName = qualifiedType.replace('.', '/') + ".md";

            return archive.docFor(qualifiedType)
                    .map(BeanDoc::getName)
                    .map(componentName -> linkFormatter.linkFor(
                            refLinkFor.apply(mdFileName), componentName))
                    .or(() -> apiLinkFor.resolve(qualifiedType, "html")
                                    .map(link -> linkFormatter.linkFor(link, qualifiedType)))
                    .orElseGet(() -> linkFormatter.noLinkFor(qualifiedType, null));
        }
    }
}
