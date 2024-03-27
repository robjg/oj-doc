package org.oddjob.doc.markdown;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class MarkdownLinksTest {

    @Test
    void linkFor() {

        MarkdownLinks markdownLinks = new MarkdownLinks();

        assertThat(markdownLinks.linkFor("../../org/foo/Job.md", "SomeJob"),
                is("[SomeJob](../../org/foo/Job.md)"));

        assertThat(markdownLinks.linkFor("http://foo.bar/api/org/foo/Job.html", null),
                is("[http://foo.bar/api/org/foo/Job.html](http://foo.bar/api/org/foo/Job.html)"));

    }

    @Test
    void noLinkFor() {

        MarkdownLinks markdownLinks = new MarkdownLinks();

        assertThat(markdownLinks.noLinkFor("org.foo.Job", "SomeJob"),
                is("`SomeJob org.foo.Job`"));

        assertThat(markdownLinks.noLinkFor("org.foo.Job", null),
                is("`org.foo.Job`"));
    }
}