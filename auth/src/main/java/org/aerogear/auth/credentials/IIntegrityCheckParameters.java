package org.aerogear.auth.credentials;

import org.json.JSONException;

public interface IIntegrityCheckParameters {

    String getAudience();
    String getIssuer();
    String getPublicKey();
    boolean isValid();
    String serialize() throws JSONException;
}
