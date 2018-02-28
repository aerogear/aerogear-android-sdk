package org.aerogear.mobile.security.metrics;

import android.support.annotation.NonNull;

import org.aerogear.mobile.core.metrics.Metrics;
import org.aerogear.mobile.security.SecurityCheckResult;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.aerogear.mobile.core.utils.SanityCheck.nonNull;

/**
 * Metric representation of {@link SecurityCheckResult}. This is intended to be used with the
 * {@link org.aerogear.mobile.core.metrics.MetricsService}.
 */
public class SecurityCheckResultMetric implements Metrics {

    private final String identifier;
    private final Map<String, String> data;

    /**
     * Creates a SecurityCheckResultMetric object.
     *
     * @param result the {@link SecurityCheckResult} of the test executed
     * @throws IllegalArgumentException if result is null
     */
    public SecurityCheckResultMetric(@NonNull final SecurityCheckResult result) {
        this.identifier = nonNull(result, "result").getName();
        this.data = getDataFromResult(result);
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
    public Map<String, String> data() {
        return Collections.unmodifiableMap(data);
    }

    /**
     * Creates the data structure that stores whether or not the result passed or not.
     *
     * @param result the {@link SecurityCheckResult} of the test executed
     * @return {@link Map} data
     */
    private Map<String, String> getDataFromResult(final SecurityCheckResult result) {
        final Map<String, String> data = new HashMap<>();
        data.put("passed", String.valueOf(result.passed()));
        return data;
    }
}
