package org.oddjob.doc.markdown;

import org.oddjob.doc.util.LinkFormatter;

public class MarkdownLinks implements LinkFormatter {

    @Override
    public String noLinkFor(String signature, String label) {

        String link = signature;
        if (label != null) {
            link += " " + label;
        }
        return "<code>" + link + "</code>";
    }

    @Override
    public String linkFor(String url, String label) {

        if (label == null) {
            label = url;
        }

        return "[" + label + "](" + url + ")";
    }
}
