package org.oddjob.tools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Objects;

/**
 * Processes reference and user guide to load the examples from file.
 * 
 * @author rob
 *
 */
public class PostProcessDocs implements Runnable {
	private static final Logger logger = 
		LoggerFactory.getLogger(PostProcessDocs.class);

	private String name;

	private File[] files;

	private File baseDir;

	@Override
	public void run() {

		DocPostProcessor processor = new DocPostProcessor();
		processor.setBaseDir(baseDir);

		for (File file : files)  {
			
			logger.info("Processing " + file);
			
			File tmp = new File(file + ".tmp");

			try {
				processor.setInput(new FileInputStream(file));
				processor.setOutput(new FileOutputStream(tmp));
			} catch (FileNotFoundException e) {
				throw new RuntimeException(e);
			}

			processor.run();

			logger.info("Processed {}", file);
			
			if (!file.delete()) {
				logger.warn("Failed to delete {}", file);
			}
			if (!tmp.renameTo(file)) {
				logger.warn("Failed to rename {}", tmp);
			}
		}
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setFiles(File[] files) {
		this.files = files;
	}

	public void setBaseDir(File baseDir) {
		this.baseDir = baseDir;
	}

	@Override
	public String toString() {
		return Objects.requireNonNullElseGet(name, () -> getClass().getSimpleName());
	}
}
