package org.oddjob.doc.html;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class HtmlLinksTest {

    @Test
    void noLinkFor() {

        HtmlLinks htmlLinks = new HtmlLinks();

        assertThat(htmlLinks.noLinkFor("org.foo.Job", "SomeJob"),
                is("<code>SomeJob org.foo.Job</code>"));

        assertThat(htmlLinks.noLinkFor("org.foo.Job", null),
                is("<code>org.foo.Job</code>"));
    }

    @Test
    void linkFor() {

        HtmlLinks htmlLinks = new HtmlLinks();

        assertThat(htmlLinks.linkFor("../../org/foo/Job.html", "SomeJob"),
                is("<code><a href='../../org/foo/Job.html'>SomeJob</a></code>"));

        assertThat(htmlLinks.linkFor("http://foo.bar/api/org/foo/Job.html", null),
                is("<code><a href='http://foo.bar/api/org/foo/Job.html'>http://foo.bar/api/org/foo/Job.html</a></code>"));
    }
}