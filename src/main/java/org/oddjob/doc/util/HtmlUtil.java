package org.oddjob.doc.util;

/**
 * General purpose HTML utilities.
 */
public class HtmlUtil {

    /**
     * Escape HTML. From https://stackoverflow.com/questions/1265282/what-is-the-recommended-way-to-escape-html-symbols-in-plain-java
     * @param html The HTML.
     * @return Escaped HTML.
     */
    public static String escapeHtml(String html) {
        StringBuilder out = new StringBuilder(Math.max(16, html.length()));
        for (int i = 0; i < html.length(); i++) {
            char c = html.charAt(i);
            if (c > 127 || c == '"' || c == '\'' || c == '<' || c == '>' || c == '&') {
                out.append("&#");
                out.append((int) c);
                out.append(';');
            } else {
                out.append(c);
            }
        }
        return out.toString();
    }
}
