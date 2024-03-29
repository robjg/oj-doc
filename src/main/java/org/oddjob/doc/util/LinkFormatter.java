package org.oddjob.doc.util;

/**
 * Formats a link for specific Markup - i.e. HTML or Markdown.
 */
public interface LinkFormatter {

    /**
     * Used when the link can't be found so return preformatted text of the link.
     *
     * @param signature The signature that couldn't be found. Never null.
     * @param label The label if any. Probably null.
     *
     * @return The preformatted text.
     */
    String noLinkFor(String signature, String label);

    /**
     * Format a link from a URL.
     *
     * @param url The url. Never null.
     * @param label The label. Not normally null, but could be.
     *
     * @return The formatted link.
     */
    String linkFor(String url, String label);

}
