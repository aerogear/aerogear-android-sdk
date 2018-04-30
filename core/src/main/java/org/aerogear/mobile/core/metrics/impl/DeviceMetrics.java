package org.aerogear.mobile.core.metrics.impl;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Build;

import org.aerogear.mobile.core.MobileCore;
import org.aerogear.mobile.core.metrics.Metrics;


/**
 * Collects device metrics
 */
public final class DeviceMetrics extends Metrics<JSONObject> {

    private final String platform;
    private final String platformVersion;

    public DeviceMetrics() {
        this.platform = "android";
        this.platformVersion = String.valueOf(Build.VERSION.SDK_INT);
    }

    @Override
    public String getIdentifier() {
        return "device";
    }

    @Override
    public JSONObject getData() {
        final JSONObject data = new JSONObject();
        try {
            data.put("platform", platform);
            data.put("platformVersion", platformVersion);
        } catch (JSONException e) {
            MobileCore.getLogger().error("Error building JSON for Device Metrics", e);
        }
        return data;
    }



}
