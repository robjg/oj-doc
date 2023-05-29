package org.oddjob.doc.doclet;

/**
 * Constants for Oddjob's custom Javadoc tags used to build the 
 * Oddojob Reference.
 * 
 * @author rob
 *
 */
public interface CustomTagNames {

	String EOL = System.getProperty("line.separator");
	
	/**
	 * Provide a description of the job or value.
	 */
	String DESCRIPTION_TAG_NAME = "oddjob.description";
	
	String DESCRIPTION_TAG = "@" + DESCRIPTION_TAG_NAME;
	
	/**
	 * Tag for a property of a job or value.
	 */
	String PROPERTY_TAG_NAME = "oddjob.property";
	
	String PROPERTY_TAG = "@" + PROPERTY_TAG_NAME;
	
	/**
	 * Tag for if the property is required.
	 */
	String REQUIRED_TAG_NAME = "oddjob.required";
	
	String REQUIRED_TAG = "@" + REQUIRED_TAG_NAME;
	
	/**
	 * Tag for an example of a Job or value.
	 */
	String EXAMPLE_TAG_NAME = "oddjob.example";
	
	String EXAMPLE_TAG = "@" + EXAMPLE_TAG_NAME;
	
	/**
	 * Tag for an XML resource that is to be loaded into the documentation
	 * as formatted XML.
	 */
	String XML_RESOURCE_TAG_NAME = "oddjob.xml.resource";
	
	String XML_RESOURCE_TAG = "@" + XML_RESOURCE_TAG_NAME;
	
	/**
	 * Tag for a Java Code file that is to be formatted and loaded
	 * into the documentation.
	 */
	String JAVA_FILE_TAG_NAME = "oddjob.java.file";
	
	String JAVA_FILE_TAG = "@" + JAVA_FILE_TAG_NAME;
	
	/**
	 * Tag for an XML file that is to be loaded into the documentation
	 * as formatted XML.
	 */
	String XML_FILE_TAG_NAME = "oddjob.xml.file";
	
	String XML_FILE_TAG = "@" + XML_FILE_TAG_NAME;
	
	
	/**
	 * Tag for a text file that is to be loaded into the documentation
	 * as formatted HTML.
	 */
	String TEXT_FILE_TAG_NAME = "oddjob.text.file";
	
	String TEXT_FILE_TAG = "@" + TEXT_FILE_TAG_NAME;
	
	
	/**
	 * Tag for a text resource that is to be loaded into the documentation
	 * as formatted HTML.
	 */
	String TEXT_RESOURCE_TAG_NAME = "oddjob.text.resource";
	
	String TEXT_RESOURCE_TAG = "@" + TEXT_RESOURCE_TAG_NAME;
	
}
