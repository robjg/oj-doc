package org.oddjob.doc.doclet;

import org.junit.jupiter.api.Test;
import org.oddjob.Oddjob;
import org.oddjob.state.ParentState;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class ReferenceMainTest {

    @Test
    void oddjobReferenceExample() {

        Path pwd = Path.of(".");

        Path configPath = pwd.resolve("src/test/resources/examples/ReferenceMainExample.xml");

        if (!Files.exists(configPath)) {
            throw new IllegalArgumentException("Expected to run in module root, not " + pwd.toAbsolutePath());
        }

        Oddjob oddjob = new Oddjob();
        oddjob.setFile(configPath.toFile());

        oddjob.run();

        assertThat(oddjob.lastStateEvent().getState(), is(ParentState.COMPLETE));

        oddjob.destroy();
    }
}