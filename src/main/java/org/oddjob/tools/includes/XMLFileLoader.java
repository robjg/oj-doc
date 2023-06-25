package org.oddjob.tools.includes;

import org.oddjob.doc.doclet.CustomTagNames;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.util.Objects;

/**
 * Creates XML that can be inserted into JavaDoc or another XML document from
 * an XML file.
 * 
 * The style-sheet used is courtesy of:
 * <a href="http://lenzconsulting.com/xml-to-string/">lenzconsulting.com</a>
 * 
 * @author rob
 *
 */
public class XMLFileLoader implements IncludeLoader, CustomTagNames {

	private static final Logger logger = LoggerFactory.getLogger(XMLFileLoader.class);
	
	private final File base;
	
	public XMLFileLoader(File base) {
		this.base = base;
	}
	
	@Override
	public boolean canLoad(String tag) {
		return XML_FILE_TAG.equals(tag);
	}
	
	@Override
	public String load(String fileName) {
		
		try {
			FilterFactory filterFactory = new FilterFactory(fileName);
						
			File file = new File(base, filterFactory.getResourcePath());
			
			InputStream input = new FileInputStream(file);
			
			String xml = filterFactory.getTextLoader().load(input);

			InputStream stylesheet = Objects.requireNonNull(
					XMLResourceLoader.class.getResourceAsStream("xml-2-string.xsl"),
					"No Stylesheet");

			ByteArrayOutputStream result = new ByteArrayOutputStream();

			Transformer transformer = TransformerFactory.newInstance()
					.newTransformer(new StreamSource(stylesheet));

			logger.info("Processing XML of length {}, from ", xml.length(), file);

			transformer.transform(
					new StreamSource(new ByteArrayInputStream(xml.getBytes())),
					new StreamResult(result));
			
			return "<pre class=\"xml\">" + EOL + result +
				"</pre>" + EOL;
		}
		catch (Exception e) {
			logger.error("Failed processing {}", fileName, e);
			return "<p><em>" + e + "</em></p>" + EOL;
		}
	}
}
