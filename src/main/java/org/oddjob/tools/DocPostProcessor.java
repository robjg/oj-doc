package org.oddjob.tools;

import org.oddjob.arooa.beandocs.element.JavaCodeBlock;
import org.oddjob.arooa.beandocs.element.XmlBlock;
import org.oddjob.doc.doclet.CustomTagNames;
import org.oddjob.doc.html.ExceptionToHtml;
import org.oddjob.doc.html.JavaToHtml;
import org.oddjob.doc.loader.IncludeLoader;
import org.oddjob.doc.loader.PlainTextLoader;
import org.oddjob.doc.loader.JavaCodeLoader;
import org.oddjob.doc.loader.XmlLoader;
import org.oddjob.doc.html.XmlToHtml;

import javax.xml.transform.TransformerException;
import java.io.*;
import java.nio.file.Path;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class DocPostProcessor implements Runnable {

	private Path baseDir;
	
	private InputStream input;
	
	private OutputStream output;


	private ClassLoader classLoader;

	@Override
	public void run() {

		ClassLoader classLoader = Objects.requireNonNullElse(this.classLoader, getClass().getClassLoader());

		Injector[] injectors = new Injector[] {
			new JavaCodeInjector(),
			new XMLResourceInjector(classLoader),
			new XMLFileInjector(),
			new GenericInjector(CustomTagNames.TEXT_FILE_TAG, 
					PlainTextLoader.fromFile(baseDir)),
			new GenericInjector(CustomTagNames.TEXT_RESOURCE_TAG, 
							PlainTextLoader.fromResource(classLoader))
			};
		
		try {
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(input));

			PrintWriter writer = new PrintWriter(
					new OutputStreamWriter(output));

			while (true) {
				String line = reader.readLine();
				if (line == null) {
					break;
				}
				
				boolean replaced = false;
				for (Injector injector : injectors) {

					try {
						if (injector.parse(line, writer)) {
							replaced = true;
							break;
						}
					} catch (Exception e) {
						writer.println(ExceptionToHtml.toHtml(e));
						break;
					}
				}
				
				if (!replaced) {
					writer.println(line);
				}
			}

			reader.close();
			writer.close();
			
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public Path getBaseDir() {
		return baseDir;
	}

	public void setBaseDir(Path baseDir) {
		this.baseDir = baseDir;
	}

	public InputStream getInput() {
		return input;
	}

	public void setInput(InputStream input) {
		this.input = input;
	}

	public OutputStream getOutput() {
		return output;
	}

	public void setOutput(OutputStream output) {
		this.output = output;
	}
	
	interface Injector {
		
		boolean parse(String line, PrintWriter out) throws Exception;
	}
	
	class JavaCodeInjector implements Injector {	
		
		final Pattern pattern = Pattern.compile("\\{\\s*" + 
				CustomTagNames.JAVA_FILE_TAG + "\\s*(\\S+)\\s*\\}");

		private final JavaCodeLoader javaCodeLoader = JavaCodeLoader.fromFile(baseDir);


		@Override
		public boolean parse(String line, PrintWriter out) throws IOException {
			
			Matcher matcher = pattern.matcher(line);
			
			if (!matcher.find()) {
				return false;
			}

			JavaCodeBlock java = javaCodeLoader.load(matcher.group(1));

			out.println(JavaToHtml.toHtml(java));
			
			return true;
		}		
	}
	
	static class XMLResourceInjector implements Injector {

		final static Pattern pattern = Pattern.compile("\\{\\s*" +
				CustomTagNames.XML_RESOURCE_TAG + "\\s*(\\S+)\\s*\\}");

		private final XmlLoader resourceLoader;

		XMLResourceInjector(ClassLoader classLoader) {
			this.resourceLoader = XmlLoader.fromResource(classLoader);
		}

		@Override
		public boolean parse(String line, PrintWriter out) throws IOException, TransformerException {
			
			Matcher matcher = pattern.matcher(line);
			
			if (!matcher.find()) {
				return false;
			}

			XmlBlock xml = resourceLoader.load(matcher.group(1));

			out.println(XmlToHtml.toHtml(xml));
			
			return true;
		}		
	}
	
	class XMLFileInjector implements Injector {	
		
		final Pattern pattern = Pattern.compile("\\{\\s*" + 
				CustomTagNames.XML_FILE_TAG + "\\s*(\\S+)\\s*\\}");

		private final XmlLoader xmlLoader = XmlLoader.fromFile(baseDir);

		@Override
		public boolean parse(String line, PrintWriter out) throws IOException, TransformerException {
			
			Matcher matcher = pattern.matcher(line);
			
			if (!matcher.find()) {
				return false;
			}

			XmlBlock xml = xmlLoader.load(matcher.group(1));

			out.println(XmlToHtml.toHtml(xml));
			
			return true;
		}		
	}
	
	static class GenericInjector implements Injector {
		
		final Pattern pattern;
		
		private final IncludeLoader loader;
		
		public GenericInjector(String tag, IncludeLoader loader) {
			this.pattern = Pattern.compile("\\{\\s*" + 
					tag + "\\s*(\\S+)\\s*\\}");
			this.loader = loader;
		}
		
		@Override
		public boolean parse(String line, PrintWriter out) throws Exception {
			
			Matcher matcher = pattern.matcher(line);
			
			if (!matcher.find()) {
				return false;
			}
			
			out.println(loader.load(matcher.group(1)));
			
			return true;
		}		
		
	}
}
