package org.oddjob.doc.markdown;

import org.junit.jupiter.api.Test;
import org.oddjob.arooa.beandocs.element.*;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MdVisitorTest {

    @Test
    void removeNewLines() {

        assertThat(MdVisitor.removeNewLines("\nStuff\r\n\tMore Stuff\r\nAnd More"),
                is("Stuff More Stuff And More"));
    }

    @Test
    void whenLfAndNotRemoveLfThenSpacesRemovedOk() {

        MdContext context = mock(MdContext.class);

        List<BeanDocElement> elements = List.of(
                StandardElement.of(
                        "  The first line\n Another line \r\n\tAnd another.")
        );

        String result = MdVisitor.visitAll(elements, context);

        assertThat(result, is("  The first line\nAnother line\nAnd another."));
    }

    @Test
    void whenLinkThenSpacesNotRemoved() {

        MdContext context = mock(MdContext.class);
        when(context.processLink(any(LinkElement.class))).thenReturn("SOME LINK");

        List<BeanDocElement> elements = List.of(
                new LinkElement(),
                StandardElement.of(" then some text.")
        );

        String result = MdVisitor.visitAll(elements, context);

        assertThat(result, is("SOME LINK then some text."));
    }

    @Test
    void whenLfAndRemoveLfThenReplacedWithSpacesOk() {

        MdContext context = mock(MdContext.class);

        StandardElement standardElement = StandardElement.of(
                "  The first line\n Another line \n\r\tAnd another.");

        List<BeanDocElement> elements = List.of(standardElement);

        String result = MdVisitor.visitAll(elements, context, true);

        assertThat(result, is("The first line Another line And another."));
    }

    @Test
    void whenParagraphTagThenHandledOk() {

        MdContext context = mock(MdContext.class);

        List<BeanDocElement> elements = List.of(
                StartHtmlElement.of("p", "<p>"),
                StandardElement.of("\nSome text.\n"),
                StartHtmlElement.of("p", "<p>"),
                StandardElement.of(" and "),
                StartHtmlElement.of("b", "<b>"),
                StandardElement.of("bold"),
                StartHtmlElement.of("b", "</b>"),
                StandardElement.of(" normal."),
                EndHtmlElement.of("p", "<p>"),
                StandardElement.of("And more.")
                );

        String result = MdVisitor.visitAll(elements, context);

        assertThat(result, is("\n\nSome text.\n\n and <b>bold</b> normal.\nAnd more."));
    }

    @Test
    void whenUnorderedListTagThenHandledOk() {

        MdContext context = mock(MdContext.class);

        List<BeanDocElement> elements = List.of(
                StandardElement.of("\nSome text.\n"),
                StartHtmlElement.of("ul", "<ignored>"),
                StandardElement.of("\r\n"),
                StartHtmlElement.of("li", "<ignored>"),
                StandardElement.of("An item."),
                EndHtmlElement.of("li", "<ignored>"),
                StandardElement.of("\r\n"),
                StartHtmlElement.of("li", "<ignored>"),
                StandardElement.of("Another item."),
                EndHtmlElement.of("li", "<ignored>"),
                StandardElement.of("\r\n"),
                EndHtmlElement.of("ul", "<ignored>"),
                StandardElement.of("\r\n"),
                StandardElement.of("more.")
        );

        String result = MdVisitor.visitAll(elements, context);

        assertThat(result, is(
                "\nSome text.\n\n" +
                "- An item.\n" +
                "- Another item.\n" +
                "\r\n" +
                "more."));
    }
}