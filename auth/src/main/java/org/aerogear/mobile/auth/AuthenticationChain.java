package org.aerogear.mobile.auth;

import org.aerogear.mobile.auth.credentials.ICredential;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Authenticate the user by executing the authentication chain.
 * Stops at the first successful login or at the first failing mandatory authenticator.
 * At least one authenticator must be successful.
 */
public class AuthenticationChain {

    /**
     * Executor used to execute the callables.
     */
    private final ExecutorService executorService = Executors.newFixedThreadPool(10);

    /**
     * Authentication chain rings.
     */
    private final List<AuthenticationRing> chain;

    /**
     * Object representing a ring in the authentication chain.
     */
    private static class AuthenticationRing {

        /**
         * The authenticator to be run when this ring is executed.
         */
        private final AbstractAuthenticator authenticator;

        /**
         * If the authenticator must succeed or if it is optional.
         */
        private final boolean mandatory;

        /**
         * Builds a new authenticator ring.
         *
         * @param authenticator the authenticator to be run as part of this ring execution
         * @param mandatory whether the authenticator is mandatory or optional
         */
        public AuthenticationRing(final AbstractAuthenticator authenticator, final boolean mandatory) {
            this.authenticator = authenticator;
            this.mandatory = mandatory;
        }

        /**
         * Builds a new authenticator ring with an optional authenticator.
         *
         * @param authenticator
         */
        public AuthenticationRing(final AbstractAuthenticator authenticator) {
            this.authenticator = authenticator;
            this.mandatory = false;
        }

        public boolean isMandatory() {
            return mandatory;
        }

        public AbstractAuthenticator getAuthenticator() {
            return authenticator;
        }

        public Principal authenticate(final ICredential credential) throws AuthenticationException {
            try {
                return authenticator.authenticate(credential);
            } catch (AuthenticationException ae) {
                if (isMandatory()) {
                    throw new AuthenticationException(ae);
                }
            }

            return null;
        }
    }

    /**
     * Builds an authenticator chain.
     */
    public static class Builder {
        private List<AuthenticationRing> chain = new ArrayList<>();
        private ExecutorService executorService = Executors.newFixedThreadPool(10);

        private Builder(){
        }

        /**
         * Adds an optional authenticator to the chain.
         * @param authenticator authenticator to be added
         * @return this
         */
        public Builder with(AbstractAuthenticator authenticator) {
            chain.add(new AuthenticationRing(authenticator));
            return this;
        }

        /**
         * Adds an authenticator to the chain.
         *
         * @param authenticator the authenticator to be added
         * @param mandatory whether the authenticator is mandatory or not
         * @return this
         */
        public Builder with(AbstractAuthenticator authenticator, boolean mandatory) {
            chain.add(new AuthenticationRing(authenticator, mandatory));
            return this;
        }

        /**
         * Replace the default executor service. The executor service is used to execute the authenticators in an
         * asynchronous manner.
         * The default executor uses 10 threads.
         *
         * @param executorService the new executor service
         * @return this
         */
        public Builder with(ExecutorService executorService) {
            this.executorService = executorService;
            return this;
        }

        /**
         * Builds the authenticators chain as configured with the 'with' methods.
         * @return this
         */
        public AuthenticationChain build() {
            return new AuthenticationChain(this.chain, executorService);
        }

    }

    private AuthenticationChain(final List<AuthenticationRing> chain, ExecutorService executorService) {
        this.chain = chain;
    }

    /**
     * Authenticates the given credential
     * @param credential
     * @return
     */
    public Future<Principal> authenticate(final ICredential credential) {

        Callable<Principal> authenticateCallable = new Callable<Principal>() {
            @Override
            public Principal call() throws Exception {
                for(AuthenticationRing ring : chain) {
                    Principal principal = ring.authenticate(credential);
                    if (principal != null) {
                        return principal;
                    }

                    if (ring.isMandatory()) {
                        throw new AuthenticationException("Authentication failed");
                    }
                }

                throw new AuthenticationException("Authentication failed");
            }
        };

        return executorService.submit(authenticateCallable);
    }

    public Future<Void> logout(final Principal principal) {

        Callable<Void> authenticateCallable = new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                if (principal instanceof AbstractPrincipal) {
                    ((AbstractPrincipal) principal).getAuthenticator().logout(principal);
                }
                return null;
            }
        };

        return executorService.submit(authenticateCallable);
    }


    public static Builder newChain() {
        return new Builder();
    }
}
