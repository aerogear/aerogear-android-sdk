package org.aerogear.mobile.core.metrics;

import java.util.Map;

public interface Metrics {

    String identifier();

    Map<String, String> data();

}
