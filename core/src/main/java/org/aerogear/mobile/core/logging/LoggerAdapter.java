package org.aerogear.mobile.core.logging;

import android.util.Log;

public class LoggerAdapter implements Logger {

    private static final String TAG = "AeroGear";

    @Override
    public void info(String tag, String message) {
        Log.i(tag, message);
    }

    @Override
    public void info(String message) {
        Log.i(TAG, message);
    }

    @Override
    public void info(String tag, String message, Exception e) {
        Log.i(tag, message, e);
    }

    @Override
    public void info(String message, Exception e) {
        Log.i(TAG, message, e);
    }

    @Override
    public void warning(String tag, String message) {
        Log.w(tag, message);
    }

    @Override
    public void warning(String message) {
        Log.w(TAG, message);
    }

    @Override
    public void warning(String tag, String message, Exception e) {
        Log.w(tag, message, e);
    }

    @Override
    public void warning(String message, Exception e) {
        Log.w(TAG, message, e);
    }

    @Override
    public void debug(String tag, String message) {
        Log.d(tag, message);
    }

    @Override
    public void debug(String message) {
        Log.d(TAG, message);
    }

    @Override
    public void debug(String tag, String message, Exception e) {
        Log.d(tag, message, e);
    }

    @Override
    public void debug(String message, Exception e) {
        Log.d(TAG, message, e);
    }

    @Override
    public void error(String tag, String message) {
        Log.e(tag, message);
    }

    @Override
    public void error(String message) {
        Log.e(TAG, message);
    }

    @Override
    public void error(String tag, String message, Exception e) {
        Log.e(tag, message, e);
    }

    @Override
    public void error(String message, Exception e) {
        Log.e(TAG, message, e);
    }

}
