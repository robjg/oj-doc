package org.oddjob.doc.markdown;

import org.junit.jupiter.api.Test;
import org.oddjob.OurDirs;
import org.oddjob.doc.doclet.ReferenceMain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

class MdReferenceWriterTest {

    private static final Logger logger = LoggerFactory.getLogger(MdReferenceWriterTest.class);

    @Test
    void oddjobMdReferenceProduced() throws IOException {

        Path dest = OurDirs.workPathDir(getClass(), "oddjobReference");

        Path index = dest.resolve("README.md");
        Path oddjob = dest.resolve("org/oddjob/Oddjob.md");

        Path includes = Paths.get("../oddjob/target/test-classes");

        // Has oddjob been built?
        if (!Files.exists(includes)) {
            throw new NoSuchFileException(includes.toString());
        }

        Path oddjobSrc= Paths.get("../oddjob/src/main/java");

        logger.info("Source dir is {}", oddjobSrc);

        ReferenceMain referenceMain = new ReferenceMain();
        referenceMain.setDirectory(dest.toString());
        referenceMain.setSourcepath(oddjobSrc.toString());
        referenceMain.setLoaderPath(includes.toString());
        referenceMain.setWriterFactory(MdReferenceWriterFactory.class.getName());
        referenceMain.setApiUrl("http://rgordon.co.uk/oddjob/1.6.0/api");

        int result = referenceMain.call();

        assertThat(result, is(0));

        assertThat(Files.exists(index), is(true));
        assertThat(Files.exists(oddjob), is(true));

    }

}
