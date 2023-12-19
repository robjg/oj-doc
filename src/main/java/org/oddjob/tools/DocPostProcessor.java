package org.oddjob.tools;

import org.oddjob.doc.doclet.CustomTagNames;
import org.oddjob.tools.includes.*;

import java.io.*;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class DocPostProcessor implements Runnable {

	private File baseDir;
	
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
					new PlainTextFileLoader(baseDir)),
			new GenericInjector(CustomTagNames.TEXT_RESOURCE_TAG, 
							new PlainTextResourceLoader())
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
					if (injector.parse(line, writer)) {
						replaced = true;
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

	public File getBaseDir() {
		return baseDir;
	}

	public void setBaseDir(File baseDir) {
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
		
		public boolean parse(String line, PrintWriter out);
	}
	
	class JavaCodeInjector implements Injector {	
		
		final Pattern pattern = Pattern.compile("\\{\\s*" + 
				JavaCodeFileLoader.JAVA_FILE_TAG + "\\s*(\\S+)\\s*\\}");
		
		@Override
		public boolean parse(String line, PrintWriter out) {
			
			Matcher matcher = pattern.matcher(line);
			
			if (!matcher.find()) {
				return false;
			}
			
			out.println(new JavaCodeFileLoader(baseDir).load(matcher.group(1)));
			
			return true;
		}		
	}
	
	static class XMLResourceInjector implements Injector {

		final static Pattern pattern = Pattern.compile("\\{\\s*" +
				XMLResourceLoader.XML_RESOURCE_TAG + "\\s*(\\S+)\\s*\\}");

		private final XMLResourceLoader resourceLoader;

		XMLResourceInjector(ClassLoader classLoader) {
			this.resourceLoader = new XMLResourceLoader(classLoader);
		}

		@Override
		public boolean parse(String line, PrintWriter out) {
			
			Matcher matcher = pattern.matcher(line);
			
			if (!matcher.find()) {
				return false;
			}
			
			out.println(resourceLoader.load(matcher.group(1)));
			
			return true;
		}		
	}
	
	class XMLFileInjector implements Injector {	
		
		final Pattern pattern = Pattern.compile("\\{\\s*" + 
				XMLFileLoader.XML_FILE_TAG + "\\s*(\\S+)\\s*\\}");
		
		@Override
		public boolean parse(String line, PrintWriter out) {
			
			Matcher matcher = pattern.matcher(line);
			
			if (!matcher.find()) {
				return false;
			}
			
			out.println(new XMLFileLoader(baseDir).load(matcher.group(1)));
			
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
		public boolean parse(String line, PrintWriter out) {
			
			Matcher matcher = pattern.matcher(line);
			
			if (!matcher.find()) {
				return false;
			}
			
			out.println(loader.load(matcher.group(1)));
			
			return true;
		}		
		
	}
}
