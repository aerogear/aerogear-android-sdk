package org.aerogear.mobile.core.logging;

public interface Logger {

    void info(String tag, String message);
    void info(String message);
    void info(String tag, String message, Exception e);
    void info(String message, Exception e);

    void warning(String tag, String message);
    void warning(String message);
    void warning(String tag, String message, Exception e);
    void warning(String message, Exception e);

    void debug(String tag, String message);
    void debug(String message);
    void debug(String tag, String message, Exception e);
    void debug(String message, Exception e);

    void error(String tag, String message);
    void error(String message);
    void error(String tag, String message, Exception e);
    void error(String message, Exception e);

}
