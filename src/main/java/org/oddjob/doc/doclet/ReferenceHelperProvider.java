package org.oddjob.doc.doclet;

import com.sun.source.util.DocTrees;
import org.oddjob.doc.util.DocUtil;
import org.oddjob.doc.util.InlineTagHelper;
import org.oddjob.doc.util.LoaderProvider;

import javax.lang.model.element.TypeElement;
import java.util.function.Function;

/**
 * Helper provider for the Reference.
 */
public class ReferenceHelperProvider implements InlineHelperProvider {

    private final DocTrees docTrees;

    private final LoaderProvider loaderProvider;

    private final Function<String, String> refLookup;

    private final Function<String, String> apiDirFunc;

    public ReferenceHelperProvider(DocTrees docTrees,
                                   LoaderProvider loaderProvider,
                                   Function<String, String> refLookup,
                                   Function<String, String> apiDirFunc) {
        this.docTrees = docTrees;
        this.loaderProvider = loaderProvider;
        this.refLookup = refLookup;
        this.apiDirFunc = apiDirFunc;
    }

    @Override
    public InlineTagHelper forElement(TypeElement typeElement) {

        String pathToRoot = DocUtil.pathToRoot(typeElement);

        return new ReferenceInlineTagHelper(docTrees, loaderProvider, refLookup, apiDirFunc, pathToRoot);
    }

}
