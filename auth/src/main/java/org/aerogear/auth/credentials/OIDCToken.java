package org.aerogear.auth.credentials;

import java.util.Arrays;

/**
 * This object holds and parse an OIDC token
 */
public class OIDCToken {
    private final byte[] tokenValue;

    public OIDCToken(final byte[] tokenValue) {
        this.tokenValue = Arrays.copyOf(tokenValue, tokenValue.length);

        parseToken();
    }

    /**
     * Parses the raw token bytes to extract token data (expiration, issue time, etc.)
     */
    private void parseToken() {
        // TODO: implement
    }

    /**
     * Returns the raw token bytes
     * @return
     */
    public byte[] getRaw() {
        return Arrays.copyOf(tokenValue, tokenValue.length);
    }
}
