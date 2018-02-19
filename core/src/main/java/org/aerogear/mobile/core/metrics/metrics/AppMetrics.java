package org.aerogear.mobile.core.metrics.metrics;

import android.content.Context;

import org.aerogear.mobile.core.MobileCore;
import org.aerogear.mobile.core.metrics.Metrics;

import java.util.HashMap;
import java.util.Map;

/**
 * Collects app metrics
 */
public class AppMetrics implements Metrics {

    private final String appId;
    private final String appVersion;
    private final String sdkVersion;

    public AppMetrics(final Context context) {
        this.appId = context.getPackageName();
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
    public Map<String, String> data() {
        Map<String, String> data = new HashMap<>();
        data.put("appId", appId);
        data.put("appVersion", appVersion);
        data.put("sdkVersion", sdkVersion);
        return data;
    }

}
