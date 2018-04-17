package org.aerogear.mobile.push;

import static java.net.HttpURLConnection.HTTP_NO_CONTENT;
import static java.net.HttpURLConnection.HTTP_OK;
import static org.aerogear.mobile.core.utils.SanityCheck.nonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Base64;

import org.aerogear.mobile.core.Callback;
import org.aerogear.mobile.core.MobileCore;
import org.aerogear.mobile.core.ServiceModule;
import org.aerogear.mobile.core.configuration.ServiceConfiguration;
import org.aerogear.mobile.core.exception.ConfigurationNotFoundException;
import org.aerogear.mobile.core.exception.HttpException;
import org.aerogear.mobile.core.executor.AppExecutors;
import org.aerogear.mobile.core.http.HttpRequest;
import org.aerogear.mobile.core.http.HttpResponse;
import org.aerogear.mobile.core.reactive.Responder;

/**
 * The entry point for communication with Unified Push Server
 */
public class PushService implements ServiceModule {

    private static final String DEFAULT_MESSAGE_HANDLER_KEY = "DEFAULT_MESSAGE_HANDLER_KEY";

    private static final String registryDeviceEndpoint = "rest/registry/device";
    private static final String JSON_ANDROID_CONFIG_KEY = "android";
    private static final String JSON_VARIANT_ID_KEY = "variantId";
    private static final String JSON_VARIANT_SECRET_KEY = "variantSecret";
    private static final String JSON_SENDER_ID_KEY = "senderId";


    private static final List<MessageHandler> MAIN_THREAD_HANDLERS =
                    Collections.synchronizedList(new ArrayList<>());
    private static final List<MessageHandler> BACKGROUND_THREAD_HANDLERS =
                    Collections.synchronizedList(new ArrayList<>());

    private final String deviceType = "ANDROID";
    private final String operatingSystem = "android";
    private final String osVersion = android.os.Build.VERSION.RELEASE;

    private MobileCore core;
    private String url;
    private UnifiedPushCredentials unifiedPushCredentials;

    private static MessageHandler defaultHandler;

    @Override
    public String type() {
        return "push";
    }

    @Override
    public void configure(MobileCore core, ServiceConfiguration serviceConfiguration) {
        this.core = core;
        this.url = serviceConfiguration.getUrl();
        getDefaultHandler(core.getContext());

        try {
            JSONObject android = new JSONObject(
                            serviceConfiguration.getProperties().get(JSON_ANDROID_CONFIG_KEY));

            unifiedPushCredentials = new UnifiedPushCredentials();
            unifiedPushCredentials.setVariant(android.getString(JSON_VARIANT_ID_KEY));
            unifiedPushCredentials.setSecret(android.getString(JSON_VARIANT_SECRET_KEY));
            unifiedPushCredentials.setSenderId(android.getString(JSON_SENDER_ID_KEY));

        } catch (JSONException e) {
            MobileCore.getLogger().error(e.getMessage(), e);
            throw new ConfigurationNotFoundException(
                            "An error occurred while trying to load the push config");
        }
    }

    @Override
    public boolean requiresConfiguration() {
        return true;
    }

    @Override
    public void destroy() {}

    /**
     * Register the device on Unified Push Server
     *
     * @param callback A callback to handle successful or failed registration
     */
    public void registerDevice(final Callback callback) {
        registerDevice(new UnifiedPushConfig(), callback);
    }

