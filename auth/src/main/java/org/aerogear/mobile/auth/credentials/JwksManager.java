package org.aerogear.mobile.auth.credentials;


import android.content.Context;
import android.content.SharedPreferences;

import org.aerogear.mobile.auth.configuration.AuthServiceConfiguration;
import org.aerogear.mobile.auth.configuration.KeycloakConfiguration;
import org.aerogear.mobile.core.executor.AppExecutors;
import org.aerogear.mobile.core.http.HttpRequest;
import org.aerogear.mobile.core.http.HttpResponse;
import org.aerogear.mobile.core.http.HttpServiceModule;
import org.aerogear.mobile.core.logging.Logger;
import org.jose4j.jwk.JsonWebKeySet;
import org.jose4j.lang.JoseException;

import java.util.Date;

/**
 * A class that is responsible for manage the Json Web Key Set(JWKS).
 */
class JwksManager {

    private static final int MILLISECONDS_PER_MINUTE = 60*1000;

    private static final String STORE_NAME = "org.aerogear.mobile.auth.JwksStore";

    private static final String ENTRY_SUFFIX_FOR_KEY_CONTENT = "jwks_content";
    private static final String ENTRY_SUFFIX_FOR_REQUEST_DATE = "requested_date";

    private HttpServiceModule httpModule;
    private AuthServiceConfiguration authServiceConfiguration;
    private SharedPreferences sharedPrefs;
    private Logger logger;

    JwksManager(Context context, HttpServiceModule httpModule, AuthServiceConfiguration authServiceConfiguration, Logger logger) {
        this.httpModule = httpModule;
        this.authServiceConfiguration = authServiceConfiguration;
        this.sharedPrefs = context.getSharedPreferences(STORE_NAME, Context.MODE_PRIVATE);
        this.logger = logger;
    }

    /**
     * Load the cached JWKS from the private storage of the app.
     * It will return null if there is no cached JWKS found.
     * It will trigger a request to fetch the JWKS in the background if there is no cached key found, or {@link AuthServiceConfiguration#getMinTimeBetweenJwksRequests()} is passed since the key set is requested last time.
     * @return the cached JWKS, or null if it doesn't exist
     */
    public JsonWebKeySet load(KeycloakConfiguration keyCloakConfig) {
        boolean needFetchNow = true;
        JsonWebKeySet jwks = null;
        String namespace = keyCloakConfig.getRealmName();
        String keyContentEntryName = buildEntryNameForKeyContent(namespace);
        String keyContent = this.sharedPrefs.getString(keyContentEntryName, null);
        if (keyContent != null) {
            try {
                jwks = new JsonWebKeySet(keyContent);
                needFetchNow = false;
            } catch (JoseException e) {
                logger.error("failed to parse JsonWebKeySet content", e);
            }
        }
        fetchJwksIfNeeded(keyCloakConfig, needFetchNow);
        return jwks;
    }

    /**
     * Fetch the JWKS from the server if necessary and save them locally.
     * The request will be trigger if:
     * 1. forceFetch is set to true, or
     * 2. {@link AuthServiceConfiguration#getMinTimeBetweenJwksRequests()} is passed since the key set is requested last time.
     * @param keycloakConfiguration the configuration of the keycloak server
     * @param forceFetch if set to true, the request will be trigger immediately.
     */
    public void fetchJwksIfNeeded(KeycloakConfiguration keycloakConfiguration, boolean forceFetch) {
        if (forceFetch || shouldRequestJwks(keycloakConfiguration)) {
            String jwksUrl = keycloakConfiguration.getJwksUrl();
            HttpRequest getRequest = httpModule.newRequest();
            getRequest.get(jwksUrl);
            HttpResponse response = getRequest.execute();
            response.onComplete(() -> {
                //this is invoked on a background thread.
                if (response.getStatus() == 200) {
                    String jwksContent = response.stringBody();
                    boolean isValidKeyset = true;
                    try {
                        new JsonWebKeySet(jwksContent);
                    } catch (JoseException e) {
                        isValidKeyset = false;
                        logger.warning("failed to parse the content of JWKS key. content = " + jwksContent);
                    }
                    if (isValidKeyset) {
                        persistJwksContent(keycloakConfiguration.getRealmName(), jwksContent);
                    }
                } else {
                    logger.warning("failed to fetch JWKS from server. url = " + jwksUrl + " statusCode = " + response.getStatus());
                }
            });
        }
    }

    /**
     * Check when the JWKS was requested last time and determine if a request should be sent again.
     * @param keyCloakConfig the configuration of the Keycloak server
     * @return true if the request should be triggered
     */
    private boolean shouldRequestJwks(KeycloakConfiguration keyCloakConfig) {
        boolean shouldRequest = true;
        String namespace = keyCloakConfig.getRealmName();
        String requestedDateEntryName = buildEntryNameForQuestedDate(namespace);
        long lastRequestDate = this.sharedPrefs.getLong(requestedDateEntryName, 0);
        long currentTime = new Date().getTime();
        long duration = currentTime - lastRequestDate;
        if (duration < this.authServiceConfiguration.getMinTimeBetweenJwksRequests() * MILLISECONDS_PER_MINUTE) {
            shouldRequest = false;
        }
        return shouldRequest;
    }

    /**
     * Save the JWKS content for the given name space locally using SharedPreferences.
     * @param namespace the namespace associated with the JWKS
     * @param jwksContent the content of the JWKS
     */
    private void persistJwksContent(String namespace, String jwksContent) {
        if (jwksContent != null && !jwksContent.isEmpty()) {
            long timeFetched = new Date().getTime();
            SharedPreferences.Editor editor = this.sharedPrefs.edit();
            editor.putString(buildEntryNameForKeyContent(namespace), jwksContent)
                .putLong(buildEntryNameForQuestedDate(namespace), timeFetched);
            if (!editor.commit()) {
                logger.warning("failed to persist JWKS content");
            }
        }
    }

    /**
     * Build the entry name for the JWKS content
     * @param namespace the namespace associated with the JWKS
     * @return the full entry name
     */
    private String buildEntryNameForKeyContent(String namespace) {
        return String.format("%s_%s", namespace, ENTRY_SUFFIX_FOR_KEY_CONTENT);
    }

    /**
     * Build the entry name for the last requested date for the JWKS content
     * @param namespace the namespace associated with the JWKS
     * @return the full entry name
     */
    private String buildEntryNameForQuestedDate(String namespace) {
        return String.format("%s_%s", namespace, ENTRY_SUFFIX_FOR_REQUEST_DATE);
    }

}
