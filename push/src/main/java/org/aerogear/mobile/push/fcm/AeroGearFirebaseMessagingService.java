package org.aerogear.mobile.push.fcm;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.aerogear.mobile.push.PushService;

public class AeroGearFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        PushService.notifyHandlers(remoteMessage.getData());

    }

}
