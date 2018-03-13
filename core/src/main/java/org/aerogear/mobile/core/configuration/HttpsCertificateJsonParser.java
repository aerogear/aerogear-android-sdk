package org.aerogear.mobile.core.configuration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import static org.aerogear.mobile.core.utils.SanityCheck.nonNull;


/**
 * This class is responsible for consuming a reader and producing a map of
 * hashes to be consumed and added to httpservice requests
 */

public class HttpsCertificateJsonParser {
    private final Map<String, String> hashes = new HashMap<>();

    private HttpsCertificateJsonParser(final InputStream jsonStream) throws IOException, JSONException{
        final String jsonText = readJsonStream(jsonStream);
        final JSONObject jsonObject = new JSONObject(jsonText);
        parseHttpsArray(jsonObject.getJSONArray("https"));
    }

    private String readJsonStream(final InputStream jsonStream) throws IOException{
        nonNull(jsonStream, "jsonStream");

        final StringBuilder builder = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(jsonStream))){
            String line;
            while ((line = reader.readLine()) != null){
                builder.append(line).append('\n');
            }
        }
        return builder.toString();
    }

    private void parseHttpsArray(final JSONArray array) throws JSONException, IOException{
        final int arrayLength = nonNull(array, "jsonArray").length();
        for(int i=0; i< arrayLength; i++){
            parseHttpsObject(array.getJSONObject(i));
        }
    }

    private void parseHttpsObject(final JSONObject jsonObject) throws JSONException, IOException{
        nonNull(jsonObject, "jsonObject");

        final HttpsConfiguration.Builder httpsConfigBuilder = HttpsConfiguration.newHashConfiguration();
        httpsConfigBuilder.setHostName(jsonObject.getString("host"));
        httpsConfigBuilder.setCertificateHash(jsonObject.getString("certificateHash"));

        final HttpsConfiguration httpsConfiguration = httpsConfigBuilder.build();
        hashes.put(httpsConfiguration.getHostName(), httpsConfiguration.getCertificateHash());
    }

    public static Map<String, String> parse (final InputStream jsonStream) throws IOException, JSONException{
        HttpsCertificateJsonParser parser = new HttpsCertificateJsonParser(jsonStream);
        return parser.hashes;
    }
}
