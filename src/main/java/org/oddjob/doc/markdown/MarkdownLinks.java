package org.oddjob.doc.markdown;

import org.oddjob.doc.util.LinkFormatter;

/**
 * Format links as Markdown.
 */
public class MarkdownLinks implements LinkFormatter {

    @Override
    public String noLinkFor(String signature, String label) {

        return "`" + (label == null ? "" : label + " ") + signature + "`";
    }

    @Override
    public String linkFor(String url, String label) {

        if (label == null) {
            label = url;
        }

        return "[" + label + "](" + url + ")";
    }
}
