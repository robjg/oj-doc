package org.oddjob.doc.processor;

import org.hamcrest.Matchers;
import org.hamcrest.io.FileMatchers;
import org.junit.jupiter.api.Test;
import org.oddjob.OurDirs;
import org.oddjob.arooa.utils.IoUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class ProcessCopyDocsTest {

    @Test
    void processFilesOk() throws IOException {

        Path dest = OurDirs.workPathDir(getClass(), "processFilesOk");

        ProcessCopyDocs test = new ProcessCopyDocs();
        test.setToDir(dest);
        test.setFromDir(Path.of("src/test/resources/org/oddjob/doc/processor"));
        test.setPattern("*.md");
        test.setFormat(ProcessCopyDocs.Format.MD);
        test.call();

        Path result1 = dest.resolve("Sample1.md");

        assertThat(result1.toFile(),
                FileMatchers.anExistingFile());
        String text1 = IoUtils.read(Files.newInputStream(result1));

        assertThat(text1, Matchers.containsString("```xml"));
        assertThat(text1, Matchers.containsString("echo text=\"Hello\""));

        Path result2 = dest.resolve("Sample2.md");

        assertThat(result2.toFile(),
                FileMatchers.anExistingFile());

        String text2 = IoUtils.read(Files.newInputStream(result2));

        assertThat(text2, Matchers.containsString("```java"));
        assertThat(text2, Matchers.containsString("public class SomeJavaCode"));
    }

    @Test
    void findFiles() throws IOException {

        List<Path> results1 = ProcessCopyDocs.findFiles(
                Path.of("src/main/java"), "**/*.java");

        assertThat(results1.contains(Path.of("org/oddjob/doc/processor/DocPostProcessor.java")),
                is(true));

        List<Path> results2 = ProcessCopyDocs.findFiles(
                Path.of("src/main/java"), "org/oddjob/doc/processor/*.java");

        assertThat(results2.contains(Path.of("org/oddjob/doc/processor/DocPostProcessor.java")),
                is(true));
    }
}