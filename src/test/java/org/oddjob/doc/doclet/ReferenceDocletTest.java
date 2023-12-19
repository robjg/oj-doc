package org.oddjob.doc.doclet;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.oddjob.OurDirs;
import org.oddjob.arooa.convert.convertlets.FileConvertlets;
import org.oddjob.arooa.utils.FileUtils;
import org.oddjob.tools.BuildOddball;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
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
	
	Path dest;

	File oddjobSrc = dirs.relative("../oddjob/src/main/java");
	
    @Before
    public void setUp() throws Exception {

		logger.info("-------------------  " + getName() + "  -------------------");

		assertThat(oddjobSrc.exists(), is(true));

		dest = Paths.get("target/work/", getClass().getSimpleName(), getName());
		if (Files.exists(dest)) {
			FileUtils.deleteDirectory(dest);
		}
		logger.info("Creating {}", dest);
		Files.createDirectories(dest);
	}
	
    @Test
	public void whenReferenceRunOnOddjobSourceThenOddjobCreated() throws NoSuchFileException  {

		Path index = dest.resolve("index.html");
		Path oddjob = dest.resolve("org/oddjob/Oddjob.html");

		Path includes = Paths.get("../oddjob/target/test-classes");

		// Has oddjob been built?
		if (!Files.exists(includes)) {
			throw new NoSuchFileException(includes.toString());
		}

		logger.info("Source dir is {}", oddjobSrc);

		int result = ReferenceMain.mainCall(
				"-sourcepath", oddjobSrc.toString(),
				"-d", dest.toString(),
				"-xcp", includes.toString());

		assertThat(result, is(0));

		assertThat(Files.exists(index), is(true));
		assertThat(Files.exists(oddjob), is(true));
	}
	
    @Test
	public void testIsType() throws NoSuchFileException {

		Path arooaSrc = Paths.get("../arooa/src/main/java");
		
		Path index = dest.resolve("index.html");
		Path is = dest.resolve("org/oddjob/arooa/types/IsType.html");

		Path arooaIncludes = Paths.get("../arooa/target/test-classes");
		Path oddjobIncludes = Paths.get("../oddjob/target/test-classes");

		// Has oddjob been built?
		if (!Files.exists(arooaIncludes)) {
			throw new NoSuchFileException(arooaIncludes.toString());
		}

		logger.info("Source dir is {}", arooaSrc);

		int result = ReferenceMain.mainCall(
				"-sourcepath", arooaSrc.toString(),
				"-d", dest.toString(),
				"-xcp", arooaIncludes + File.pathSeparator + oddjobIncludes,
				"org.oddjob.arooa.types");

		assertThat(result, is(0));

		assertThat(Files.exists(index), is(true));
		assertThat(Files.exists(is), is(true));
	}
	
	
    @Test
	public void testDescriptorPath() throws Throwable {

		File src = new File(dirs.base(), "build/src");
		if (!src.exists()) {
			return;
		}
		
		buildOddballs();

		Path index = dest.resolve("index.html");
		Path apple = dest.resolve("fruit/Apple.html");
		Path is = dest.resolve("org/oddjob/arooa/types/IsType.html");
		
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

		assertThat(Files.exists(index), is(true));
		assertThat(Files.exists(apple), is(true));
		assertThat(Files.exists(is), is(false));
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
