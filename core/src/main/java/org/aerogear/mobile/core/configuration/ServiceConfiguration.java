package org.aerogear.mobile.core.configuration;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * This represents a parsed service from the mobilce-config.json
 */
public class ServiceConfiguration {
    private String name;
    private HashMap<String, String> properties = new HashMap<>();
    private String type;
    private String uri;
    private HashMap<String, String> headers = new HashMap<>();


    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void addProperty(String name, String value) {
        properties.put(name, value);
    }

    public Map<String, String> getProperties() {
        return Collections.unmodifiableMap(properties);
    }

    public String getProperty(String name) {
        return properties.get(name);
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getUri() {
        return uri;
    }

    public void addHeader(String headerName, String headerValue) {
        headers.put(headerName, headerValue);
    }

    public Map<String, String> getHeaders() {
        return Collections.unmodifiableMap(headers);
    }

}
