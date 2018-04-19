package org.aerogear.mobile.push.fcm;

import com.google.firebase.iid.FirebaseInstanceIdService;

import org.aerogear.mobile.core.MobileCore;
import org.aerogear.mobile.push.PushService;

/**
 * This is an Android Service which listens for InstanceID messages from Firebase services. These
 * messages arrive periodically from Firebase systems to alert the application it needs to refresh
 * its registration tokens.
 *
 * See:
 * https://firebase.google.com/docs/reference/android/com/google/firebase/iid/FirebaseInstanceIdService.html
 *
 */
public class AeroGearFirebaseInstanceIdService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();

        MobileCore.getInstance().getService(PushService.class).refreshToken();
    }

}