    /**
     * Register the device on Unified Push Server
     *
     * @param unifiedPushConfig Unified Push configuration to be send to the Unified Push Server
     * @param callback A callback to handle successful or failed registration
     */
    public void registerDevice(final UnifiedPushConfig unifiedPushConfig, final Callback callback) {

        try {

            JSONObject data = new JSONObject();

            data.put("deviceToken", FirebaseInstanceId.getInstance().getToken());
            data.put("deviceType", deviceType);
            data.put("operatingSystem", operatingSystem);
            data.put("osVersion", osVersion);
            data.put("alias", unifiedPushConfig.getAlias());

            final List<String> categories = unifiedPushConfig.getCategories();
            if (categories != null && !categories.isEmpty()) {
                JSONArray jsonCategories = new JSONArray();
                for (String category : categories) {
                    jsonCategories.put(category);
                }
                data.put("categories", jsonCategories);
            }

            String authHash = getHashedAuth(unifiedPushCredentials.getVariant(),
                            unifiedPushCredentials.getSecret().toCharArray());

            final HttpRequest httpRequest = core.getHttpLayer().newRequest();
            httpRequest.addHeader("Authorization", authHash);
            httpRequest.post(url + registryDeviceEndpoint, data.toString().getBytes())
                            .respondWith(new Responder<HttpResponse>() {
                                @Override
                                public void onResult(HttpResponse httpResponse) {
                                    switch (httpResponse.getStatus()) {
                                        case HTTP_OK:

                                            FirebaseMessaging firebaseMessaging =
                                                            FirebaseMessaging.getInstance();

                                            if (categories != null) {
                                                for (String catgory : categories) {
                                                    firebaseMessaging.subscribeToTopic(catgory);
                                                }
                                            }

                                            firebaseMessaging.subscribeToTopic(
                                                            unifiedPushCredentials.getVariant());

                                            callback.onSuccess();
                                            break;
                                        default:
                                            callback.onError(new HttpException(
                                                            httpResponse.getStatus()));
                                            break;
                                    }
                                }


                                @Override
                                public void onException(Exception error) {
                                    MobileCore.getLogger().error(error.getMessage(), error);
                                    callback.onError(error);
                                }
                            });

        } catch (JSONException e) {
            MobileCore.getLogger().error(e.getMessage(), e);
            callback.onError(e);
        }

    }

    /**
     * Unregister the device on Unified Push Server
     *
     * @param callback A callback to handle successful or failed unregistration
     */
    public void unregisterDevice(final Callback callback) {
        unregisterDevice(new UnifiedPushConfig(), callback);
    }

    /**
     * Unregister the device on Unified Push Server
     *
     * @param unifiedPushConfig Unified Push configuration to be send to the Unified Push Server
     * @param callback A callback to handle successful or failed unregistration
     */
    public void unregisterDevice(final UnifiedPushConfig unifiedPushConfig,
                    final Callback callback) {

        String authHash = getHashedAuth(unifiedPushCredentials.getVariant(),
                        unifiedPushCredentials.getSecret().toCharArray());

        final HttpRequest httpRequest = core.getHttpLayer().newRequest();
        httpRequest.addHeader("Authorization", authHash);
        httpRequest.delete(url + registryDeviceEndpoint + "/"
                        + FirebaseInstanceId.getInstance().getToken())
                        .respondWith(new Responder<HttpResponse>() {
                            @Override
                            public void onResult(HttpResponse httpResponse) {
                                switch (httpResponse.getStatus()) {
                                    case HTTP_NO_CONTENT:

                                        FirebaseMessaging firebaseMessaging =
                                                        FirebaseMessaging.getInstance();

                                        for (String category : unifiedPushConfig.getCategories()) {
                                            firebaseMessaging.unsubscribeFromTopic(category);
                                        }

                                        firebaseMessaging.unsubscribeFromTopic(
                                                        unifiedPushCredentials.getVariant());

                                        callback.onSuccess();
                                        break;
                                    default:
                                        callback.onError(new HttpException(
                                                        httpResponse.getStatus()));
                                        break;
                                }
                            }


                            @Override
                            public void onException(Exception error) {
                                MobileCore.getLogger().error(error.getMessage(), error);
                                callback.onError(error);
                            }
                        });

    }

    /**
     * Provide an auth hash to be used to authenticate in Unified Push Service
     *
     * @param variant Unified Push variant id
     * @param secret Unified Push variant secret
     * @return Auth hash
     */
    private String getHashedAuth(final String variant, final char[] secret) {
        StringBuilder headerValueBuilder = new StringBuilder("Basic").append(" ");
        String unhashedCredentials = variant + ":" + String.valueOf(secret);
        String hashedCrentials =
                        Base64.encodeToString(unhashedCredentials.getBytes(), Base64.NO_WRAP);
        return headerValueBuilder.append(hashedCrentials).toString();
    }


