package org.oddjob.doc.taglet;

import jdk.javadoc.doclet.Doclet;
import jdk.javadoc.doclet.DocletEnvironment;
import jdk.javadoc.doclet.Taglet;
import org.oddjob.doc.util.TagletProvider;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Provides Unkown Inline Taglets for the Reference Doclet and Block Taglets for
 * the Standard Javadoc Taglet.
 */
public class UnknownInlineTagletProvider implements TagletProvider {

    private final Map<String, Taglet> taglets = new HashMap<>();


    public UnknownInlineTagletProvider(DocletEnvironment env, Doclet doclet) {

        Taglet xmlResource = new XmlResourceTaglet();
        xmlResource.init(env, doclet);
        taglets.put(xmlResource.getName(), xmlResource);
        Taglet textResource = new PlainTextResourceTaglet();
        textResource.init(env, doclet);
        taglets.put(textResource.getName(), textResource);
    }

    @Override
    public Optional<Taglet> tagletFor(String name) {
        return Optional.ofNullable(taglets.get(name));
    }
}
