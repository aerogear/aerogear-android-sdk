package org.aerogear.mobile.security;

/**
 * Listener for events about check execution.
 */
public interface DeviceCheckExecutorListener {

    /**
     * Called after each check is executed
     *
     * @param result the result of the check
     */
    void onExecuted(DeviceCheckResult result);

    /**
     * Called when all submitted checks has been executed.
     */
    void onComplete();
}
