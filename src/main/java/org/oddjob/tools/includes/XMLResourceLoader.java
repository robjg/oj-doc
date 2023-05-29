package org.oddjob.tools.includes;

import org.oddjob.doc.doclet.CustomTagNames;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Creates XML that can be inserted into JavaDoc or another XML document from
 * an XML class path resource.
 *
 * The style-sheet used is courtesy of:
 * <a href="http://lenzconsulting.com/xml-to-string/">http://lenzconsulting.com/xml-to-string/</a>
 *
 * @author rob
 *
 */
public class XMLResourceLoader implements IncludeLoader, CustomTagNames {

	@Override
	public boolean canLoad(String tag) {
		return XML_RESOURCE_TAG.equals(tag);
	}
	
	@Override
	public String load(String resource) {
		return loadXml(resource);
	}

	public static String loadXml(String resource) {
		try {
			FilterFactory filterFactory = new FilterFactory(resource);

			String resourcePath = filterFactory.getResourcePath();

			InputStream input = XMLResourceLoader.class.getClassLoader().getResourceAsStream(resourcePath);
			if (input == null) {
				throw new IOException("No resource " + resourcePath);
			}

			String xml = filterFactory.getTextLoader().load(input);
			
			InputStream stylesheet = 
				XMLResourceLoader.class.getResourceAsStream("xml-2-string.xsl");
			
			ByteArrayOutputStream result = new ByteArrayOutputStream();

			Transformer transformer = TransformerFactory.newInstance()
					.newTransformer(new StreamSource(stylesheet));

			transformer.transform(
					new StreamSource(new ByteArrayInputStream(xml.getBytes())),
					new StreamResult(result));
			
			return "<pre class=\"xml\">" + EOL +
					result +
				"</pre>" + EOL;
		}
		catch (Exception e) {
			return "<p><em>" + e + "</em></p>" + EOL;
		}
	}
}
