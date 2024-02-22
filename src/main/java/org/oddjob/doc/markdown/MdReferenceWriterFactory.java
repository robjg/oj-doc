package org.oddjob.doc.markdown;

import org.oddjob.arooa.beandocs.BeanDoc;
import org.oddjob.arooa.beandocs.BeanDocArchive;
import org.oddjob.arooa.beandocs.element.LinkElement;
import org.oddjob.doc.doclet.ReferenceWriter;
import org.oddjob.doc.doclet.ReferenceWriterFactory;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

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

        ApiLinkProvider linkProvider;
        if (apiLink.contains(":")) {
            linkProvider = new AbsoluteApiLink(apiLink);
        }
        else {
            linkProvider = new RelativeApiLink(apiLink);
        }

        MdContextProvider contextProvider = new ContextProviderImpl(linkProvider);

        return new MdReferenceWriter(
                Objects.requireNonNull(destination, "No destination"),
                title,
                contextProvider);
    }

    interface ApiLinkProvider {

        Function<String, String> apiLinkFor(String pathToRoot);
    }

    static class RelativeApiLink implements ApiLinkProvider {

        private final String relativeLink;

        RelativeApiLink(String relativeLink) {
            this.relativeLink = relativeLink;
        }

        @Override
        public Function<String, String> apiLinkFor(String pathToRoot) {
            return fileName -> pathToRoot + "/" + relativeLink + "/" + fileName;
        }
    }

    static class AbsoluteApiLink implements ApiLinkProvider {

        private final String absoluteLink;

        AbsoluteApiLink(String absoluteLink) {
            this.absoluteLink = absoluteLink;
        }

        @Override
        public Function<String, String> apiLinkFor(String pathToRoot) {
            return fileName -> absoluteLink + "/" + fileName;
        }
    }

    class ContextProviderImpl implements MdContextProvider {

        private final ApiLinkProvider apiLinkProvider;

        ContextProviderImpl(ApiLinkProvider apiLinkProvider) {
            this.apiLinkProvider = apiLinkProvider;
        }

        @Override
        public MdContext contextFor(String pathToRoot) {

            Function<String, String> apiLinkFor = apiLinkProvider.apiLinkFor(pathToRoot);

            Function<String, String> refLink = fileName -> pathToRoot + "/" + fileName;

            return new MdContextImpl(archive, apiLinkFor, refLink);
        }
    }

    static class MdContextImpl implements MdContext {

        private final BeanDocArchive archive;

        private final Function<String, String> apiLink;

        private final Function<String, String> refLink;

        MdContextImpl(BeanDocArchive archive, Function<String, String> apiLink, Function<String, String> refLink) {
            this.archive = archive;
            this.apiLink = apiLink;
            this.refLink = refLink;
        }

        public String processLink(LinkElement linkElement) {

            String qualifiedType = linkElement.getQualifiedType();

            if (qualifiedType == null) {
                String link = linkElement.getSignature();
                if (linkElement.getLabel() != null) {
                    link += " " + linkElement.getLabel();
                }
                return "<code>" + link + "</code>";
            }

            String htmlFileName = qualifiedType.replace('.', '/') + ".html";
            String mdFileName = qualifiedType.replace('.', '/') + ".md";

            return Optional.ofNullable(archive.docFor(qualifiedType))
                    .map(BeanDoc::getName)
                    .map(componentName -> "[" + componentName + "](" + refLink.apply(mdFileName) + ")")
                    .orElseGet(() ->  "[" + qualifiedType + "](" + apiLink.apply(htmlFileName) + ")");
        }
    }

}
