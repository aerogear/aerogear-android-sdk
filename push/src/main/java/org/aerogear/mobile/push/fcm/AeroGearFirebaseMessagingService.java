package org.aerogear.mobile.push.fcm;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.aerogear.mobile.push.PushService;

/**
 * AeroGear <code>Service</code> implementation for Firebase Cloud Messaging.
 *
 * Internally received messages are delivered to attached implementations of our
 * <code>MessageHandler</code> interface.
 */
public class AeroGearFirebaseMessagingService extends FirebaseMessagingService {

    /**
     * Called when a message is received.
     *
     * @param remoteMessage A remote Firebase Message.
     */
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        PushService.notifyHandlers(getApplicationContext(), remoteMessage.getData());
    }

}
