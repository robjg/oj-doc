package org.oddjob.doc.taglet;

import com.sun.source.doctree.UnknownInlineTagTree;
import org.oddjob.doc.doclet.CustomTagNames;
import org.oddjob.doc.loader.IncludeLoader;
import org.oddjob.doc.loader.JavaCodeLoader;
import org.oddjob.doc.loader.PlainTextLoader;
import org.oddjob.doc.loader.XmlLoader;
import org.oddjob.doc.util.LoaderProvider;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Provides Unknown Inline {@link IncludeLoader} for the Reference Doclet and Block Taglets for
 * the Standard Javadoc Taglet. Used to process {@link UnknownInlineTagTree} nodes.
 */
public class UnknownInlineLoaderProvider implements LoaderProvider {

    private final Map<String, IncludeLoader> loaders = new HashMap<>();


    public UnknownInlineLoaderProvider(ClassLoader classLoader) {

        loaders.put(CustomTagNames.XML_RESOURCE_TAG_NAME, XmlLoader.fromResource(classLoader));
        loaders.put(CustomTagNames.TEXT_RESOURCE_TAG_NAME, PlainTextLoader.fromResource(classLoader));
        loaders.put(CustomTagNames.JAVA_RESOURCE_TAG_NAME, JavaCodeLoader.fromResource(classLoader));
    }

    @Override
    public Optional<IncludeLoader> loaderFor(String name) {
        return Optional.ofNullable(loaders.get(name));
    }
}
