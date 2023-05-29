package org.oddjob.tools.includes;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class PlainStreamToText implements StreamToText {

	@Override
	public String load(InputStream input) throws IOException {

		return new String(input.readAllBytes(), StandardCharsets.UTF_8);
	}
}
