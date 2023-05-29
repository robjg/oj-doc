package org.oddjob.doc.doclet;

import org.oddjob.OurDirs;
import org.oddjob.tools.BuildOddball;

import java.io.File;

public class ManualDocletBasicTest {

	OurDirs dirs = new OurDirs();
	
//    @Test
//	public void testJobsAndTypes() throws Throwable {
//
//    	buildOddballs();
//
//		String descriptorPath = new FileConvertlets().filesToPath(
//				new File[] { dirs.relative("src/test/oddballs/apple/classes"),
//						dirs.relative("src/test/oddballs/orange/classes")});
//
//		ManualDoclet test = new ManualDoclet(descriptorPath, null);
//
//		JobsAndTypes jats = test.jobsAndTypes();
//
//		List<String> types = new ArrayList<String>();
//		for (String type : jats.types()) {
//			types.add(type);
//		}
//
//		assertEquals(2, types.size());
//
//		assertTrue(types.contains("fruit:colour"));
//		assertTrue(types.contains("fruit:flavour"));
//
//		List<String> jobs = new ArrayList<String>();
//		for (String type : jats.jobs()) {
//			jobs.add(type);
//		}
//
//		assertEquals(2, jobs.size());
//
//		assertTrue(jobs.contains("fruit:apple"));
//		assertTrue(jobs.contains("fruit:orange"));
//	}
    
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
