package org.aerogear.mobile.security;

/**
 * Interface for the results of a {@link SecurityCheck}.
 */
public interface SecurityCheckResult {
    /**
     * Gets the unique identifier of the security check that this is the result of. This can be used
     * for reporting purposes.
     *
     * @return {@link String} the name of the check
     */
    String getId();

    /**
     * Get the name for displaying the security check that this is the result of. This can be used
     * for reporting purposes.
     *
     * @return {@link String} the name of the check
     */
    String getName();

    /**
     * Whether the check was successful. A successful check means that the environment this
     * application is running in is more secure than otherwise, as opposed to signalling if a
     * certain feature was enabled
     *
     * For example, a check for whether the device is Rooted should return <code>true</code> when it
     * is *not* rooted, since this would be the more secure condition.
     *
     * @return <code>true</code> if the check was successful
     */
    boolean passed();
}
