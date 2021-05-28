package org.oddjob.tools.taglet;

import org.apache.commons.io.FileUtils;
import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.oddjob.OurDirs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;


public class TagletsTest {

    private static final Logger logger = LoggerFactory.getLogger(TagletsTest.class);

    @Rule
    public TestName name = new TestName();

    public String getName() {
        return name.getMethodName();
    }

    OurDirs dirs = new OurDirs();

    File dest = new File(dirs.base(), "work/javadoc");

    @Before
    public void setUp() throws Exception {

        String jdk = System.getProperty("java.vm.specification.version");

        logger.info("-------------------  " + getName() + "  " + jdk + " -------------------");

        org.junit.Assume.assumeTrue("1.8".equals(jdk));

        // try 3 times - why does this fail?
        for (int i = 0; ; ++i) {
            if (dest.exists()) {
                logger.info("Deleting " + dest);
                try {
                    FileUtils.forceDelete(dest);
                } catch (IOException e) {
                    if (i < 3) {
                        logger.error("failed deleting " + dest, e);
                        Thread.sleep(200);
                        continue;
                    } else {
                        throw e;
                    }
                }
            }
            break;
        }

        logger.info("Creating " + dest);
        if (!dest.mkdir()) {
            throw new RuntimeException("Failed to create dir " + dest);
        }

    }

    @Test
    public void testOne() {

        File index = new File(dest, "index.html");
        File oddjob = new File(dest, "org/oddjob/Oddjob.html");

        File oddjobSrc = dirs.relative("../oddjob/src/main/java");

        assertThat(oddjobSrc.exists(), is(true));

        int result = com.sun.tools.javadoc.Main.execute(
                new String[]{
                        "-sourcepath", oddjobSrc.toString(),
                        "-taglet", "org.oddjob.tools.taglet.PropertyTaglet",
                        "-taglet", "org.oddjob.tools.taglet.DescriptionTaglet",
                        "-taglet", "org.oddjob.tools.taglet.ExampleTaglet",
                        "-taglet", "org.oddjob.tools.taglet.RequiredTaglet",
                        "-tag", "see",
                        "-tag", "author",
                        "-tag", "version",
                        "-tag", "since",
                        "-d", dest.toString(),
                        "org.oddjob"});

        logger.info("Javadoc completed with status " + result);

        assertThat(index.exists(), is(true));
        assertThat(oddjob.exists(), is(true));
    }

	@Test
	public void testThatWasCausingNullPointerException() {

		File index = new File(dest, "index.html");
		File expected = new File(dest, "org/oddjob/arooa/deploy/BeanDefinition.html");

		File src = dirs.relative("../arooa/src/main/java");

		assertThat(src.exists(), CoreMatchers.is(true));

		int result = com.sun.tools.javadoc.Main.execute(
				new String[]{
						"-sourcepath", src.toString(),
						"-taglet", "org.oddjob.tools.taglet.PropertyTaglet",
						"-taglet", "org.oddjob.tools.taglet.DescriptionTaglet",
						"-taglet", "org.oddjob.tools.taglet.ExampleTaglet",
						"-taglet", "org.oddjob.tools.taglet.RequiredTaglet",
						"-tag", "see",
						"-tag", "author",
						"-tag", "version",
						"-tag", "since",
						"-d", dest.toString(),
						"org.oddjob.arooa.deploy"});

		logger.info("Javadoc completed with status " + result);

		assertThat(index.exists(), is(true));
		assertThat(expected.exists(), is(true));
	}
}
