package org.oddjob.doc.doclet;

import org.junit.Test;
import org.oddjob.doc.html.HtmlPageWriter;

import static org.junit.Assert.assertEquals;

public class HtmlPageWriterTest {

    @Test
    public void testIndexFileWithPackage() {

        String result = HtmlPageWriter.getIndexFile("com.foo.ba.HelloWorld");

        assertEquals("../../../index.html", result);
    }

    @Test
    public void testIndexFileWithSmallNames() {

        String result = HtmlPageWriter.getIndexFile("a.b.c.X");

        assertEquals("../../../index.html", result);
    }

    @Test
    public void testIndexFileNoPackage() {

        String result = HtmlPageWriter.getIndexFile("HelloWorld");

        assertEquals("index.html", result);
    }

    @Test
    public void testIndexFileNoClass() {

        String result = HtmlPageWriter.getIndexFile("");

        assertEquals("index.html", result);
    }
}
