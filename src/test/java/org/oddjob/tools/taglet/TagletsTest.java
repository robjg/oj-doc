package org.oddjob.tools.taglet;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.oddjob.tools.OurDirs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TagletsTest {
	
	private static final Logger logger = LoggerFactory.getLogger(TagletsTest.class);
	
	@Rule public TestName name = new TestName();

	public String getName() {
        return name.getMethodName();
    }

	OurDirs dirs = new OurDirs();
	
	File dest = new File(dirs.base(), "work/javadoc");
	
    @Before
    public void setUp() throws Exception {
		logger.info("-------------------  " + getName() + "  -------------------");
		
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
					}
					else {
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
		
		assertThat(oddjobSrc.exists(), CoreMatchers.is(true));
		
		int result = com.sun.tools.javadoc.Main.execute(
				new String[] {
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
						"org.oddjob"} );
		
		logger.info("Javadoc completed with status " + result);
		
		assertTrue(index.exists());
		assertTrue(oddjob.exists());
	}
	
}
