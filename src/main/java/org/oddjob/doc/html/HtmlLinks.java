package org.oddjob.doc.html;

import org.oddjob.doc.util.LinkFormatter;

/**
 * Format links as HTML.
 */
public class HtmlLinks implements LinkFormatter {

    @Override
    public String noLinkFor(String signature, String label) {
        return "<code>" + (label == null ? "" : label + " ") + signature + "</code>";
    }

    @Override
    public String linkFor(String url, String label) {

        if (label == null) {
            label = url;
        }

        return "<code><a href='" + url  + "'>" + label + "</a></code>";
    }
}
