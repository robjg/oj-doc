package org.oddjob.doc.loader;

import org.junit.Test;
import org.oddjob.doc.loader.FilterFactory;
import org.oddjob.doc.loader.PlainInputStreamToText;
import org.oddjob.doc.loader.SnippetFilter;

import static org.junit.Assert.assertEquals;

public class FilterFactoryTest {

   @Test
	public void testForSnippet() {
		
		FilterFactory test = new FilterFactory("x/y/z/abc.txt#snippet");
		
		assertEquals("x/y/z/abc.txt", test.getResourcePath());
		assertEquals(SnippetFilter.class, test.getTextLoader().getClass());
		
	}
	
    @Test
	public void testForNoneSnippet() {
		
		FilterFactory test = new FilterFactory("x/y/z/abc.txt");
		
		assertEquals("x/y/z/abc.txt", test.getResourcePath());
		assertEquals(PlainInputStreamToText.class, test.getTextLoader().getClass());
		
	}
}
