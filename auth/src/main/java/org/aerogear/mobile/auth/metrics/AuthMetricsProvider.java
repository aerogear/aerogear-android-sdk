package org.aerogear.mobile.auth.metrics;

import android.content.Context;

import org.aerogear.mobile.metrics.interfaces.MetricsProvider;
import org.json.JSONException;
import org.json.JSONObject;

public class AuthMetricsProvider extends MetricsProvider {
    @Override
    public String namespace() {
        return "auth";
    }

    @Override
    public JSONObject metrics(Context context) throws JSONException {
        JSONObject metrics = new JSONObject();
        // TODO: add metrics
        return metrics;
    }
}
