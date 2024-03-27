package org.oddjob.doc.html;

import org.oddjob.arooa.beandocs.BeanDocArchive;
import org.oddjob.arooa.beandocs.element.LinkElement;
import org.oddjob.doc.doclet.ReferenceWriter;
import org.oddjob.doc.doclet.ReferenceWriterFactory;
import org.oddjob.doc.util.ExternLinkProvider;
import org.oddjob.doc.util.LinkResolver;
import org.oddjob.doc.util.LinkResolverProvider;

import java.nio.file.Path;
import java.util.Objects;
import java.util.function.Function;

/**
 * Creates an {@link ReferenceWriter} for HTML.
 */
public class HtmlReferenceWriterFactory extends ReferenceWriterFactory {

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

        private final LinkResolverProvider apiLinkProvider;

        ContextProviderImpl(LinkResolverProvider apiLinkProvider) {
            this.apiLinkProvider = apiLinkProvider;
        }

        @Override
        public HtmlContext contextFor(String pathToRoot) {

            LinkResolver apiLinkFor = apiLinkProvider.apiLinkFor(pathToRoot);

            Function<String, String> refLinkFor = fileName -> pathToRoot + "/" + fileName;

            return new HtmlContextImpl(getArchive(), apiLinkFor, refLinkFor);
        }
    }
    static class HtmlContextImpl implements HtmlContext {

        private final BeanDocArchive archive;

        private final LinkResolver apiLinkFor;

        private final Function<String, String> refLinkFor;

        HtmlContextImpl(BeanDocArchive archive,
                        LinkResolver apiLinkFor,
                        Function<String, String> refLinkFor) {
            this.archive = archive;
            this.apiLinkFor = apiLinkFor;
            this.refLinkFor = refLinkFor;
        }

        @Override
        public String hyperlinkFor(LinkElement linkElement) {

            String qualifiedType = linkElement.getQualifiedType();

            if (qualifiedType == null) {
                String link = linkElement.getSignature();
                if (linkElement.getLabel() != null) {
                    link += " " + linkElement.getLabel();
                }
                return "<code>" + link + "</code>";
            }

            String fileName = qualifiedType.replace('.', '/') +  ".html";

            return archive.docFor(qualifiedType)
                    .map(beanDoc -> (
                            "<a href='" + refLinkFor.apply(fileName) + "'>"
                                  + beanDoc.getName() + "</a>"))
                    .or(() -> apiLinkFor.resolve(qualifiedType, "html")
                            .map(link -> "<code><a href='" + link  + "'>"
                                + qualifiedType + "</a></code>"))
                    .orElseGet(() -> "<code>" + qualifiedType + "</code>");
        }

    }

}
