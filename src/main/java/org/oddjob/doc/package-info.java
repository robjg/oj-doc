/**
 * Provides utilities for creating the Oddjob documentation. This includes:
 * <ul>
 *     <li>a Doclet for creating the Oddjob Reference from Javadoc.</li>
 *     <li>Taglets to Handle the OddjobTags in Javadoc.
 *     <p>Note that the only includes supported in the Javadoc are currently
 *     {@link org.oddjob.doc.doclet.CustomTagNames#XML_RESOURCE_TAG_NAME} and
 *     {@link org.oddjob.doc.doclet.CustomTagNames#TEXT_RESOURCE_TAG_NAME}. Java could be added
 *     if needed.</p>
 *     </li>
 *     <li>DocPostProcessor that processes a document and inserts include snippets.</li>
 * </ul>
 *
 */
package org.oddjob.doc;