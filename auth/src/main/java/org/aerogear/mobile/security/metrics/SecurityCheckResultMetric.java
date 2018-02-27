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

    /**
     * The name of the check performed. Cannot be null.
     */
    private final String identifier;

    /**
     * A {@link Map} that stores whether the check passed or not. Cannot be null.
     */
    private final Map<String, String> data;

    /**
     * Creates a SecurityCheckResultMetric object.
     *
     * @param result the {@link SecurityCheckResult} of the test executed
     * @throws IllegalArgumentException if {@param result} is null
     */
    public SecurityCheckResultMetric(@NonNull final SecurityCheckResult result) {
        this.identifier = nonNull(result, "result").getName();
        this.data = getDataFromResult(result);
    }

    /**
     * Gets the name of the check performed.
     *
     * @return {@link String} identifier
     */
    @Override
    public String identifier() {
        return identifier;
    }

    /**
     * Gets the data from the result which contains whether the check passed or not.
     *
     * @return {@link Map<String, String>} data
     */
    @Override
    public Map<String, String> data() {
        return Collections.unmodifiableMap(data);
    }

    /**
     * Creates the data structure that stores whether or not the result passed or not.
     *
     * @param result the {@link SecurityCheckResult} of the test executed
     * @return {@link Map<String, String>} data
     */
    private Map<String, String> getDataFromResult(final SecurityCheckResult result) {
        final Map<String, String> data = new HashMap<>();
        data.put("passed", String.valueOf(result.passed()));
        return data;
    }
}
