package org.oddjob.doclet;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.oddjob.arooa.convert.convertlets.FileConvertlets;
import org.oddjob.tools.BuildOddball;
import org.oddjob.tools.OurDirs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ManualDocletTest {
	
	private static final Logger logger = LoggerFactory.getLogger(ManualDocletTest.class);
	
	@Rule public TestName name = new TestName();

	public String getName() {
        return name.getMethodName();
    }

	OurDirs dirs = new OurDirs();
	
	File dest = new File(dirs.base(), "work/reference");

	File oddjobSrc = dirs.relative("../oddjob/src/java");
	
    @Before
    public void setUp() throws Exception {
		logger.info("-------------------  " + getName() + "  -------------------");

		assertThat(oddjobSrc.exists(), is(true));
		
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
		if (!dest.mkdirs()) {
			throw new RuntimeException("Failed to create dir " + dest);
		}
		
	}
	
    @Test
	public void testStart() {

		File index = new File(dest, "index.html");
		File oddjob = new File(dest, "org/oddjob/Oddjob.html");
		
		int result = com.sun.tools.javadoc.Main.execute(
				new String[] {
						"-doclet", ManualDoclet.class.getName(),
						"-sourcepath", oddjobSrc.toString(), 
						"-d", dest.toString(), 
						"-private",
						"org.oddjob"} );
		
		assertEquals(0, result);
		
		assertTrue(index.exists());
		assertTrue(oddjob.exists());
	}
	
    @Test
	public void testIstType() {

		File src = new File(dirs.base(), "build/src");
		if (!src.exists()) {
			return;
		}
		
		File index = new File(dest, "index.html");
		File is = new File(dest, "org/oddjob/arooa/types/IsType.html");
		
		int result = com.sun.tools.javadoc.Main.execute(
				new String[] {
						"-doclet", ManualDoclet.class.getName(),
						"-sourcepath", oddjobSrc.toString(), 
						"-d", dest.toString(), 
						"org.oddjob.arooa.types"} );
		
		assertEquals(0, result);
		
		assertTrue(index.exists());
		assertTrue(is.exists());
	}
	
	
    @Test
	public void testDescriptorPath() throws Throwable {

		File src = new File(dirs.base(), "build/src");
		if (!src.exists()) {
			return;
		}
		
		buildOddballs();
		
		
		File index = new File(dest, "index.html");
		File apple = new File(dest, "fruit/Apple.html");
		File is = new File(dest, "org/oddjob/arooa/types/IsType.html");
		
		String sourcePath = new FileConvertlets().filesToPath(
				new File[] { dirs.relative("src/test/oddballs/apple/src"),
						dirs.relative("src/test/oddballs/orange/src")});
		
		String descriptorPath = new FileConvertlets().filesToPath(
				new File[] { dirs.relative("src/test/oddballs/apple/classes"),
						dirs.relative("src/test/oddballs/orange/classes")});
		
		int result = com.sun.tools.javadoc.Main.execute(
				new String[] {
						"-doclet", ManualDoclet.class.getName(),
						"-sourcepath", sourcePath, 
						"-d", dest.toString(), 
						"-dp", descriptorPath, 
						"fruit"} );
		
		assertEquals(0, result);
		
		assertTrue(index.exists());
		assertTrue(apple.exists());
		assertFalse(is.exists());
	}	
    
    void buildOddballs() throws Throwable {

    	
    	buildOddball("apple");
    	buildOddball("orange");
    }
    
    void buildOddball(String oddball) throws Throwable {
    	
    	File oddballRoot = dirs.relative("src/test/oddballs/");

    	BuildOddball buildOddball = new BuildOddball();
    	buildOddball.setOddballDir(new File(oddballRoot, oddball).toString());
    	buildOddball.run();
    	
    	if (buildOddball.lastStateEvent().getState().isException()) {
    		throw buildOddball.lastStateEvent().getException();
    	}
    }
}
