package org.aerogear.mobile.security.metrics;

import org.aerogear.mobile.core.metrics.Metrics;
import org.aerogear.mobile.security.SecurityCheckResult;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class SecurityCheckResultMetric implements Metrics {

    private final String identifier;
    private final Map<String, String> data;

    public SecurityCheckResultMetric(final SecurityCheckResult result) {
        this.identifier = result.getName();
        this.data = getDataFromResult(result);
    }

    @Override
    public String identifier() {
        return identifier;
    }

    @Override
    public Map<String, String> data() {
        return Collections.unmodifiableMap(data);
    }

    private Map<String, String> getDataFromResult(final SecurityCheckResult result) {
        HashMap<String, String> data = new HashMap<>();
        data.put("passed", String.valueOf(result.passed()));
        return data;
    }
}
