package org.aerogear.mobile.core.configuration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.TreeMap;

/**
 * This class is responsible for consuming a reader and producing a tree of config values to be
 * consumed by modules.
 */
public class MobileCoreJsonParser {

    private final Map<String, ServiceConfiguration> values = new TreeMap<>();

    private MobileCoreJsonParser(final InputStream jsonStream) throws IOException, JSONException {
        final String jsonText = readJsonStream(jsonStream);
        final JSONObject jsonDocument = new JSONObject(jsonText);
        parseMobileCoreArray(jsonDocument.getJSONArray("services"));
    }

    private String readJsonStream(final InputStream jsonStream) throws IOException {
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
        final int length = array.length();
        for (int i = 0; i < length; i++) {
            parseConfigObject(array.getJSONObject(i));
        }
    }

    private void parseConfigObject(final JSONObject object) throws JSONException, IOException {
        final ServiceConfiguration.Builder serviceConfigBuilder = ServiceConfiguration.newConfiguration();
        serviceConfigBuilder.setName(object.getString("name"));
        serviceConfigBuilder.setUrl(object.getString("url"));
        serviceConfigBuilder.setType(object.getString("type"));

        final JSONObject config = object.getJSONObject("config");
        final JSONArray namesArray = config.names();
        if(namesArray!=null){
            int namesSize = namesArray.length();
            for (int i = 0; i < namesSize; i++) {
                final String name = namesArray.getString(i);
                serviceConfigBuilder.addProperty(name, config.getString(name));
            }
        }
        final ServiceConfiguration serviceConfig = serviceConfigBuilder.build();
        values.put(serviceConfig.getName(), serviceConfig);

    }

    /**
     * @param jsonStream a inputStream to for mobile-core.json.  Please note that this
     *                   should be managed by the calling core.  The parser will not close the resource
     *                   when it is finished.
     *
     * @return A map of ServiceConfigs mapped by their name.
     * @throws IOException   if reading the stream fails
     * @throws JSONException if the json document is malformed
     */
    public static Map<String, ServiceConfiguration> parse(final InputStream jsonStream) throws IOException, JSONException {
        MobileCoreJsonParser parser = new MobileCoreJsonParser(jsonStream);
        return parser.values;
    }
}
