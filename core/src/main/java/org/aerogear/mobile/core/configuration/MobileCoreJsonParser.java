package org.aerogear.mobile.core.configuration;

import static org.aerogear.mobile.core.utils.SanityCheck.nonNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import org.aerogear.mobile.core.configuration.https.CertificatePinningEntry;
import org.aerogear.mobile.core.configuration.https.HttpsConfiguration;

/**
 * This class is responsible for consuming a reader and producing a tree of config values to be
 * consumed by modules.
 */
public final class MobileCoreJsonParser {

    private final InputStream jsonStream;

    public MobileCoreJsonParser(final InputStream jsonStream) {
        this.jsonStream = jsonStream;
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

    private Map<String, ServiceConfiguration> parseMobileCoreArray(final JSONArray array)
                    throws JSONException, IOException {
        final int length = nonNull(array, "json array").length();
        Map<String, ServiceConfiguration> serviceConfigs = new HashMap<>();
        for (int i = 0; i < length; i++) {
            ServiceConfiguration serviceConfig = parseConfigObject(array.getJSONObject(i));
            serviceConfigs.put(serviceConfig.getType(), serviceConfig);
        }
        return serviceConfigs;
    }

    private ServiceConfiguration parseConfigObject(final JSONObject jsonObject)
                    throws JSONException, IOException {
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
        return serviceConfigBuilder.build();
    }

    private HttpsConfiguration parseHttpsConfig(final JSONObject httpsJsonConfig)
                    throws JSONException, IOException {
        HttpsConfiguration.Builder configBuilder = HttpsConfiguration.newBuilder();

        if (httpsJsonConfig != null && httpsJsonConfig.has(HttpsConfiguration.CERT_PINNING_KEY)) {
            List<CertificatePinningEntry> certPinningConfig = getCertPinningConfig(
                            httpsJsonConfig.getJSONArray(HttpsConfiguration.CERT_PINNING_KEY));
            configBuilder.setCertPinningConfig(certPinningConfig);
        }
        return configBuilder.build();
    }

    private List<CertificatePinningEntry> getCertPinningConfig(
                    final JSONArray certPinningJsonConfig) throws JSONException, IOException {
        final int arrayLength = nonNull(certPinningJsonConfig, "jsonArray").length();

        List<CertificatePinningEntry> pinningConfig = new ArrayList<>();
        for (int i = 0; i < arrayLength; i++) {
            JSONObject pinningJsonEntry = certPinningJsonConfig.getJSONObject(i);
            CertificatePinningEntry pinningEntry =
                            new CertificatePinningEntry(pinningJsonEntry.getString("host"),
                                            pinningJsonEntry.getString("certificateHash"));
            pinningConfig.add(pinningEntry);
        }
        return pinningConfig;
    }

    /**
     * @return MobileCoreJsonConfig
     * @throws IOException if reading the stream fails
     * @throws JSONException if the json document is malformed
     */
    public MobileCoreConfiguration parse() throws IOException, JSONException {
        JSONObject jsonConfig = new JSONObject(readJsonStream(jsonStream));
        JSONArray servicesJson = jsonConfig.getJSONArray("services");
        JSONObject httpsJson = jsonConfig.optJSONObject("https");

        return MobileCoreConfiguration.newBuilder()
                        .setHttpsConfiguration(parseHttpsConfig(httpsJson))
                        .setServiceConfiguration(parseMobileCoreArray(servicesJson)).build();
    }
}
