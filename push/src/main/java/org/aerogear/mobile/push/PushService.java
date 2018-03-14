package org.aerogear.mobile.push;

import static java.net.HttpURLConnection.HTTP_OK;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.firebase.iid.FirebaseInstanceId;

import android.os.Handler;
import android.os.Looper;
import android.util.Base64;

import org.aerogear.mobile.core.Callback;
import org.aerogear.mobile.core.MobileCore;
import org.aerogear.mobile.core.ServiceModule;
import org.aerogear.mobile.core.configuration.ServiceConfiguration;
import org.aerogear.mobile.core.exception.HttpException;
import org.aerogear.mobile.core.http.HttpRequest;
import org.aerogear.mobile.core.http.HttpResponse;

public class PushService implements ServiceModule {

    private static final String PUSH_CONFIG_FILE_NAME = "push-config.json";
    private static final String JSON_OBJECT = "android";
    private static final String JSON_SENDER_ID = "senderID";
    private static final String JSON_VARIANT_ID = "variantID";
    private static final String JSON_VARIANT_SECRET = "variantSecret";

    private static final String registryDeviceEndpoint = "rest/registry/device";

    private static final List<MessageHandler> MAIN_THREAD_HANDLERS = new ArrayList<>();
    private static final List<MessageHandler> BACKGROUND_THREAD_HANDLERS = new ArrayList<>();

    private final String deviceType = "ANDROID";
    private final String operatingSystem = "android";
    private final String osVersion = android.os.Build.VERSION.RELEASE;

    private MobileCore core;
    private String url;
    private UnifiedPushCredentials unifiedPushCredentials;

    @Override
    public String type() {
        return "push";
    }

    @Override
    public void configure(MobileCore core, ServiceConfiguration serviceConfiguration) {
        this.core = core;
        this.url = serviceConfiguration.getUrl();
        loadConfigJson();
    }

    @Override
    public boolean requiresConfiguration() {
        return true;
    }

    @Override
    public void destroy() {}

    private void loadConfigJson() {
        InputStream fileStream = null;

        try {
            fileStream = core.getContext().getResources().getAssets().open(PUSH_CONFIG_FILE_NAME);
            int size = fileStream.available();
            byte[] buffer = new byte[size];
            fileStream.read(buffer);
            fileStream.close();
            String json = new String(buffer);

            JSONObject pushConfig = new JSONObject(json);
            JSONObject pushAndroidConfig = pushConfig.getJSONObject(JSON_OBJECT);
            UnifiedPushCredentials unifiedPushCredentials = new UnifiedPushCredentials();
            unifiedPushCredentials.setSender(pushAndroidConfig.getString(JSON_SENDER_ID));
            unifiedPushCredentials.setVariant(pushAndroidConfig.getString(JSON_VARIANT_ID));
            unifiedPushCredentials.setSecret(pushAndroidConfig.getString(JSON_VARIANT_SECRET));

            this.unifiedPushCredentials = unifiedPushCredentials;
        } catch (JSONException e) {
            MobileCore.getLogger().error(e.getMessage(), e);
            throw new RuntimeException("An error occurred while parsing the "
                            + PUSH_CONFIG_FILE_NAME + ". Please check the file format");
        } catch (IOException e) {
            MobileCore.getLogger().error(e.getMessage(), e);
            throw new RuntimeException("An error occurred while parsing the "
                            + PUSH_CONFIG_FILE_NAME + ". Please check if the file exists");
        } finally {
            if (fileStream != null) {
                try {
                    fileStream.close();
                } catch (IOException e) {
                    // Ignore IOException
                }
            }
        }
    }

    public void registerDevice(final Callback callback) {
        registerDevice(new UnifiedPushConfig(), callback);
    }

    public void registerDevice(final UnifiedPushConfig unifiedPushConfig, final Callback callback) {

        loadConfigJson();

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
            httpRequest.post(url + registryDeviceEndpoint, data.toString().getBytes());

            final HttpResponse httpResponse = httpRequest.execute();
            httpResponse.onSuccess(new Runnable() {
                @Override
                public void run() {
                    switch (httpResponse.getStatus()) {
                        case HTTP_OK:
                            callback.onSuccess();
                            break;
                        default:
                            callback.onError(new HttpException());
                            break;
                    }
                }
            });

        } catch (JSONException e) {
            MobileCore.getLogger().error(e.getMessage(), e);
            callback.onSuccess(e);
        }

    }

    private String getHashedAuth(String username, char[] password) {
        StringBuilder headerValueBuilder = new StringBuilder("Basic").append(" ");
        String unhashedCredentials = username + ":" + String.valueOf(password);
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
    public static void registerMainThreadHandler(MessageHandler handler) {
        MAIN_THREAD_HANDLERS.add(handler);
    }

    /**
     * When a push message is received, all background thread handlers will be notified on a non UI
     * thread. This should be used by classes which need to update internal state or preform some
     * action which doesn't change the UI.
     *
     * @param handler a handler to added to the list of handlers to be notified.
     */
    public static void registerBackgroundThreadHandler(MessageHandler handler) {
        BACKGROUND_THREAD_HANDLERS.add(handler);
    }

    /**
     * This will remove the given handler from the collection of main thread handlers. This MUST be
     * called when a Fragment or activity is backgrounded via onPause.
     *
     * @param handler a handler to be removed to the list of handlers
     */
    public static void unregisterMainThreadHandler(MessageHandler handler) {
        MAIN_THREAD_HANDLERS.remove(handler);
    }

    /**
     * This will remove the given handler from the collection of background thread handlers.
     *
     * @param handler a handler to be removed to the list of handlers
     */
    public static void unregisterBackgroundThreadHandler(MessageHandler handler) {
        BACKGROUND_THREAD_HANDLERS.remove(handler);
    }

    /**
     * This will deliver an message to all registered handlers.
     *
     * @param message the message to pass
     */
    public static void notifyHandlers(final Map<String, String> message) {

        for (final MessageHandler handler : BACKGROUND_THREAD_HANDLERS) {
            new Thread(new Runnable() {
                public void run() {
                    handler.onMessage(message);
                }
            }).start();
        }

        Looper main = Looper.getMainLooper();

        for (final MessageHandler handler : MAIN_THREAD_HANDLERS) {
            new Handler(main).post(new Runnable() {
                @Override
                public void run() {
                    handler.onMessage(message);
                }
            });
        }
    }

}
