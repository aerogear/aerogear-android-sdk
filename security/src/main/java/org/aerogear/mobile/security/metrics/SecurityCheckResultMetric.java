package org.aerogear.mobile.security.metrics;

import java.util.Arrays;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.support.annotation.NonNull;

import org.aerogear.mobile.core.MobileCore;
import org.aerogear.mobile.core.logging.Logger;
import org.aerogear.mobile.core.metrics.Metrics;
import org.aerogear.mobile.security.DeviceCheckResult;

/**
 * Metric representation of {@link DeviceCheckResult}. This is intended to be used with the
 * {@link org.aerogear.mobile.core.metrics.MetricsService}.
 */
public class SecurityCheckResultMetric implements Metrics<JSONArray> {

    private final JSONArray data;
    private final Logger LOG = MobileCore.getLogger();
    private final String TAG = "SecurityCheckResultMetric";

    public static final String IDENTIFIER = "security";
    public static final String KEY_ID = "id";
    public static final String KEY_NAME = "name";
    public static final String KEY_VALUE = "passed";

    /**
     * Creates a SecurityCheckResultMetric object.
     *
     * @param results the list of {@link DeviceCheckResult} of the tests executed
     * @throws IllegalArgumentException if result is null
     *
     */
    public SecurityCheckResultMetric(@NonNull final Iterable<DeviceCheckResult> results) {
        this.data = getDataFromResult(results);
    }

    /**
     * Creates a SecurityCheckResultMetric object.
     *
     * @param results the list of {@link DeviceCheckResult} of the tests executed
     * @throws IllegalArgumentException if result is null
     *
     */
    public SecurityCheckResultMetric(@NonNull final DeviceCheckResult... results) {
        this.data = getDataFromResult(Arrays.asList(results));
    }

    /**
     * Gets the name of the check performed.
     *
     * @return {@link String} name of security check
     */
    @Override
    public String identifier() {
        return IDENTIFIER;
    }

    /**
     * Gets the data from the result which contains whether the check passed or not.
     *
     * @return {@link JSONArray} containing the results for self-defence checks
     */
    @Override
    public JSONArray data() {
        // TODO: consider returning a deep clone
        return data;
    }

    /**
     * Creates the data structure that stores whether or not the result passed or not.
     *
     * @param results the {@link DeviceCheckResult} iterable of the test executed
     * @return {@link JSONArray} data
     */
    private JSONArray getDataFromResult(final Iterable<DeviceCheckResult> results) {
        final JSONArray data = new JSONArray();

        try {
            for (DeviceCheckResult result : results) {
                final JSONObject resultJson = new JSONObject();
                resultJson.put(KEY_ID, result.getId());
                resultJson.put(KEY_NAME, result.getName());
                resultJson.put(KEY_VALUE, result.passed());
                data.put(resultJson);
            }
        } catch (JSONException e) {
            // should never happen since we're building from scratch
            LOG.error(TAG, "Error building JSON from Self Defence Check result", e);
        }

        return data;
    }
}
