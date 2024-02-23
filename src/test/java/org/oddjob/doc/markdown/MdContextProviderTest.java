package org.oddjob.doc.markdown;

import org.junit.jupiter.api.Test;
import org.oddjob.arooa.beandocs.BeanDoc;
import org.oddjob.arooa.beandocs.BeanDocArchive;
import org.oddjob.arooa.beandocs.element.LinkElement;
import org.oddjob.doc.util.ApiLinkProvider;
import org.oddjob.doc.util.DocUtil;

import java.util.function.Function;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MdContextProviderTest {

    @Test
    void relativePath() {

        BeanDoc doc = mock(BeanDoc.class);
        when(doc.getName()).thenReturn("SomeJob");

        BeanDocArchive beanDocArchive = mock(BeanDocArchive.class);
        when(beanDocArchive.docFor("org.foo.Job")).thenReturn(doc);

        String pathToRoot = DocUtil.pathToRoot("org.bar.A");

        ApiLinkProvider apiLinkProvider = ApiLinkProvider.providerFor("../api");

        Function<String, String> apiLinkFor = apiLinkProvider.apiLinkFor(pathToRoot);

        Function<String, String> refLinkFor = fileName -> pathToRoot + "/" + fileName;

        MdContext htmlContext = new MdReferenceWriterFactory.MdContextImpl(
                beanDocArchive, apiLinkFor, refLinkFor);

        LinkElement refElement = new LinkElement();
        refElement.setQualifiedType("org.foo.Job");

        LinkElement codeElement = new LinkElement();
        codeElement.setQualifiedType("org.bar.Stuff");

        String refLink = htmlContext.processLink(refElement);
        assertThat(refLink, is("[SomeJob](../../org/foo/Job.md)"));

        String codeLink = htmlContext.processLink(codeElement);
        assertThat(codeLink, is("[org.bar.Stuff](../../../api/org/bar/Stuff.html)"));
    }

    @Test
    void urlPathToApi() {

        BeanDoc doc = mock(BeanDoc.class);
        when(doc.getName()).thenReturn("SomeJob");

        BeanDocArchive beanDocArchive = mock(BeanDocArchive.class);
        when(beanDocArchive.docFor("org.foo.Job")).thenReturn(doc);

        String pathToRoot = DocUtil.pathToRoot("org.bar.A");

        ApiLinkProvider apiLinkProvider = ApiLinkProvider.providerFor("http://www.foo.org/api");

        Function<String, String> apiLinkFor = apiLinkProvider.apiLinkFor(pathToRoot);

        Function<String, String> refLinkFor = fileName -> pathToRoot + "/" + fileName;

        MdContext htmlContext = new MdReferenceWriterFactory.MdContextImpl(
                beanDocArchive, apiLinkFor, refLinkFor);

        LinkElement refElement = new LinkElement();
        refElement.setQualifiedType("org.foo.Job");

        LinkElement codeElement = new LinkElement();
        codeElement.setQualifiedType("org.bar.Stuff");

        String refLink = htmlContext.processLink(refElement);
        assertThat(refLink, is("[SomeJob](../../org/foo/Job.md)"));

        String codeLink = htmlContext.processLink(codeElement);
        assertThat(codeLink, is("[org.bar.Stuff](http://www.foo.org/api/org/bar/Stuff.html)"));
    }
}