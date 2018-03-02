package org.aerogear.mobile.core.metrics;

import org.json.JSONObject;

public interface Metrics {

    String identifier();

    JSONObject data();

}
