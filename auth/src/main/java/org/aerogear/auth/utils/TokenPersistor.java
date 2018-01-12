package org.aerogear.auth.utils;

import org.aerogear.auth.credentials.OIDCCredentials;

/**
 * Saves, retrieves and delete a token.
 */
public class TokenPersistor {
    private TokenPersistor() {
    }

    public static OIDCCredentials load() {
        throw new IllegalStateException("Not yet implemented");
    }

    /**
     * Saves a token
     * @param token token to be saved
     */
    public static void save(OIDCCredentials token) {
        throw new IllegalStateException("Not yet implemented");
    }

    /**
     * Deletes a token
     * @param token token to be deleted
     */
    public static void delete(OIDCCredentials token) {
        throw new IllegalStateException("Not yet implemented");
    }
}
