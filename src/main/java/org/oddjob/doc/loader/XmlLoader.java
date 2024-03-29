package org.oddjob.doc.loader;

import org.oddjob.arooa.beandocs.element.BeanDocElement;
import org.oddjob.arooa.beandocs.element.ExceptionElement;
import org.oddjob.arooa.beandocs.element.XmlBlock;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Creates XML that can be inserted into JavaDoc or another XML document from
 * an XML file.
 * <p>
 * The style-sheet used is courtesy of:
 * <a href="http://lenzconsulting.com/xml-to-string/">lenzconsulting.com</a>
 * </p>
 *
 * @author rob
 *
 */
public class XmlLoader extends AbstractLoader {


	private XmlLoader(Loader loader) {
		super(loader);
	}

	public static XmlLoader fromFile(Path base) {
		return new XmlLoader(new FromFile(base));
	}

	public static XmlLoader fromResource(ClassLoader classLoader) {
		return new XmlLoader(new FromResource(classLoader));
	}

	@Override
	public BeanDocElement load(String fileName) {

		try {
			String contents = doLoad(fileName);

			XmlBlock xmlBlock = new XmlBlock();
			xmlBlock.setXml(contents);

			return xmlBlock;

		} catch (IOException e) {
			return ExceptionElement.of(e);
		}
	}
}
