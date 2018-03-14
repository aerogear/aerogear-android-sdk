package org.aerogear.mobile.push;

import java.util.Map;

import android.content.Context;

public interface MessageHandler {

    /**
     * Invoked when server delivered a message to the device.
     *
     * @param message A map containing the submitted key/value pairs
     */
    public void onMessage(Context context, Map<String, String> message);

}
