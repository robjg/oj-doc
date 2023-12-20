package org.oddjob.doc.html;

import org.oddjob.arooa.beandocs.element.XmlBlock;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Objects;

/**
 * Creates XML that can be inserted into JavaDoc or another XML document from
 * an XML class path resource.
 * <p>
 * The style-sheet used is courtesy of:
 * <a href="http://lenzconsulting.com/xml-to-string/">http://lenzconsulting.com/xml-to-string/</a>
 * </p>
 *
 * @author rob
 */
public class XmlToHtml {

    public static String toHtml(XmlBlock xmlBlock) throws TransformerException {

        String xml = xmlBlock.getXml();

        InputStream stylesheet = Objects.requireNonNull(
                XmlToHtml.class.getResourceAsStream("xml-2-string.xsl"),
                "No Stylesheet");

        ByteArrayOutputStream result = new ByteArrayOutputStream();

        Transformer transformer = TransformerFactory.newInstance()
                .newTransformer(new StreamSource(stylesheet));

        transformer.transform(
                new StreamSource(new ByteArrayInputStream(xml.getBytes())),
                new StreamResult(result));

        return "<pre class=\"xml\">\n" +
                result + "\n" +
                "</pre>\n";
    }
}
