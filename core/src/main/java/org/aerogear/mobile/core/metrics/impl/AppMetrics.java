package org.aerogear.mobile.core.metrics.impl;

import android.content.Context;

import org.aerogear.mobile.core.MobileCore;
import org.aerogear.mobile.core.metrics.Metrics;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static org.aerogear.mobile.core.utils.SanityCheck.nonNull;

/**
 * Collects app metrics
 */
public class AppMetrics implements Metrics<JSONObject> {

    private final String appId;
    private final String appVersion;
    private final String sdkVersion;

    public AppMetrics(final Context context) {
        this.appId = nonNull(context, "context").getPackageName();
        this.appVersion = MobileCore.getAppVersion();
        this.sdkVersion = MobileCore.getSdkVersion();
    }

    @Override
    public String identifier() {
        return "app";
    }

    /**
     * Return some app info
     *
     * @return Map of app info
     */
    @Override
    public JSONObject data() {
        JSONObject data = new JSONObject();
        try {
            data.put("appId", appId);
            data.put("appVersion", appVersion);
            data.put("sdkVersion", sdkVersion);
            return data;
        } catch (JSONException e) {
            MobileCore.getLogger().error("Error building JSON for App Metrics", e);
        }
        return data;
    }

}
