package org.oddjob.doc.util;

import org.oddjob.arooa.beandocs.BeanDoc;
import org.oddjob.arooa.beandocs.BeanDocArchive;
import org.oddjob.arooa.beandocs.element.LinkElement;

import java.util.Optional;
import java.util.function.Function;

/**
 * Link Processor that checks an Archive first to see if a link is for a Reference Page
 * otherwise it defers to an {@link ApiLinkProvider}
 */
public class RefFirstLinks implements LinkProcessorProvider {

    private final ApiLinkProvider apiLinkProvider;

    private final BeanDocArchive archive;

    private final LinkFormatter linkFormatter;

    RefFirstLinks(ApiLinkProvider apiLinkProvider,
                  BeanDocArchive archive,
                  LinkFormatter linkFormatter) {
        this.apiLinkProvider = apiLinkProvider;
        this.archive = archive;
        this.linkFormatter = linkFormatter;
    }

    public static LinkProcessorProvider newProcessorProvider(ApiLinkProvider apiLinkProvider,
                                                             BeanDocArchive archive,
                                                             LinkFormatter linkFormatter) {
        return new RefFirstLinks(apiLinkProvider, archive, linkFormatter);
    }

    @Override
    public LinkProcessor linkProcessorFor(String pathToRoot) {

        Function<String, String> apiLinkFor = apiLinkProvider.apiLinkFor(pathToRoot);

        Function<String, String> refLinkFor = fileName -> pathToRoot + "/" + fileName;

        return new MdLinkProcessor(archive, apiLinkFor, refLinkFor, linkFormatter);
    }

    static class MdLinkProcessor implements LinkProcessor {

        private final BeanDocArchive archive;

        private final Function<String, String> apiLinkFor;

        private final Function<String, String> refLinkFor;

        private final LinkFormatter linkFormatter;

        MdLinkProcessor(BeanDocArchive archive, Function<String, String> apiLinkFor, Function<String, String> refLinkFor, LinkFormatter linkFormatter) {
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

            String htmlFileName = qualifiedType.replace('.', '/') + ".html";
            String mdFileName = qualifiedType.replace('.', '/') + ".md";

            return Optional.ofNullable(archive.docFor(qualifiedType))
                    .map(BeanDoc::getName)
                    .map(componentName -> linkFormatter.linkFor(
                            refLinkFor.apply(mdFileName), componentName))
                    .orElseGet(() ->  linkFormatter.linkFor(
                            apiLinkFor.apply(htmlFileName), qualifiedType));
        }
    }
}
