package org.aerogear.mobile.core.configuration;

import android.util.JsonReader;
import android.util.JsonToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

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

    private TreeMap<String, ServiceConfiguration> values = new TreeMap<>();

    private MobileCoreJsonParser(InputStream jsonStream) throws IOException, JSONException {
        String jsonText = readJsonStream(jsonStream);
        JSONObject jsonDocument = new JSONObject(jsonText);
        parseMobileCoreArray(jsonDocument.getJSONArray("services"));
    }

    private String readJsonStream(InputStream jsonStream) throws IOException {
        String out = "";
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(jsonStream))) {
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line).append('\n');
            }
            out = builder.toString();
        }
        return out;
    }

    private void parseMobileCoreArray(JSONArray array) throws JSONException, IOException {
        int length = array.length();
        for (int i = 0; i < length; i++) {
            parseConfigObject(array.getJSONObject(i));
        }
    }

    private void parseConfigObject(JSONObject object) throws JSONException, IOException {
        ServiceConfiguration serviceConfig = new ServiceConfiguration();
        serviceConfig.setName(object.getString("name"));
        JSONObject config = object.getJSONObject("config");
        JSONArray namesArray = config.names();
        int namesSize = namesArray.length();
        for (int i = 0; i < namesSize; i++) {
            String name = namesArray.getString(i);
            switch (name) {
                case "type":
                    serviceConfig.setType(config.getString("type"));
                case "uri":
                    serviceConfig.setUri(config.getString("uri"));
                case "headers":
                    addHeaders(serviceConfig, config.getJSONObject("headers"));
                default:
                    serviceConfig.addProperty(name, config.getString(name));
            }
        }

        values.put(serviceConfig.getName(), serviceConfig);

    }

    private void addHeaders(ServiceConfiguration serviceConfig, JSONObject headers) throws JSONException {
        JSONArray headerNames = headers.names();

        if (headerNames == null) {
            return;
        }

        int namesSize = headerNames.length();
        for (int i = 0; i < namesSize; i++) {
            String headerName = headerNames.getString(i);
            serviceConfig.addHeader(headerName, headers.getString(headerName));
        }
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
    public static Map<String, ServiceConfiguration> parse(InputStream jsonStream) throws IOException, JSONException {
        MobileCoreJsonParser parser = new MobileCoreJsonParser(jsonStream);
        return parser.values;
    }
}
