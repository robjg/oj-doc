package org.oddjob.doc.processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;

/**
 * Post Processes HTML docs looking for Tags to replace with imported code and configuration.
 * Used to inject examples into the User and Development Guides. Docs are processed in place, as
 * Ant has already copied them.
 * 
 * @author rob
 *
 */
public class PostProcessDocs implements Runnable {
	private static final Logger logger = 
		LoggerFactory.getLogger(PostProcessDocs.class);

	/** The name of this task. */
	private String name;


	private File[] files;

	/** The base directory for finding include files. */
	private Path baseDir;

	@Override
	public void run() {

		DocPostProcessor processor = DocPostProcessor.of(baseDir);

		for (File file : files)  {
			
			logger.info("Processing " + file);
			
			File tmp = new File(file + ".tmp");

			try {

				processor.process(new FileInputStream(file), new FileOutputStream(tmp));
			} catch (IOException e) {
				throw new RuntimeException(e);
			}

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

	public void setBaseDir(Path baseDir) {
		this.baseDir = baseDir;
	}

	@Override
	public String toString() {
		return Objects.requireNonNullElseGet(name, () -> getClass().getSimpleName());
	}
}
