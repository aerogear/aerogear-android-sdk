package org.aerogear.mobile.push;

import static java.net.HttpURLConnection.HTTP_OK;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.firebase.iid.FirebaseInstanceId;

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

    public void registerDeviceOnUnifiedPushServer(final Callback callback) {
        registerDeviceOnUnifiedPushServer(new UnifiedPushConfig(), callback);
    }

    public void registerDeviceOnUnifiedPushServer(UnifiedPushConfig unifiedPushConfig,
                    final Callback callback) {

        loadConfigJson();

        try {

            JSONObject data = new JSONObject();

            data.put("deviceToken", FirebaseInstanceId.getInstance().getToken());
            data.put("deviceType", deviceType);
            data.put("operatingSystem", operatingSystem);
            data.put("osVersion", osVersion);
            data.put("alias", unifiedPushConfig.getAlias());

            List<String> categories = unifiedPushConfig.getCategories();
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
            }).onComplete(new Runnable() {
                @Override
                public void run() {
                    MobileCore.getLogger().info("onComplete");
                }
            });

        } catch (JSONException e) {
            MobileCore.getLogger().error(e.getMessage(), e);
        }

    }

    private String getHashedAuth(String username, char[] password) {
        StringBuilder headerValueBuilder = new StringBuilder("Basic").append(" ");
        String unhashedCredentials = username + ":" + String.valueOf(password);
        String hashedCrentials =
                        Base64.encodeToString(unhashedCredentials.getBytes(), Base64.NO_WRAP);
        return headerValueBuilder.append(hashedCrentials).toString();
    }

}
