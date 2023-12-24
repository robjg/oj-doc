package org.oddjob.doc.html;

import org.oddjob.arooa.beandocs.element.CodeElement;
import org.oddjob.arooa.beandocs.element.ExceptionElement;
import org.oddjob.arooa.beandocs.element.LiteralElement;

/**
 * General purpose HTML utilities.
 */
public class HtmlUtil {

    /**
     * Escape HTML. From <a href="https://stackoverflow.com/questions/1265282/what-is-the-recommended-way-to-escape-html-symbols-in-plain-java">here</a>
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

    /**
     * Code Element to HTML.
     *
     * @param code The code element.
     *
     * @return HTML.
     */
    public static String toHtml(CodeElement code) {
        return "<pre>" + escapeHtml(code.getText()) + "</pre>";
    }


    /**
     * Literal Element to HTML.
     *
     * @param literal The literal element.
     *
     * @return HTML.
     */
    public static String toHtml(LiteralElement literal) {
        return escapeHtml(literal.getText());
    }

    /**
     * Convert an Exception Element to HTML.
     *
     * @param exceptionElement The element.
     *
     * @return HTML.
     */
    public static String toHtml(ExceptionElement exceptionElement) {

        return "<p><em>" + escapeHtml(exceptionElement.getMessage()) + "</em></p>\n";
    }
}
