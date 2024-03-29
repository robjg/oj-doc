package org.oddjob.doc.util;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.oddjob.arooa.beandocs.BeanDoc;
import org.oddjob.arooa.beandocs.BeanDocArchive;
import org.oddjob.arooa.beandocs.element.LinkElement;

import java.util.Arrays;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class RefFirstLinksTest {

    @Test
    void relativePath() {

        BeanDoc doc = mock(BeanDoc.class);
        when(doc.getName()).thenReturn("SomeJob");

        BeanDocArchive beanDocArchive = mock(BeanDocArchive.class);
        when(beanDocArchive.docFor("org.foo.Job"))
                .thenReturn(Optional.of(doc));

        String pathToRoot = DocUtil.pathToRoot("org.bar.A");

        LinkResolverProvider linkResolverProvider = mock(LinkResolverProvider.class);

        LinkResolver linkResolver = mock(LinkResolver.class);
        when(linkResolver.resolve("org.foo.Job", "md"))
                .thenReturn(Optional.of("../../org/foo/Job.md"));
        when(linkResolver.resolve("org.bar.Stuff", "html"))
                .thenReturn(Optional.of("../../../api/org/bar/Stuff.html"));
        when(linkResolver.resolve("org.elsewhere.More", "html"))
                .thenReturn(Optional.empty());

        when(linkResolverProvider.apiLinkFor("../.."))
                .thenReturn(linkResolver);

        LinkFormatter linkFormatter = mock(LinkFormatter.class);
        when(linkFormatter.linkFor(anyString(), anyString()))
                .thenAnswer(invocation -> "LINK: " + Arrays.toString(invocation.getArguments()));
        when(linkFormatter.noLinkFor(anyString(), ArgumentMatchers.isNull()))
                .thenAnswer(invocation -> "NO LINK: " + Arrays.toString(invocation.getArguments()));

        LinkProcessorProvider linkProcessorProvider = RefFirstLinks.newProcessorProvider(
                linkResolverProvider, beanDocArchive, linkFormatter, "xyz");

        LinkProcessor linkProcessor = linkProcessorProvider.linkProcessorFor(pathToRoot);

        verify(linkResolverProvider).apiLinkFor("../..");

        LinkElement refElement = new LinkElement();
        refElement.setQualifiedType("org.foo.Job");

        LinkElement codeElement = new LinkElement();
        codeElement.setQualifiedType("org.bar.Stuff");

        LinkElement noElement = new LinkElement();
        noElement.setQualifiedType("org.elsewhere.More");

        String refLink = linkProcessor.processLink(refElement);

        assertThat(refLink, is("LINK: [../../org/foo/Job.xyz, SomeJob]"));

        String codeLink = linkProcessor.processLink(codeElement);
        assertThat(codeLink, is("LINK: [../../../api/org/bar/Stuff.html, org.bar.Stuff]"));

        String noLink = linkProcessor.processLink(noElement);
        assertThat(noLink, is("NO LINK: [org.elsewhere.More, null]"));
    }

}