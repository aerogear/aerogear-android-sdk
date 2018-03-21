package org.aerogear.mobile.core.configuration;

import static org.aerogear.mobile.core.utils.SanityCheck.nonNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * This class is responsible for consuming a reader and producing a tree of config values to be
 * consumed by modules.
 */
public class MobileCoreJsonConfig {

    private final Map<String, ServiceConfiguration> values = new TreeMap<>();
    private final Map<String, String> hashes = new HashMap<>();


    private MobileCoreJsonConfig(final InputStream jsonStream) throws IOException, JSONException {
        final String jsonText = readJsonStream(jsonStream);
        final JSONObject jsonDocument = new JSONObject(jsonText);
        parseMobileCoreArray(jsonDocument.getJSONArray("services"));
        if (jsonDocument.has("https")) {
            parseHttpsArray(jsonDocument.getJSONArray("https"));
        }
    }

    private String readJsonStream(final InputStream jsonStream) throws IOException {
        nonNull(jsonStream, "jsonStream");
        final StringBuilder builder = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(jsonStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line).append('\n');
            }
        }
        return builder.toString();
    }

    private void parseMobileCoreArray(final JSONArray array) throws JSONException, IOException {
        final int length = nonNull(array, "json array").length();
        for (int i = 0; i < length; i++) {
            parseConfigObject(array.getJSONObject(i));
        }
    }

    private void parseConfigObject(final JSONObject jsonObject) throws JSONException, IOException {
        nonNull(jsonObject, "jsonObject");

        final ServiceConfiguration.Builder serviceConfigBuilder =
                        ServiceConfiguration.newConfiguration();
        serviceConfigBuilder.setName(jsonObject.getString("name"));
        serviceConfigBuilder.setUrl(jsonObject.getString("url"));
        serviceConfigBuilder.setType(jsonObject.getString("type"));

        final JSONObject config = jsonObject.getJSONObject("config");
        final JSONArray namesArray = config.names();
        if (namesArray != null) {
            int namesSize = namesArray.length();
            for (int i = 0; i < namesSize; i++) {
                final String name = namesArray.getString(i);
                serviceConfigBuilder.addProperty(name, config.getString(name));
            }
        }
        final ServiceConfiguration serviceConfig = serviceConfigBuilder.build();
        values.put(serviceConfig.getName(), serviceConfig);
    }

    private void parseHttpsArray(final JSONArray array) throws JSONException, IOException {
        final int arrayLength = nonNull(array, "jsonArray").length();
        for (int i = 0; i < arrayLength; i++) {
            parseHttpsObject(array.getJSONObject(i));
        }
    }

    private void parseHttpsObject(final JSONObject jsonObject) throws JSONException, IOException {
        nonNull(jsonObject, "jsonObject");

        final HttpsConfiguration.Builder httpsConfigBuilder =
                        HttpsConfiguration.newHashConfiguration();
        httpsConfigBuilder.setHostName(jsonObject.getString("host"));
        httpsConfigBuilder.setCertificateHash(jsonObject.getString("certificateHash"));

        final HttpsConfiguration httpsConfiguration = httpsConfigBuilder.build();
        hashes.put(httpsConfiguration.getHostName(), httpsConfiguration.getCertificateHash());
    }

    /**
     * @param jsonStream a inputStream to for mobile-core.json. Please note that this should be
     *        managed by the calling core. The parser will not close the resource when it is
     *        finished.
     * @return MobileCoreJsonConfig
     * @throws IOException if reading the stream fails
     * @throws JSONException if the json document is malformed
     */
    public static MobileCoreJsonConfig produce(final InputStream jsonStream)
                    throws IOException, JSONException {
        MobileCoreJsonConfig jsonConfig = new MobileCoreJsonConfig(jsonStream);
        return jsonConfig;
    }

    public Map<String, ServiceConfiguration> getServicesConfig() {
        return Collections.unmodifiableMap(values);
    }

    public Map<String, String> getCertificatePinningHashes() {
        return Collections.unmodifiableMap(hashes);
    }
}
