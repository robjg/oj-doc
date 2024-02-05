package org.oddjob.doc.doclet;

import org.oddjob.doc.util.DocUtil;
import org.oddjob.doc.util.InlineTagHelper;

import java.util.function.Function;

/**
 * Helper provider for the Reference.
 */
public class ReferenceHelperProvider implements InlineHelperProvider {

    private final Function<String, String> refLookup;

    private final Function<String, String> apiDirFunc;

    public ReferenceHelperProvider(Function<String, String> refLookup,
                                   Function<String, String> apiDirFunc) {
        this.refLookup = refLookup;
        this.apiDirFunc = apiDirFunc;
    }

    @Override
    public InlineTagHelper forElement(String qualifiedClassName) {

        String pathToRoot = DocUtil.pathToRoot(qualifiedClassName);

        return new ReferenceInlineTagHelper(refLookup, apiDirFunc, pathToRoot);
    }

}
