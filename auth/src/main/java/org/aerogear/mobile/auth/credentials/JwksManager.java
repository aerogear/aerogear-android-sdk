package org.aerogear.mobile.auth.credentials;

import static org.aerogear.mobile.core.utils.SanityCheck.nonNull;

import java.util.Date;

import org.jose4j.jwk.JsonWebKeySet;
import org.jose4j.lang.JoseException;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.aerogear.mobile.auth.configuration.AuthServiceConfiguration;
import org.aerogear.mobile.auth.configuration.KeycloakConfiguration;
import org.aerogear.mobile.core.Callback;
import org.aerogear.mobile.core.MobileCore;
import org.aerogear.mobile.core.http.HttpRequest;
import org.aerogear.mobile.core.http.HttpResponse;
import org.aerogear.mobile.core.http.HttpServiceModule;
import org.aerogear.mobile.core.logging.Logger;
import org.aerogear.mobile.core.reactive.Responder;

/**
 * A class that is responsible for manage the Json Web Key Set(JWKS).
 */
public class JwksManager {
    public static final String TAG = "JWKSMANAGER";
    private static final Logger LOGGER = MobileCore.getLogger();
    private static final int MILLISECONDS_PER_MINUTE = 60 * 1000;
    private static final String STORE_NAME = "org.aerogear.mobile.auth.JwksStore";
    private static final String ENTRY_SUFFIX_FOR_KEY_CONTENT = "jwks_content";
    private static final String ENTRY_SUFFIX_FOR_REQUEST_DATE = "requested_date";

    private final HttpServiceModule httpModule;
    private final AuthServiceConfiguration authServiceConfiguration;
    private final SharedPreferences sharedPrefs;

    public JwksManager(@NonNull final Context context, @NonNull final MobileCore mobileCore,
                    @NonNull final AuthServiceConfiguration authServiceConfiguration) {
        this.httpModule = nonNull(mobileCore, "mobileCore").getHttpLayer();
        this.authServiceConfiguration =
                        nonNull(authServiceConfiguration, "authServiceConfiguration");
        this.sharedPrefs = nonNull(context, "context").getSharedPreferences(STORE_NAME,
                        Context.MODE_PRIVATE);
    }

