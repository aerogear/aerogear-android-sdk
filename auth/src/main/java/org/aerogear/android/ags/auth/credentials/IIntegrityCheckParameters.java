package org.aerogear.android.ags.auth.credentials;

public interface IIntegrityCheckParameters {

    String getAudience();
    String getIssuer();
    String getPublicKey();
    boolean isValid();
    String serialize();
}
