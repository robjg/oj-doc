package org.oddjob.doc.html;

import org.junit.jupiter.api.Test;
import org.oddjob.arooa.beandocs.element.PreformattedBlock;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class PlainTextToHtmlTest {

    @Test
    void testHtml() {

        PreformattedBlock preformattedBlock = new PreformattedBlock();
        preformattedBlock.setText("Foo");

        String result = PlainTextToHtml.toHtml(preformattedBlock);

        String expected = "<pre>\nFoo</pre>\n";

        assertThat(result, is(expected));
    }
}