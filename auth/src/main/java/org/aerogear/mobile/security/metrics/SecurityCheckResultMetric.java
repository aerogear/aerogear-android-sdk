package org.aerogear.mobile.security.metrics;

import android.support.annotation.NonNull;

import org.aerogear.mobile.core.MobileCore;
import org.aerogear.mobile.core.metrics.Metrics;
import org.aerogear.mobile.security.SecurityCheckResult;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/**
 * Metric representation of {@link SecurityCheckResult}. This is intended to be used with the
 * {@link org.aerogear.mobile.core.metrics.MetricsService}.
 */
public class SecurityCheckResultMetric implements Metrics {

    private final String identifier = "security";
    private final JSONObject data;

    /**
     * Creates a SecurityCheckResultMetric object.
     *
     * @param results the list of {@link SecurityCheckResult} of the tests executed
     * @throws IllegalArgumentException if result is null
     *
     */
    public SecurityCheckResultMetric(@NonNull final Iterable<SecurityCheckResult> results) {
        this.data = getDataFromResult(results.iterator().next());
    }

    /**
     * Creates a SecurityCheckResultMetric object.
     *
     * @param results the list of {@link SecurityCheckResult} of the tests executed
     * @throws IllegalArgumentException if result is null
     *
     */
    public SecurityCheckResultMetric(@NonNull final SecurityCheckResult... results) {
        this.data = getDataFromResult(results[0]);
    }

    /**
     * Gets the name of the check performed.
     *
     * @return {@link String} name of security check
     */
    @Override
    public String identifier() {
        return identifier;
    }

    /**
     * Gets the data from the result which contains whether the check passed or not.
     *
     * @return {@link Map} where the key is a {@link String} and
     * the value is <code>true</code> if the check result passed
     */
    @Override
    public JSONObject data() {
        // TODO: consider returning a deep clone
        return data;
    }

    /**
     * Creates the data structure that stores whether or not the result passed or not.
     *
     * @param result the {@link SecurityCheckResult} of the test executed
     * @return {@link Map} data
     */
    private JSONObject getDataFromResult(final SecurityCheckResult result) {
        final JSONObject data = new JSONObject();
        try {
            data.put("passed", result.passed());
        } catch (JSONException e) {
            // should never happen since we're building from scratch
            MobileCore.getLogger().error("Error building JSON from Self Defence Check result", e);
        }
        return data;
    }
}