    /**
     * When a push message is received, all main thread handlers will be notified on the main(UI)
     * thread. This is very convenient for Activities and Fragments.
     *
     * @param handler a handler to added to the list of handlers to be notified.
     */
    public static void registerMainThreadHandler(final MessageHandler handler) {
        MAIN_THREAD_HANDLERS.add(nonNull(handler, "handle"));
    }

    /**
     * When a push message is received, all background thread handlers will be notified on a non UI
     * thread. This should be used by classes which need to update internal state or preform some
     * action which doesn't change the UI.
     *
     * @param handler a handler to added to the list of handlers to be notified.
     */
    public static void registerBackgroundThreadHandler(final MessageHandler handler) {
        BACKGROUND_THREAD_HANDLERS.add(nonNull(handler, "handle"));
    }

    /**
     * This will remove the given handler from the collection of main thread handlers. This MUST be
     * called when a Fragment or activity is backgrounded via onPause.
     *
     * @param handler a handler to be removed to the list of handlers
     */
    public static void unregisterMainThreadHandler(final MessageHandler handler) {
        MAIN_THREAD_HANDLERS.remove(nonNull(handler, "handle"));
    }

    /**
     * This will remove the given handler from the collection of background thread handlers.
     *
     * @param handler a handler to be removed to the list of handlers
     */
    public static void unregisterBackgroundThreadHandler(final MessageHandler handler) {
        BACKGROUND_THREAD_HANDLERS.remove(nonNull(handler, "handle"));
    }

    /**
     * Notify all registered handlers.
     *
     * @param context the Android context
     * @param message the push message
     */
    public static void notifyHandlers(final Context context, final Map<String, String> message) {
        nonNull(context, "context");

        if (defaultHandler == null) {
            getDefaultHandler(context);
        }

        if (BACKGROUND_THREAD_HANDLERS.isEmpty() && MAIN_THREAD_HANDLERS.isEmpty()
                        && defaultHandler != null) {
            new AppExecutors().singleThreadService().execute(() -> defaultHandler.onMessage(context,
                            Collections.unmodifiableMap(message)));
        } else {

            for (final MessageHandler handler : BACKGROUND_THREAD_HANDLERS) {
                new AppExecutors().singleThreadService().execute(() -> handler.onMessage(context,
                                Collections.unmodifiableMap(message)));
            }

            for (final MessageHandler handler : MAIN_THREAD_HANDLERS) {
                new AppExecutors().mainThread().execute(() -> handler.onMessage(context,
                                Collections.unmodifiableMap(message)));
            }

        }

    }

    /**
     * Get a default handler from AndroidManifest.xml if exists
     *
     * <code>
     * <meta-data
     *   android:name="DEFAULT_MESSAGE_HANDLER_KEY" android:value="my.package.HandlerClassName" />
     * </code>
     *
     */
    @SuppressWarnings("unchecked")
    private static void getDefaultHandler(final Context context) {
        nonNull(context, "context");

        try {

            ApplicationInfo applicationInfo = context.getPackageManager().getApplicationInfo(
                            context.getPackageName(), PackageManager.GET_META_DATA);

            Bundle metaData = applicationInfo.metaData;

            if (metaData != null) {

                String defaultHandlerClassName = metaData.getString(DEFAULT_MESSAGE_HANDLER_KEY);
                if (defaultHandlerClassName != null) {
                    try {
                        Class<? extends MessageHandler> defaultHandlerClass =
                                        (Class<? extends MessageHandler>) Class
                                                        .forName(defaultHandlerClassName);
                        defaultHandler = defaultHandlerClass.newInstance();
                    } catch (Exception ex) {
                        MobileCore.getLogger().error(ex.getMessage(), ex);
                    }
                }

            }

        } catch (PackageManager.NameNotFoundException e) {
            MobileCore.getLogger().warning(e.getMessage(), e);
        }

    }

}
