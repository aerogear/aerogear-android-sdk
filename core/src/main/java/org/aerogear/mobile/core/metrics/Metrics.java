package org.aerogear.mobile.core.metrics;

import org.json.JSONObject;

/**
 * Interface for a record to be published to the metrics service
 *
 * @apiNote Not intended by direct usage outside of the SDK
 *
 * @param <T> A type that can be serialized into a {@link JSONObject} value
 */
public abstract class Metrics<T> {

    /**
     * The key to identify this record inside the metrics payload
     *
     * @return A string identifying the payload
     */
    protected abstract String getIdentifier();

    /**
     * The data to be included under the identifier key in the metrics payload
     *
     * @return The serializable data object
     */
    protected abstract T getData();

}
