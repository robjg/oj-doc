package org.oddjob.doc.doclet;

import org.oddjob.arooa.beandocs.element.LinkElement;
import org.oddjob.doc.util.InlineTagHelper;

import java.util.function.Function;

/**
 * Inline helper for the Reference.
 */
public class ReferenceInlineTagHelper implements InlineTagHelper {

    private final Function<String, String> refLookup;

    private final Function<String, String> apiDirFunc;

    private final String pathToRoot;

    public ReferenceInlineTagHelper(Function<String, String> refLookup,
                                    Function<String, String> apiDirFunc,
                                    String pathToRoot) {
        this.refLookup = refLookup;
        this.apiDirFunc = apiDirFunc;
        this.pathToRoot = pathToRoot;
    }

    @Override
    public String processLink(LinkElement linkElement) {

        String qualifiedType = linkElement.getQualifiedType();

        if (qualifiedType == null) {
            String link = linkElement.getSignature();
            if (linkElement.getLabel() != null) {
                link += " " + linkElement.getLabel();
            }
            return "<code>" + link + "</code>";
        }

        String fileName = qualifiedType.replace('.', '/') +  ".html";

        String componentName = refLookup.apply(qualifiedType);

        if (componentName == null) {
            return "<code><a href='" + apiDirFunc.apply(pathToRoot) + "/" + fileName + "'>"
                    + qualifiedType + "</a></code>";
        }
        else {
            return "<a href='" + pathToRoot + "/" + fileName + "'>"
                    + componentName + "</a>";
        }
    }
}
