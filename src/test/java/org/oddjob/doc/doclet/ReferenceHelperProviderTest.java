package org.oddjob.doc.doclet;

import org.junit.jupiter.api.Test;
import org.oddjob.arooa.beandocs.element.LinkElement;
import org.oddjob.doc.util.DocUtil;
import org.oddjob.doc.util.InlineTagHelper;

import java.util.function.Function;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ReferenceHelperProviderTest {

    @Test
    void relativePath() {

        Function<String, String> refLookup = mock();
        when(refLookup.apply("org.foo.Job")).thenReturn("SomeJob");

        InlineHelperProvider inlineHelperProvider = new ReferenceHelperProvider(
                refLookup,
                pathToRefRoot -> pathToRefRoot + "/../api"
        );

        String pathToRoot = DocUtil.pathToRoot("org.bar.A");

        InlineTagHelper tagHelper = inlineHelperProvider.forElement(pathToRoot);

        LinkElement refElement = new LinkElement();
        refElement.setQualifiedType("org.foo.Job");

        LinkElement codeElement = new LinkElement();
        codeElement.setQualifiedType("org.bar.Stuff");

        String refLink = tagHelper.processLink(refElement);
        assertThat(refLink, is("<a href='../../org/foo/Job.html'>SomeJob</a>"));

        String codeLink = tagHelper.processLink(codeElement);
        assertThat(codeLink, is("<code><a href='../../../api/org/bar/Stuff.html'>org.bar.Stuff</a></code>"));
    }
}