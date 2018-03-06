package org.aerogear.mobile.security;

/**
 * Interface for the results of a {@link SecurityCheck}.
 */
public interface SecurityCheckResult {
    /**
     * Get the name of the security check that this is the result of. This can be used for reporting
     * purposes.
     *
     * @return {@link String} the name of the check
     */
    String getName();

    /**
     * Whether the check was successful.
     *
     * @return <code>true</code> if the check was successful
     */
    boolean passed();
}
