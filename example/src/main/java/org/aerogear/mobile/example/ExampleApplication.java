package org.aerogear.mobile.example;

import android.app.Application;

import org.aerogear.mobile.core.MobileCore;

public class ExampleApplication extends Application {

    @Override
    public void onTerminate() {
        super.onTerminate();

        MobileCore.getInstance().destroy();
    }

}
