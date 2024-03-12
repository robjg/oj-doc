package org.oddjob.doc.markdown;

import org.oddjob.arooa.beandocs.BeanDoc;
import org.oddjob.arooa.beandocs.BeanDocArchive;
import org.oddjob.arooa.beandocs.element.LinkElement;
import org.oddjob.doc.util.ApiLinkProvider;
import org.oddjob.doc.util.LinkProcessor;
import org.oddjob.doc.util.LinkProcessorProvider;

import java.util.Optional;
import java.util.function.Function;

public class MarkdownLinks implements LinkProcessorProvider {

    private final ApiLinkProvider apiLinkProvider;

    private final BeanDocArchive archive;

    MarkdownLinks(ApiLinkProvider apiLinkProvider, BeanDocArchive archive) {
        this.apiLinkProvider = apiLinkProvider;
        this.archive = archive;
    }

    public static LinkProcessorProvider newProcessorProvider(ApiLinkProvider apiLinkProvider, BeanDocArchive archive) {
        return new MarkdownLinks(apiLinkProvider, archive);
    }

    @Override
    public LinkProcessor linkProcessorFor(String pathToRoot) {

        Function<String, String> apiLinkFor = apiLinkProvider.apiLinkFor(pathToRoot);

        Function<String, String> refLinkFor = fileName -> pathToRoot + "/" + fileName;

        return new MdLinkProcessor(archive, apiLinkFor, refLinkFor);
    }

    static class MdLinkProcessor implements LinkProcessor {

        private final BeanDocArchive archive;

        private final Function<String, String> apiLinkFor;

        private final Function<String, String> refLinkFor;

        MdLinkProcessor(BeanDocArchive archive, Function<String, String> apiLinkFor, Function<String, String> refLinkFor) {
            this.archive = archive;
            this.apiLinkFor = apiLinkFor;
            this.refLinkFor = refLinkFor;
        }

        @Override
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
