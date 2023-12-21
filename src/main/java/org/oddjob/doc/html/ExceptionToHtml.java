package org.oddjob.doc.html;

import org.oddjob.arooa.beandocs.element.ExceptionElement;

/**
 * Simple utility to wrap an exception.
 */
public class ExceptionToHtml {

    public static String toHtml(ExceptionElement exceptionElement) {

        return "<p><em>" + exceptionElement.getMessage() + "</em></p>\n";
    }
}
