package org.oddjob.doclet;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ManualWriterTest {

    @Test
	public void testIndexFileWithPackage() {
		
		String result = ManualWriter.getIndexFile("com.foo.ba.HelloWorld");
		
		assertEquals("../../../index.html", result);
	}

   @Test
	public void testIndexFileWithSmallNames() {
		
		String result = ManualWriter.getIndexFile("a.b.c.X");
		
		assertEquals("../../../index.html", result);
	}
	
   @Test
	public void testIndexFileNoPackage() {
		
		String result = ManualWriter.getIndexFile("HelloWorld");
		
		assertEquals("index.html", result);
	}
	
   @Test
	public void testIndexFileNoClass() {
		
		String result = ManualWriter.getIndexFile("");
		
		assertEquals("index.html", result);
	}
}
