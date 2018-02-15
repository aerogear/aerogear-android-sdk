package org.aerogear.mobile.metrics.interfaces;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

public abstract class MetricsProvider {
    public String namespace() {
        return null;
    }

    public JSONObject metrics(final Context context) throws JSONException {
        return new JSONObject();
    }

    @Override
    public int hashCode() {
        if (namespace() == null) {
            return 0;
        }

        // Two providers with the same namespace are considered equal
        return namespace().hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null)
            return false;
        if (getClass() != o.getClass())
            return false;
        MetricsProvider person = (MetricsProvider) o;
        return Objects.equals(namespace(), person.namespace());
    }
}
