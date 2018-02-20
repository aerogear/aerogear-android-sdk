package org.aerogear.mobile.security;

import org.aerogear.mobile.core.MobileCore;
import org.aerogear.mobile.core.ServiceModule;
import org.aerogear.mobile.core.configuration.ServiceConfiguration;
import org.aerogear.mobile.security.impl.SecurityCheckExecutorImpl;

public class SecurityService implements ServiceModule{
    private final static String TYPE = "security";

    private MobileCore core;

    @Override
    public String type() {
        return TYPE;
    }

    @Override
    public void configure(MobileCore core, ServiceConfiguration serviceConfiguration) {
        this.core = core;
    }

    @Override
    public void destroy() {}

    /**
     * Retrieve a {@link SecurityCheckExecutor} to run multiple {@link Check checks} chained.
     *
     * @return A new executor.
     */
    public SecurityCheckExecutor getCheckExecutor() {
        return new SecurityCheckExecutorImpl(core.getContext());
    }

    /**
     * Perform a single {@link Check} and get the {@link SecurityCheckResult result} for it.
     *
     * @param check The check to execute.
     * @return The result of the check.
     */
    public SecurityCheckResult check(Check check) {
        return check.getSecurityCheck().test(core.getContext());
    }
}
