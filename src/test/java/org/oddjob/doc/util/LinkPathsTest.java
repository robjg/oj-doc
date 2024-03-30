package org.oddjob.doc.util;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class LinkPathsTest {

    @Test
    void whenRelativeLinkThenOk() {

        LinkPaths linkProvider = LinkPaths.relativeLinkProvider("../api", "");

        LinkPaths.Func apiFunc = linkProvider.apiLinkFor("../..");

        String link = apiFunc.resolve("org.foo.Stuff", "foo");

        assertThat(link, is("../../../api/org/foo/Stuff.foo"));
    }

    @Test
    void whenUrlLinkThenOk() {

        LinkPaths linkProvider = LinkPaths.absoluteLinkProvider("http://www.foo.org/api", "");

        LinkPaths.Func apiFunc = linkProvider.apiLinkFor("../..");

        String link = apiFunc.resolve("org.foo.Stuff", "foo");

        assertThat(link, is("http://www.foo.org/api/org/foo/Stuff.foo"));
    }

    @Test
    void whenUrlAndModuleLinkThenOk() {

        LinkPaths linkProvider = LinkPaths.absoluteLinkProvider("http://www.foo.org/api", "bar.base");

        LinkPaths.Func apiFunc = linkProvider.apiLinkFor("../..");

        String link = apiFunc.resolve("org.foo.Stuff", "foo");

        assertThat(link, is("http://www.foo.org/api/bar.base/org/foo/Stuff.foo"));
    }
}