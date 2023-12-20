package org.oddjob.doc.loader;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class PlainInputStreamToText implements InputStreamToText {

	@Override
	public String load(InputStream input) throws IOException {

		return new String(input.readAllBytes(), StandardCharsets.UTF_8);
	}
}
