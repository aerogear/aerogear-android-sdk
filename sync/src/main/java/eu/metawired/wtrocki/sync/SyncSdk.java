package eu.metawired.wtrocki.sync;

import android.content.Context;
import android.util.Log;

import org.aerogear.mobile.core.api.Core;
import org.aerogear.mobile.core.api.SdkCore;
import org.aerogear.mobile.core.configuration.ServiceConfig;

/**
 * Sync service SDK
 *
 * Need to be used in top level app:
 *
 * new SyncSdk(context).performSync();
 */
public class SyncSdk {

    private Core core;

    public SyncSdk(Context context) {
        this.core = new SdkCore(context);
    }

    /**
     * Test method
     */
    public void performSync(){
        ServiceConfig sync = this.core.getConfiguration("sync");
        Log.i("Sync", sync.getName());
    }
}
