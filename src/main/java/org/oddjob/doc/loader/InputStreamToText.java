package org.oddjob.doc.loader;

import java.io.IOException;
import java.io.InputStream;

/**
 * Read an {@link InputStream} into a {@link String}.
 * 
 * @author rob
 *
 */
public interface InputStreamToText {

	String load(InputStream input) throws IOException;
}
