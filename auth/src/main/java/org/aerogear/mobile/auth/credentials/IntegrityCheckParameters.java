package org.aerogear.mobile.auth.credentials;

//TODO: not sure if this is required
public interface IntegrityCheckParameters {

    String getAudience();
    String getIssuer();
    String getPublicKey();

    /**
     * Check whether the parameters are valid or not. The criteria for validity is that each of
     * the parameters is defined (not null) and has valid formatting.
     * @return <code>true</code> if the parameters are valid.
     */
    boolean isValid();
    String serialize();
}
