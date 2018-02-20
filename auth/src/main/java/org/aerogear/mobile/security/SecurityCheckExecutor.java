package org.aerogear.mobile.security;

public interface SecurityCheckExecutor {
    /**
     * Add a {@link Check check} to be executed on {@link #execute()}.
     *
     * @param check The check to add.
     * @return {@link SecurityCheckExecutor}
     */
    SecurityCheckExecutor addCheck(Check check);

    /**
     * Return the results of each test that was added to the executor.
     *
     * @return Array of {@link SecurityCheckResult results}
     */
    SecurityCheckResult[] execute();
}
