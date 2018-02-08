package org.aerogear.mobile.example;

import android.app.Application;

import org.aerogear.mobile.core.MobileCore;

public class ExampleApplication extends Application {

    private MobileCore mobileCore;

    @Override
    public void onCreate() {
        super.onCreate();

        mobileCore = MobileCore.init(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();

        mobileCore.destroy();
    }

    public MobileCore getMobileCore() {
        return mobileCore;
    }

}
