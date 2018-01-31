package org.aerogear.auth.credentials;

import org.json.JSONException;
import org.json.JSONObject;

public class IntegrityCheckParameters implements IIntegrityCheckParameters {

    private String issuer;
    private String audience;

    public IntegrityCheckParameters(String audience, String issuer) {
        this.issuer = issuer;
        this.audience = audience;
    }

    public String getIssuer() {
        return this.issuer;
    }

    public String getAudience() {
        return this.audience;
    }

    public String serialize() throws JSONException {
        return new JSONObject()
            .put("audience", this.audience)
            .put("issuer", this.issuer)
            .toString();
    }

    public static IntegrityCheckParameters deserialize(String serializedParams) throws JSONException {
        JSONObject jsonParams = new JSONObject(serializedParams);
        String audience = jsonParams.getString("audience");
        String issuer = jsonParams.getString("issuer");
        return new IntegrityCheckParameters(audience, issuer);
    }

    public boolean isValid() {
        return issuer != null && audience != null;
    }
}
