package org.oddjob.doc.doclet;

public class IndexLine {

	private final String name;

	private final String fileName;

	private final String firstSentence;
	
	public IndexLine(String name, String fileName, String firstLine) {
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

	public String getFirstSentence() {
		return firstSentence;
	}
}
