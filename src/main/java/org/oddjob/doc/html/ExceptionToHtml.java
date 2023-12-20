package org.oddjob.doc.html;

/**
 * Simple utility to wrap an exception.
 */
public class ExceptionToHtml {

    public static String toHtml(Exception e) {

        return "<p><em>" + e + "</em></p>\n";
    }
}
