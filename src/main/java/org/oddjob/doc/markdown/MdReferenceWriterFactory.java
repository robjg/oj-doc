package org.oddjob.doc.markdown;

import org.oddjob.arooa.beandocs.BeanDoc;
import org.oddjob.arooa.beandocs.BeanDocArchive;
import org.oddjob.arooa.beandocs.element.LinkElement;
import org.oddjob.doc.doclet.ReferenceWriter;
import org.oddjob.doc.doclet.ReferenceWriterFactory;
import org.oddjob.doc.util.ApiLinkProvider;

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

        ApiLinkProvider linkProvider = ApiLinkProvider.providerFor(apiLink);

        MdContextProvider contextProvider = new ContextProviderImpl(linkProvider);

        return new MdReferenceWriter(
                Objects.requireNonNull(destination, "No destination"),
                title,
                contextProvider);
    }


    class ContextProviderImpl implements MdContextProvider {

        private final ApiLinkProvider apiLinkProvider;

        ContextProviderImpl(ApiLinkProvider apiLinkProvider) {
            this.apiLinkProvider = apiLinkProvider;
        }

        @Override
        public MdContext contextFor(String pathToRoot) {

            Function<String, String> apiLinkFor = apiLinkProvider.apiLinkFor(pathToRoot);

            Function<String, String> refLinkFor = fileName -> pathToRoot + "/" + fileName;

            return new MdContextImpl(archive, apiLinkFor, refLinkFor);
        }
    }

    static class MdContextImpl implements MdContext {

        private final BeanDocArchive archive;

        private final Function<String, String> apiLinkFor;

        private final Function<String, String> refLinkFor;

        MdContextImpl(BeanDocArchive archive, Function<String, String> apiLinkFor, Function<String, String> refLinkFor) {
            this.archive = archive;
            this.apiLinkFor = apiLinkFor;
            this.refLinkFor = refLinkFor;
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
                    .map(componentName -> "[" + componentName + "](" + refLinkFor.apply(mdFileName) + ")")
                    .orElseGet(() ->  "[" + qualifiedType + "](" + apiLinkFor.apply(htmlFileName) + ")");
        }
    }

}
