package org.oddjob.doc.doclet;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.oddjob.OurDirs;
import org.oddjob.arooa.convert.convertlets.FileConvertlets;
import org.oddjob.tools.BuildOddball;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.spi.ToolProvider;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class ReferenceDocletTest {
	
	private static final Logger logger = LoggerFactory.getLogger(ReferenceDocletTest.class);
	
	@Rule public TestName name = new TestName();

	public String getName() {
        return name.getMethodName();
    }

	OurDirs dirs = new OurDirs();
	
	File dest = new File(dirs.base(), "work/reference");

	File oddjobSrc = dirs.relative("../oddjob/src/main/java");
	
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

		logger.info("Source dir is {}", oddjobSrc);

		ToolProvider toolProvider = ToolProvider.findFirst("javadoc")
				.orElseThrow(() -> new IllegalArgumentException("No JavaDco"));
		int result = toolProvider.run(System.out, System.err,
						"-doclet", ReferenceDoclet.class.getName(),
						"-sourcepath", oddjobSrc.toString(), 
						"-d", dest.toString(), 
						"-private",
						"org.oddjob");
		
		assertThat(result, is(0));
		
		assertThat(index.exists(), is(true));
		assertThat(oddjob.exists(), is(true));
	}
	
    @Test
	public void testIstType() {

		File arooaSrc = dirs.relative("../arooa/src/main/java");
		
		File index = new File(dest, "index.html");
		File is = new File(dest, "org/oddjob/arooa/types/IsType.html");

		logger.info("Source dir is {}", arooaSrc);

		ToolProvider toolProvider = ToolProvider.findFirst("javadoc")
				.orElseThrow(() -> new IllegalArgumentException("No JavaDco"));
		int result = toolProvider.run(System.out, System.err,
						"-doclet", ReferenceDoclet.class.getName(),
						"-sourcepath", arooaSrc.toString(),
						"-d", dest.toString(), 
						"org.oddjob.arooa.types");

		assertThat(result, is(0));

		assertThat(index.exists(), is(true));
		assertThat(is.exists(), is(true));
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
				dirs.relative("src/test/oddballs/apple/src"),
				dirs.relative("src/test/oddballs/orange/src"));

		String descriptorPath = new FileConvertlets().filesToPath(
				dirs.relative("src/test/oddballs/apple/classes"),
				dirs.relative("src/test/oddballs/orange/classes"));

		logger.info("Descriptor path is {}", descriptorPath);

		ToolProvider toolProvider = ToolProvider.findFirst("javadoc")
				.orElseThrow(() -> new IllegalArgumentException("No JavaDco"));
		int result = toolProvider.run(System.out, System.err,
						"-doclet", ReferenceDoclet.class.getName(),
						"-sourcepath", sourcePath,
						"-dp", descriptorPath,
						"-d", dest.toString(), 
						"fruit");

		assertThat(result, is(0));

		assertThat(index.exists(), is(true));
		assertThat(apple.exists(), is(true));
		assertThat(is.exists(), is(false));
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
