package org.oddjob.tools;

/**
 * A really simple Logger for the Doc.
 */
public class OjDocLogger {

    private static final OjDocLogger LOGGER = new OjDocLogger(
            Boolean.parseBoolean(System.getProperty("ojdoc.debug", "false")));

    private final boolean debugEnabled;

    public static OjDocLogger getLogger() {
        return LOGGER;
    }

    public OjDocLogger(boolean debugEnabled) {
        this.debugEnabled = debugEnabled;
    }

    public void debug(String msg) {

        if (debugEnabled) {
            System.out.println("DEBUG: " + msg);
        }
    }

    public void warn(String msg) {

        System.out.println("WARN: " + msg);
    }

    public void error(String msg, RuntimeException e) {

        System.out.println("ERROR: " + msg);
        e.printStackTrace(System.out);
    }
}
