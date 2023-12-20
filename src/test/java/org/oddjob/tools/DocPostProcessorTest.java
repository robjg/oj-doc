package org.oddjob.tools;

import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.oddjob.io.BufferType;
import org.oddjob.util.IO;
import org.xmlunit.matchers.CompareMatcher;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.*;

public class DocPostProcessorTest {

	String EOL = System.lineSeparator();
	
    @Test
	public void testJavaFilePattern() {
		
		Pattern test = new DocPostProcessor().new JavaCodeInjector().pattern;
		
		Matcher matcher = test.matcher("bla bla {@oddjob.java.file " +
				"test/java/org/oddjob/tools/SomeJavaCode.java}--foo");
		assertTrue(matcher.find());
		
		assertEquals("test/java/org/oddjob/tools/SomeJavaCode.java", 
				matcher.group(1));
	}
	
    @Test
	public void testXmlResourcePattern() {
		
		Pattern test = DocPostProcessor.XMLResourceInjector.pattern;
		
		Matcher matcher = test.matcher("bla bla {@oddjob.xml.resource " +
				"org/oddjob/tools/SomeXML.xml}--foo");
		assertTrue(matcher.find());
		
		assertEquals("org/oddjob/tools/SomeXML.xml", 
				matcher.group(1));
	}
	
    @Test
	public void testInsertFile() throws IOException {
		
		DocPostProcessor test = new DocPostProcessor();
		
		test.setBaseDir(Path.of("."));
		
		String input = 
			"<body>\n" + EOL +
			"<h1>Some Java</h1>" + EOL +
			"     {@oddjob.java.file src/test/java/org/oddjob/tools/SomeJavaCode.java}" + EOL +
			"<h1>Some XML</h1>" + EOL +
			"     {@oddjob.xml.resource org/oddjob/tools/SomeXML.xml}" + EOL +
			"</body>" + EOL;
		
		test.setInput(new ByteArrayInputStream(input.getBytes()));
		
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		
		test.setOutput(output);
	
		test.run();
		
		BufferType buffer = new BufferType();
		buffer.configured();
		
		
		InputStream expected = getClass().getResourceAsStream("DocPostProcessorExpected.html");
		
		assertNotNull(expected);
		
		IO.copy(expected, 
				buffer.toOutputStream());
		
		String result = output.toString();
		
		System.out.println(result);
		
		MatcherAssert.assertThat(result,
				CompareMatcher.isSimilarTo(buffer.getText())
						.normalizeWhitespace());

	}
}
