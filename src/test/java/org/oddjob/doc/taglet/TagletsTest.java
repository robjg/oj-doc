package org.oddjob.doc.taglet;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.oddjob.OurDirs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.spi.ToolProvider;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;


public class TagletsTest {

    private static final Logger logger = LoggerFactory.getLogger(TagletsTest.class);

    @Rule
    public TestName name = new TestName();

    public String getName() {
        return name.getMethodName();
    }

    @Before
    public void setUp() {

        logger.info("-------------------  " + getName() + " -------------------");
    }

    @Test
    public void whenCustomTagletUsedThenWhatHappens() throws IOException {

        Path dest = OurDirs.workPathDir(TagletsTest.class, "whenCustom");

        Path index = dest.resolve("index.html");
        Path oddjob = dest.resolve("foo/sample/SampleOne.html");

        Path srcDir = OurDirs.relativePath("src/test/java");

        assertThat(Files.exists(srcDir), is(true));

        ToolProvider toolProvider = ToolProvider.findFirst("javadoc")
                .orElseThrow(() -> new IllegalArgumentException("No JavaDco"));
        int result = toolProvider.run(System.out, System.err,
                "-sourcepath", srcDir.toString(),
                "-d", dest.toString(),
                "-tag", "some.foo",
                "foo.sample");

        assertThat(result, is(0));

        assertThat(Files.exists(index), is(true));
        assertThat(Files.exists(oddjob), is(true));
    }

    @Test
    public void testOddjobTags() throws IOException {

        Path dest = OurDirs.workPathDir(TagletsTest.class, "oddjobTags");

        Path index = dest.resolve("index.html");
        Path oddjob = dest.resolve("org/oddjob/Oddjob.html");

        Path oddjobSrc = OurDirs.relativePath("../oddjob/src/main/java");

        assertThat(Files.exists(oddjobSrc), is(true));

        ToolProvider toolProvider = ToolProvider.findFirst("javadoc")
                .orElseThrow(() -> new IllegalArgumentException("No JavaDco"));
        int result = toolProvider.run(System.out, System.err,
                "-Xdoclint:none",
                "-sourcepath", oddjobSrc.toString(),
                "-tag", "oddjob.example:X",
                "-tag", "oddjob.property:fm:Reference Property:",
                "-tag", "oddjob.description:a:Reference Description:",
                "-tag", "oddjob.required:fm:Required:",
                "-d", dest.toString(),
                "org.oddjob");

        logger.info("Javadoc completed with status " + result);

        assertThat(result, is(0));

        assertThat(Files.exists(index), is(true));
        assertThat(Files.exists(oddjob), is(true));
    }

    @Test
    public void testOne() throws IOException {

        Path dest = OurDirs.workPathDir(TagletsTest.class, "testOne");

        Path index = dest.resolve("index.html");
        Path oddjob = dest.resolve("org/oddjob/Oddjob.html");

        Path oddjobSrc = OurDirs.relativePath("../oddjob/src/main/java");

        assertThat(Files.exists(oddjobSrc), is(true));

        String propertyTaglet = PropertyTaglet.class.getName();
        String descriptionTaglet = DescriptionTaglet.class.getName();
        String exampleTaglet = ExampleTaglet.class.getName();
        String requiredTaglet = RequiredTaglet.class.getName();


        ToolProvider toolProvider = ToolProvider.findFirst("javadoc")
                .orElseThrow(() -> new IllegalArgumentException("No JavaDoc"));
        int result = toolProvider.run(System.out, System.err,
                "-Xdoclint:none",
                "-sourcepath", oddjobSrc.toString(),
                "-taglet", propertyTaglet,
                "-taglet", descriptionTaglet,
                "-taglet", exampleTaglet,
                "-taglet", requiredTaglet,
                "-tag", "oddjob.xml.resource",
                "-tag", "see",
                "-tag", "author",
                "-tag", "version",
                "-tag", "since",
                "-d", dest.toString(),
                "org.oddjob");

        logger.info("Javadoc completed with status " + result);

        assertThat(result, is(0));

        assertThat(Files.exists(index), is(true));
        assertThat(Files.exists(oddjob), is(true));
    }

    @Test
    public void whatWasCausingNullPointerException() throws IOException {

        Path dest = OurDirs.workPathDir(TagletsTest.class,
                "whatWasCausingNullPointerException");

        Path index = dest.resolve("index.html");
        Path expected = dest.resolve("org/oddjob/arooa/deploy/BeanDefinition.html");

        Path src = OurDirs.relativePath("../arooa/src/main/java");
        Path arooaIncludes = OurDirs.relativePath("../arooa/target/test-classes");
        Path oddjobIncludes = OurDirs.relativePath("../oddjob/target/test-classes");

        assertThat(Files.exists(src), is(true));

        String propertyTaglet = PropertyTaglet.class.getName();
        String descriptionTaglet = DescriptionTaglet.class.getName();
        String exampleTaglet = ExampleTaglet.class.getName();
        String requiredTaglet = RequiredTaglet.class.getName();

        ToolProvider toolProvider = ToolProvider.findFirst("javadoc")
                .orElseThrow(() -> new IllegalArgumentException("No JavaDco"));

        ClassLoader existing = Thread.currentThread().getContextClassLoader();
        try (URLClassLoader classLoader =  URLClassLoader.newInstance(
                new URL[] { arooaIncludes.toUri().toURL(), oddjobIncludes.toUri().toURL() })) {

            Thread.currentThread().setContextClassLoader(classLoader);

            int result = toolProvider.run(System.out, System.err,
                    "-Xdoclint:none",
                    "-sourcepath", src.toString(),
                    "-taglet", propertyTaglet,
                    "-taglet", descriptionTaglet,
                    "-taglet", exampleTaglet,
                    "-taglet", requiredTaglet,
                    "-tag", "oddjob.xml.resource",
                    "-d", dest.toString(),
                    "org.oddjob.arooa.deploy");

            logger.info("Javadoc completed with status " + result);
            assertThat(result, is(0));
        }
        finally {
            Thread.currentThread().setContextClassLoader(existing);
        }

        assertThat(Files.exists(index), is(true));
        assertThat(Files.exists(expected), is(true));
    }
}
