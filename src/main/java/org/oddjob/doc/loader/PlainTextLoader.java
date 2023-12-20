package org.oddjob.doc.loader;

import org.oddjob.arooa.beandocs.element.PreformattedBlock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Creates Text that can be inserted into JavaDoc or another HTML document
 * from a plain text file.
 *
 * @author rob
 */
public class PlainTextLoader extends AbstractLoader {

    private static final Logger logger = LoggerFactory.getLogger(PlainTextLoader.class);

    private PlainTextLoader(Loader loader) {
        super(loader);
    }

    public static PlainTextLoader fromFile(Path base) {
        return new PlainTextLoader(new FromFile(base));
    }

    public static PlainTextLoader fromResource(ClassLoader classLoader) {
        return new PlainTextLoader(new FromResource(classLoader));
    }

    @Override
    public PreformattedBlock load(String fileName) throws IOException {

        PreformattedBlock preformattedBlock = new PreformattedBlock();

        String contents = doLoad(fileName);

        preformattedBlock.setText(contents);

        return preformattedBlock;
    }
}
