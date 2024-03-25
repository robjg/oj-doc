package org.oddjob.doc.util;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class ApiLinkProviderTest {

    @Test
    void whenRelativeLinkThenOk() {

        LinkResolverProvider linkProvider = ApiLinkProvider.relativeLinkProvider("../api");

        LinkResolver apiFunc = linkProvider.apiLinkFor("../..");

        String link = apiFunc.resolve("org.foo.Stuff", "foo");

        assertThat(link, is("../../../api/org/foo/Stuff.foo"));
    }

    @Test
    void whenUrlLinkThenOk() {

        LinkResolverProvider linkProvider = ApiLinkProvider.absoluteLinkProvider("http://www.foo.org/api");

        LinkResolver apiFunc = linkProvider.apiLinkFor("../..");

        String link = apiFunc.resolve("org.foo.Stuff", "foo");

        assertThat(link, is("http://www.foo.org/api/org/foo/Stuff.foo"));
    }

}