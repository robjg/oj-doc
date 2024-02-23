package org.oddjob.doc.html;

import org.oddjob.arooa.beandocs.BeanDoc;
import org.oddjob.arooa.beandocs.BeanDocArchive;
import org.oddjob.arooa.beandocs.element.LinkElement;
import org.oddjob.doc.doclet.ReferenceWriter;
import org.oddjob.doc.doclet.ReferenceWriterFactory;
import org.oddjob.doc.util.ApiLinkProvider;

import java.util.Objects;
import java.util.function.Function;

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

        ApiLinkProvider linkProvider = ApiLinkProvider.providerFor(apiLink);

        HtmlContextProvider contextProvider = new ContextProviderImpl(linkProvider);

        return new HtmlReferenceWriter(
                Objects.requireNonNull(destination, "No destination"),
                title,
                contextProvider);
    }

    class ContextProviderImpl implements HtmlContextProvider {

        private final ApiLinkProvider apiLinkProvider;

        ContextProviderImpl(ApiLinkProvider apiLinkProvider) {
            this.apiLinkProvider = apiLinkProvider;
        }

        @Override
        public HtmlContext contextFor(String pathToRoot) {

            Function<String, String> apiLinkFor = apiLinkProvider.apiLinkFor(pathToRoot);

            Function<String, String> refLinkFor = fileName -> pathToRoot + "/" + fileName;

            return new HtmlContextImpl(archive, apiLinkFor, refLinkFor);
        }
    }
    static class HtmlContextImpl implements HtmlContext {

        private final BeanDocArchive archive;

        private final Function<String, String> apiLinkFor;

        private final Function<String, String> refLinkFor;

        HtmlContextImpl(BeanDocArchive archive,
                        Function<String, String> apiLinkFor,
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

            BeanDoc beanDoc = archive.docFor(qualifiedType);

            if (beanDoc == null) {
                return "<code><a href='" + apiLinkFor.apply(fileName)  + "'>"
                        + qualifiedType + "</a></code>";
            }
            else {
                String componentName = beanDoc.getName();
                return "<a href='" + refLinkFor.apply(fileName) + "'>"
                        + componentName + "</a>";
            }
        }

    }

}
