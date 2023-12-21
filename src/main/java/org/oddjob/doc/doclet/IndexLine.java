package org.oddjob.doc.doclet;

import org.oddjob.arooa.beandocs.element.BeanDocElement;

import java.util.List;

public class IndexLine {

	private final String className;
	private final String name;
	private final List<BeanDocElement> firstSentence;
	
	public IndexLine(String className, String name, List<BeanDocElement> firstLine) {
		this.className = className;
		this.name = name;
		this.firstSentence = firstLine;
	}
	
	public String getClassName() {
		return className;
	}
	
	public String getName() {
		return name;
	}
	
	public List<BeanDocElement> getFirstSentence() {
		return firstSentence;
	}
}
