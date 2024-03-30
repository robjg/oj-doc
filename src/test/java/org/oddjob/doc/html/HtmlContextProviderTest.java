package org.oddjob.doc.html;

import org.junit.jupiter.api.Test;
import org.oddjob.arooa.beandocs.BeanDoc;
import org.oddjob.arooa.beandocs.BeanDocArchive;
import org.oddjob.arooa.beandocs.element.LinkElement;
import org.oddjob.doc.util.*;

import java.util.Optional;
import java.util.function.Function;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class HtmlContextProviderTest {

    @Test
    void relativePath() {

        BeanDoc doc = mock(BeanDoc.class);
        when(doc.getName()).thenReturn("SomeJob");

        BeanDocArchive beanDocArchive = mock(BeanDocArchive.class);
        when(beanDocArchive.docFor("org.foo.Job")).thenReturn(Optional.of(doc));

        String pathToRoot = DocUtil.pathToRoot("org.bar.A");

        LinkPaths apiLinkProvider = LinkPaths.relativeLinkProvider("../api", "");

        LinkResolverProvider linkResolverProvider = ptr -> {

            LinkPaths.Func func = apiLinkProvider.apiLinkFor(pathToRoot);

            return (link, extension) -> Optional.of(func.resolve(link, extension));
        };

        Function<String, String> refLinkFor = fileName -> pathToRoot + "/" + fileName;

        LinkProcessorProvider linkProcessorProvider = RefFirstLinks.newProcessorProvider(
                linkResolverProvider, beanDocArchive, new HtmlLinks(), "html");

        HtmlContext htmlContext = new HtmlReferenceWriterFactory.HtmlContextImpl(
                linkProcessorProvider.linkProcessorFor(pathToRoot));

        LinkElement refElement = new LinkElement();
        refElement.setQualifiedType("org.foo.Job");

        LinkElement codeElement = new LinkElement();
        codeElement.setQualifiedType("org.bar.Stuff");

        String refLink = htmlContext.hyperlinkFor(refElement);
        assertThat(refLink, is("<code><a href='../../org/foo/Job.html'>SomeJob</a></code>"));

        String codeLink = htmlContext.hyperlinkFor(codeElement);
        assertThat(codeLink, is("<code><a href='../../../api/org/bar/Stuff.html'>org.bar.Stuff</a></code>"));
    }
}