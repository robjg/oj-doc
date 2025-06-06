package org.oddjob.doc.processor;

import org.oddjob.doc.html.HtmlContext;
import org.oddjob.doc.markdown.MdContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;

/**
 * Post Processes docs looking for Tags to replace with imported code and configuration.
 *
 * @author rob
 */
public class ProcessCopyDocs implements Callable<Integer> {
    private static final Logger logger =
            LoggerFactory.getLogger(ProcessCopyDocs.class);

    /**
     * The name of this task.
     */
    private String name;

	/**
	 * The directory of the source files
	 */
    private Path fromDir;

	/**
	 * The directory to write the processed files to.
	 */
	private Path toDir;

	/**
	 * The file pattern to match, in glob format. Must be provided.
	 */
    private String pattern;

    /**
     * The base directory for finding include files.
     */
    private Path baseDir;

	/**
	 * The format. MD (Markdown) or HTML (the default).
	 */
	private Format format;

	public enum Format {

		HTML() {
			@Override
			void process(DocPostProcessor processor, InputStream input, OutputStream output) throws IOException {
				processor.process(input, output, HtmlContext.noLinks());
			}
		},

		MD() {
			@Override
			void process(DocPostProcessor processor, InputStream input, OutputStream output) throws IOException {
				processor.process(input, output, MdContext.noLinks());
			}
		}

		;

		abstract void process(DocPostProcessor processor,
							  InputStream input,
							  OutputStream output) throws IOException;
	}

    @Override
    public Integer call() throws IOException {

		Path fromDir = Objects.requireNonNull(this.fromDir, "No From Dir");
		Path toDir = Objects.requireNonNull(this.toDir, "No To Dir");
		String pattern = Objects.requireNonNull(this.pattern, "No Pattern");

		Path baseDir = Objects.requireNonNullElse(this.baseDir, Path.of("."));

		Format format = Objects.requireNonNullElse(this.format, Format.HTML);

		logger.info("Setting include base dir to {}", baseDir);

        DocPostProcessor processor = DocPostProcessor.of(baseDir);

		List<Path> files = findFiles(fromDir, pattern);

		if (files.isEmpty()) {
			logger.info("No files found to process.");
		}

        for (Path file : files) {

			Path pathIn = fromDir.resolve(file);

			Path pathOut = toDir.resolve(file);
			Path dirOut = pathOut.getParent();
			if (!Files.exists(dirOut)) {
				logger.info("Creating output directory");
				Files.createDirectories(dirOut);
			}

            logger.info("Processing {} to {}", pathIn, pathOut);

			format.process(processor, Files.newInputStream(pathIn),
					Files.newOutputStream(pathOut));

            logger.info("Processed {}", pathOut);
        }

		return  0;
    }


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Path getFromDir() {
		return fromDir;
	}

	public void setFromDir(Path fromDir) {
		this.fromDir = fromDir;
	}

	public Path getToDir() {
		return toDir;
	}

	public void setToDir(Path toDir) {
		this.toDir = toDir;
	}

	public String getPattern() {
		return pattern;
	}

	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	public Path getBaseDir() {
		return baseDir;
	}

	public void setBaseDir(Path baseDir) {
		this.baseDir = baseDir;
	}

	public Format getFormat() {
		return format;
	}

	public void setFormat(Format format) {
		this.format = format;
	}

	static List<Path> findFiles(Path rootDir, String pattern) throws IOException {
		FileSystem fs = FileSystems.getDefault();
		PathMatcher matcher = fs.getPathMatcher("glob:" + pattern);

		List<Path> results = new ArrayList<>();

		Files.walkFileTree(rootDir,
				new SimpleFileVisitor<>() {
					@Override
					public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
						Path relative = rootDir.relativize(file);
						if (matcher.matches(relative)) {
							results.add(relative);
						}
						return FileVisitResult.CONTINUE;
					}
				});
		return results;
    }

    @Override
    public String toString() {
        return Objects.requireNonNullElseGet(name, () -> getClass().getSimpleName());
    }
}
