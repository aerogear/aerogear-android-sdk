package org.aerogear.auth.credentials;

import org.json.JSONException;
import org.json.JSONObject;

public class IntegrityCheckParameters implements IIntegrityCheckParameters {

    private String issuer;
    private String audience;
    private String publicKey;

    public IntegrityCheckParameters(final String audience, final String issuer, final String publicKey) {
        this.issuer = issuer;
        this.audience = audience;
        this.publicKey = publicKey;
    }

    public IntegrityCheckParameters() {}

    public String getIssuer() {
        return this.issuer;
    }

    public String getAudience() {
        return this.audience;
    }

    public String getPublicKey() { return this.publicKey; }

    public String serialize() throws JSONException {
        return new JSONObject()
            .put("audience", this.audience)
            .put("issuer", this.issuer)
            .put("publicKey", this.publicKey)
            .toString();
    }

    public static IntegrityCheckParameters deserialize(final String serializedParams) throws JSONException {
        JSONObject jsonParams = new JSONObject(serializedParams);
        String audience = jsonParams.getString("audience");
        String issuer = jsonParams.getString("issuer");
        String publicKey = jsonParams.getString("publicKey");
        return new IntegrityCheckParameters(audience, issuer, publicKey);
    }

    public boolean isValid() {
        return issuer != null && audience != null && publicKey != null;
    }
}
