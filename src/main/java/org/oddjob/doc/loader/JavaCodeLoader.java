package org.oddjob.doc.loader;

import org.oddjob.arooa.beandocs.element.BeanDocElement;
import org.oddjob.arooa.beandocs.element.ExceptionElement;
import org.oddjob.arooa.beandocs.element.JavaCodeBlock;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Creates text that can be inserted into JavaDoc or another XML document from
 * a Java Source Code File.
 *
 * @author rob
 */
public class JavaCodeLoader extends AbstractLoader {

    private JavaCodeLoader(Loader loader) {
        super(loader);
    }

    public static JavaCodeLoader fromFile(Path base) {
        return new JavaCodeLoader(new AbstractLoader.FromFile(base));
    }

    public static JavaCodeLoader fromResource(ClassLoader classLoader) {
        return new JavaCodeLoader(new AbstractLoader.FromResource(classLoader));
    }

    @Override
    public BeanDocElement load(String fileName) {

        try {
            String contents = doLoad(fileName);

            JavaCodeBlock javaCodeBlock = new JavaCodeBlock();
            javaCodeBlock.setCode(contents);

            return javaCodeBlock;
        } catch (IOException e) {
            return ExceptionElement.of(e);
        }
    }
}
