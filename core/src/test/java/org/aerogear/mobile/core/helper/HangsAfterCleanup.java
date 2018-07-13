package org.aerogear.mobile.core.helper;

/**
 * When testing OKHTTP it was discovered that the response from OkHttp can threadlock if it is
 * accessed after it is closed. This simulates that behavior without a network request.
 */
public class HangsAfterCleanup {
    public boolean closed = false;
    public static final String EXPECTED_VALUE = "Hello World";

    public String get() {
        if (closed) {
            try {
                Thread.sleep(Long.MAX_VALUE);
            } catch (InterruptedException ignore) {
            }
        }
        return EXPECTED_VALUE;
    }

    public void close() {
        closed = true;
    }
}
