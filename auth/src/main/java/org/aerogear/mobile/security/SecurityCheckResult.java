package org.aerogear.mobile.security;

public interface SecurityCheckResult {
    /**
     * Get the name of the security check that this is the result of. This can be used for
     * reporting purposes.
     *
     * @return The name of the check
     */
    String getName();

    /**
     * Whether the check was successful.
     *
     * @return <code>true</code> if the check was successful.
     */
    boolean passed();
}
