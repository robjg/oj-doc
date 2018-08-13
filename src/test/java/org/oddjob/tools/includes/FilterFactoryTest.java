package org.oddjob.tools.includes;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

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
		assertEquals(PlainStreamToText.class, test.getTextLoader().getClass());
		
	}
}
