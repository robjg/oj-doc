package org.oddjob.doc.doclet;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ReferenceHtmlPageWriterTest {

    @Test
    public void testIndexFileWithPackage() {

        String result = ReferenceHtmlPageWriter.getIndexFile("com.foo.ba.HelloWorld");

        assertEquals("../../../index.html", result);
    }

    @Test
    public void testIndexFileWithSmallNames() {

        String result = ReferenceHtmlPageWriter.getIndexFile("a.b.c.X");

        assertEquals("../../../index.html", result);
    }

    @Test
    public void testIndexFileNoPackage() {

        String result = ReferenceHtmlPageWriter.getIndexFile("HelloWorld");

        assertEquals("index.html", result);
    }

    @Test
    public void testIndexFileNoClass() {

        String result = ReferenceHtmlPageWriter.getIndexFile("");

        assertEquals("index.html", result);
    }
}
