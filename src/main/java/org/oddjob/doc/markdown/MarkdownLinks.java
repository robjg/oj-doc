package org.oddjob.doc.markdown;

import org.oddjob.doc.util.LinkFormatter;

public class MarkdownLinks implements LinkFormatter {

    @Override
    public String noLinkFor(String signature, String label) {

        StringBuilder linkBuilder = new StringBuilder();
        linkBuilder.append('`');

        if (label != null) {
            linkBuilder.append(label).append(' ');
        }

        return linkBuilder.append(signature)
                .append('`')
                .toString();
    }

    @Override
    public String linkFor(String url, String label) {

        if (label == null) {
            label = url;
        }

        return "[" + label + "](" + url + ")";
    }
}
