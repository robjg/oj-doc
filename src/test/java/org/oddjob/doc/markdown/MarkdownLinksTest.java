package org.oddjob.doc.markdown;

import org.junit.jupiter.api.Test;
import org.oddjob.arooa.beandocs.BeanDoc;
import org.oddjob.arooa.beandocs.BeanDocArchive;
import org.oddjob.arooa.beandocs.element.LinkElement;
import org.oddjob.doc.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MarkdownLinksTest {

    @Test
    void relativePath() {

        BeanDoc doc = mock(BeanDoc.class);
        when(doc.getName()).thenReturn("SomeJob");

        BeanDocArchive beanDocArchive = mock(BeanDocArchive.class);
        when(beanDocArchive.docFor("org.foo.Job")).thenReturn(doc);

        String pathToRoot = DocUtil.pathToRoot("org.bar.A");

        LinkResolverProvider apiLinkProvider = ApiLinkProvider.relativeLinkProvider("../api");

        LinkProcessorProvider linkProcessorProvider = RefFirstLinks.newProcessorProvider(
                apiLinkProvider, beanDocArchive, new MarkdownLinks());

        LinkProcessor linkProcessor = linkProcessorProvider.linkProcessorFor(pathToRoot);

        LinkElement refElement = new LinkElement();
        refElement.setQualifiedType("org.foo.Job");

        LinkElement codeElement = new LinkElement();
        codeElement.setQualifiedType("org.bar.Stuff");

        String refLink = linkProcessor.processLink(refElement);
        assertThat(refLink, is("[SomeJob](../../org/foo/Job.md)"));

        String codeLink = linkProcessor.processLink(codeElement);
        assertThat(codeLink, is("[org.bar.Stuff](../../../api/org/bar/Stuff.html)"));
    }

    @Test
    void urlPathToApi() {

        BeanDoc doc = mock(BeanDoc.class);
        when(doc.getName()).thenReturn("SomeJob");

        BeanDocArchive beanDocArchive = mock(BeanDocArchive.class);
        when(beanDocArchive.docFor("org.foo.Job")).thenReturn(doc);

        String pathToRoot = DocUtil.pathToRoot("org.bar.A");

        LinkResolverProvider apiLinkProvider = ApiLinkProvider.absoluteLinkProvider("http://www.foo.org/api");

        LinkProcessorProvider linkProcessorProvider = RefFirstLinks.newProcessorProvider(
                apiLinkProvider, beanDocArchive, new MarkdownLinks());

        LinkProcessor linkProcessor = linkProcessorProvider.linkProcessorFor(pathToRoot);

        LinkElement refElement = new LinkElement();
        refElement.setQualifiedType("org.foo.Job");

        LinkElement codeElement = new LinkElement();
        codeElement.setQualifiedType("org.bar.Stuff");

        String refLink = linkProcessor.processLink(refElement);
        assertThat(refLink, is("[SomeJob](../../org/foo/Job.md)"));

        String codeLink = linkProcessor.processLink(codeElement);
        assertThat(codeLink, is("[org.bar.Stuff](http://www.foo.org/api/org/bar/Stuff.html)"));
    }
}