package org.aerogear.auth.credentials;

import org.json.JSONException;

public interface IIntegrityCheckParameters {

    /**
     * Get the audience parameter.
     * @return audience parameter.
     */
    String getAudience();

    /**
     * Get the issuer parameter.
     * @return issuer parameter
     */
    String getIssuer();

    /**
     * Check whether the integrity check parameters are valid.
     * @return <code>true</code> if the parameters are valid.
     */
    boolean isValid();

    /**
     * Return json representation of the parameters
     * @return json string representation of parameters
     */
    String serialize() throws JSONException;
}