    /**
     * Load the cached JWKS from the private storage of the app. It will return null if there is no
     * cached JWKS found. It will trigger a request to fetch the JWKS in the background if there is
     * no cached key found, or {@link AuthServiceConfiguration#getMinTimeBetweenJwksRequests()} is
     * passed since the key set is requested last time.
     *
     * @param keyCloakConfig the configuration to use to load the JWKS object
     *
     * @return the cached JWKS, or null if it doesn't exist
     */
    public JsonWebKeySet load(final KeycloakConfiguration keyCloakConfig) {
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
                LOGGER.error("failed to parse JsonWebKeySet content", e);
            }
        }
        fetchJwksIfNeeded(keyCloakConfig, needFetchNow);
        return jwks;
    }

    /**
     * Fetch the JWKS from the server if necessary and save them locally. The request will be
     * trigger if: 1. forceFetch is set to true, or 2.
     * {@link AuthServiceConfiguration#getMinTimeBetweenJwksRequests()} is passed since the key set
     * is requested last time.
     *
     * @param keycloakConfiguration the configuration of the keycloak server
     * @param forceFetch if set to true, the request will be trigger immediately.
     * @return whether the keys has been fetched or not.
     */
    public boolean fetchJwksIfNeeded(final KeycloakConfiguration keycloakConfiguration,
                    final boolean forceFetch) {
        if (forceFetch || shouldRequestJwks(keycloakConfiguration)) {
            fetchJwks(keycloakConfiguration, null);
            return true;
        }

        return false;
    }

    /**
     * Call the remote endpoint to load the JWKS and save it locally.
     *
     * @param keycloakConfiguration the configuration of the keycloak server
     * @param callback the callback function to be invoked when the request is completed. Can be
     *        null.
     */
    public void fetchJwks(@NonNull final KeycloakConfiguration keycloakConfiguration,
                    @Nullable final Callback<JsonWebKeySet> callback) {
        String jwksUrl = nonNull(keycloakConfiguration, "keycloakConfiguration").getJwksUrl();
        HttpRequest getRequest = httpModule.newRequest();
        getRequest.get(jwksUrl).respondWith(new Responder<HttpResponse>() {


            @Override
            public void onResult(HttpResponse response) {
                JsonWebKeySet jwks = null;
                JwksException error = null;
                // this is invoked on a background thread.
                if (response.getStatus() == 200) {
                    String jwksContent = response.stringBody();
                    try {
                        jwks = new JsonWebKeySet(jwksContent);
                    } catch (JoseException e) {
                        jwks = null;
                        error = new JwksException(e);
                        LOGGER.warning("failed to parse JWKS key content. content = "
                                        + jwksContent);
                    }
                    if (jwks != null) {
                        persistJwksContent(keycloakConfiguration.getRealmName(), jwksContent);
                    }
                } else {
                    LOGGER.warning("failed to fetch JWKS from server. url = " + jwksUrl
                                    + " statusCode = " + response.getStatus());
                    error = new JwksException("failed to fetch JWKS from server");
                }
                if (callback != null) {
                    if (jwks != null) {
                        callback.onSuccess(jwks);
                    } else {
                        callback.onError(error);
                    }
                }
            }

            @Override
            public void onException(Exception exception) {
                LOGGER.error(TAG, exception.getMessage(), exception);
            }
        });

    }

    /**
     * Check when the JWKS was requested last time and determine if a request should be sent again.
     *
     * @param keyCloakConfig the configuration of the Keycloak server
     * @return true if the request should be triggered
     */
    private boolean shouldRequestJwks(final KeycloakConfiguration keyCloakConfig) {
        boolean shouldRequest = true;
        String namespace = keyCloakConfig.getRealmName();
        String requestedDateEntryName = buildEntryNameForQuestedDate(namespace);
        long lastRequestDate = this.sharedPrefs.getLong(requestedDateEntryName, 0);
        long currentTime = System.currentTimeMillis();
        long duration = currentTime - lastRequestDate;
        if (duration < this.authServiceConfiguration.getMinTimeBetweenJwksRequests()
                        * MILLISECONDS_PER_MINUTE) {
            shouldRequest = false;
        }
        return shouldRequest;
    }

    /**
     * Save the JWKS content for the given name space locally using SharedPreferences.
     *
     * @param namespace the namespace associated with the JWKS
     * @param jwksContent the content of the JWKS
     */
    private void persistJwksContent(final String namespace, final String jwksContent) {
        if (jwksContent != null && !jwksContent.isEmpty()) {
            long timeFetched = new Date().getTime();
            SharedPreferences.Editor editor = this.sharedPrefs.edit();
            editor.putString(buildEntryNameForKeyContent(namespace), jwksContent)
                            .putLong(buildEntryNameForQuestedDate(namespace), timeFetched);
            if (!editor.commit()) {
                LOGGER.warning("failed to persist JWKS content");
            }
        }
    }

    /**
     * Build the entry name for the JWKS content
     *
     * @param namespace the namespace associated with the JWKS
     * @return the full entry name
     */
    private String buildEntryNameForKeyContent(final String namespace) {
        return String.format("%s_%s", namespace, ENTRY_SUFFIX_FOR_KEY_CONTENT);
    }

    /**
     * Build the entry name for the last requested date for the JWKS content
     *
     * @param namespace the namespace associated with the JWKS
     * @return the full entry name
     */
    private String buildEntryNameForQuestedDate(final String namespace) {
        return String.format("%s_%s", namespace, ENTRY_SUFFIX_FOR_REQUEST_DATE);
    }

}
