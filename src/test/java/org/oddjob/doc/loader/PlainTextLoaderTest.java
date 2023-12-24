package org.oddjob.doc.loader;

import org.junit.jupiter.api.Test;
import org.oddjob.arooa.beandocs.element.PreformattedBlock;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.Objects;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class PlainTextLoaderTest {

    static final String LS = "\r\n";

    @Test
    void fileReadOk() throws URISyntaxException {

        Path file = Path.of(Objects.requireNonNull(getClass().getResource("SomePlainText.txt")).toURI());

        Path parent = file.getParent();
        PlainTextLoader test = PlainTextLoader.fromFile(parent);

        PreformattedBlock result = (PreformattedBlock) test.load(file.getFileName().toString());

        String expected =
                        "Remember 2 < 3 & 5 > 4" + LS +
                        "But This is a new line." + LS;

        assertThat(result.getText(), is(expected));
    }

    @Test
    void resourceReadOk() {

        PlainTextLoader test = PlainTextLoader.fromResource(getClass().getClassLoader());

        PreformattedBlock result =
                (PreformattedBlock) test.load("org/oddjob/doc/loader/SomePlainText.txt");

        String expected =
                        "Remember 2 < 3 & 5 > 4" + LS +
                        "But This is a new line." + LS;

        assertThat(result.getText(), is(expected));
    }
}