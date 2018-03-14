package org.aerogear.mobile.push;

import java.util.Map;

public interface MessageHandler {

    /**
     * Invoked when server delivered a message to the device.
     *
     * @param message A map containing the submitted key/value pairs
     */
    public void onMessage(Map<String, String> message);

}
