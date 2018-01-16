package org.aerogear.mobile.core;

import android.util.Log;

import org.aerogear.mobile.core.logging.Logger;

public final class MobileCore implements ServiceModule {


    private MobileCore() {

    }

    public static Logger defaultLog() {
        return (String message, Exception e) -> {
            Log.e("MOBILE_CORE", message, e);
        };
    }


    @Override
    public void bootstrap(Object... args) {
        for (Object object : args) {
            if (object instanceof Class) {
                try {
                    Object instance = ((Class)object).newInstance();
                    if (instance instanceof ServiceModule) {

                    }
                } catch (InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
