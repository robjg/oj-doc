package org.oddjob.doc.util;

import org.junit.jupiter.api.Test;

import java.util.function.Function;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class ApiLinkProviderTest {


    @Test
    void whenRelativeLinkThenOk() {

        ApiLinkProvider linkProvider = ApiLinkProvider.providerFor("../api");

        Function<String, String> apiFunc = linkProvider.apiLinkFor("../..");

        String link = apiFunc.apply("org/foo/Stuff.foo");

        assertThat(link, is("../../../api/org/foo/Stuff.foo"));
    }

    @Test
    void whenUrlLinkThenOk() {

        ApiLinkProvider linkProvider = ApiLinkProvider.providerFor("http://www.foo.org/api");

        Function<String, String> apiFunc = linkProvider.apiLinkFor("../..");

        String link = apiFunc.apply("org/foo/Stuff.foo");

        assertThat(link, is("http://www.foo.org/api/org/foo/Stuff.foo"));
    }

}