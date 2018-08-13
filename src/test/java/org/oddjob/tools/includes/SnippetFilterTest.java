package org.oddjob.tools.includes;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;
import org.oddjob.tools.OurDirs;

public class SnippetFilterTest {
	
	 @Test
	// #our-snippet {
	public void testFilter() throws IOException {
		// } #our-snippet
		OurDirs dirs = new OurDirs();
		
		File file = new File(dirs.base(), 
				"src/test/java/org/oddjob/tools/includes/SnippetFilterTest.java");
		
		InputStream input = new FileInputStream(file);
		
		SnippetFilter test = new SnippetFilter("our-snippet");
		
		String result = test.load(input);
		
		assertEquals("public void testFilter() throws IOException {",
				result.trim());
	}

}