package org.oddjob.doc.doclet;

import org.oddjob.arooa.beandocs.element.BeanDocElement;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class IndexLine {

	private final String name;

	private final String fileName;

	private final List<? extends BeanDocElement> firstSentence;
	
	public IndexLine(String name, String fileName, List<? extends BeanDocElement> firstLine) {
		this.name = name;
		this.fileName = fileName;
		this.firstSentence = firstLine;
	}
	
	public String getName() {
		return name;
	}

	public String getFileName() {
		return fileName;
	}

	public List<? extends BeanDocElement> getFirstSentence() {
		return Objects.requireNonNullElse(firstSentence, Collections.emptyList());
	}
}
