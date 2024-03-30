package org.oddjob.doc.markdown;

import org.junit.jupiter.api.Test;
import org.oddjob.OurDirs;
import org.oddjob.doc.doclet.ReferenceMain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

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

        Path resourcePath = Paths.get("../oddjob/target/classes");

        String descriptor = "META-INF/arooa.xml";

        // Has oddjob been built?
        if (!Files.exists(includes)) {
            throw new NoSuchFileException(includes.toString());
        }

        Path descriptorResource = resourcePath.resolve(descriptor);
        // Does descriptor exist?
        if (!Files.exists(descriptorResource)) {
            throw new NoSuchFileException(descriptorResource.toString());
        }

        Path oddjobSrc= Paths.get("../oddjob/src/main/java" + File.pathSeparator + "../arooa/src/main/java");

        logger.info("Source dir is {}", oddjobSrc);

        ReferenceMain referenceMain = new ReferenceMain();
        referenceMain.setDirectory(dest.toString());
        referenceMain.setSourcepath(oddjobSrc.toString());
        referenceMain.setLoaderPath(includes.toString());
        referenceMain.setWriterFactory(MdReferenceWriterFactory.class.getName());
        referenceMain.setLinks(List.of("http://rgordon.co.uk/oddjob/1.6.0/api", "https://docs.oracle.com/en/java/javase/11/docs/api"));

        int result = referenceMain.call();

        assertThat(result, is(0));

        assertThat(Files.exists(index), is(true));
        assertThat(Files.exists(oddjob), is(true));

    }

    @Test
    void oddballMdReferenceProduced() throws IOException {

        Path dest = OurDirs.workPathDir(getClass(), "oddballReference");

        Path index = dest.resolve("README.md");
        Path job = dest.resolve("org/oddjob/maven/jobs/ResolveJob.md");

        Path includes = Paths.get("../oj-resolve/target/test-classes");

        String descriptorUrl = "file:../oj-resolve/src/main/resources/META-INF/arooa.xml";

        // Has oddjob been built?
        if (!Files.exists(includes)) {
            throw new NoSuchFileException(includes.toString());
        }

        Path oddjobSrc= Paths.get("../oj-resolve/src/main/java" );

        logger.info("Source dir is {}", oddjobSrc);

        ReferenceMain referenceMain = new ReferenceMain();
        referenceMain.setDirectory(dest.toString());
        referenceMain.setSourcepath(oddjobSrc.toString());
        referenceMain.setLoaderPath(includes.toString());
        referenceMain.setWriterFactory(MdReferenceWriterFactory.class.getName());
        referenceMain.setLinks(List.of("http://rgordon.co.uk/oddjob/1.6.0/api", "https://docs.oracle.com/en/java/javase/11/docs/api"));
        referenceMain.setDescriptorUrl(descriptorUrl);

        int result = referenceMain.call();

        assertThat(result, is(0));

        assertThat(Files.exists(index), is(true));
        assertThat(Files.exists(job), is(true));

    }
}
