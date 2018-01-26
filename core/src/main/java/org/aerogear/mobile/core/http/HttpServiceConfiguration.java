package org.aerogear.mobile.core.http;

import org.aerogear.mobile.core.configuration.ServiceConfiguration;

import java.util.Map;

/**
 * This is a service configuration that processes a generic ServiceConfiguration for http properties.
 */
public class HttpServiceConfiguration extends ServiceConfiguration {

    private final ServiceConfiguration configuration;

    public HttpServiceConfiguration(ServiceConfiguration configuration) {
        super();
        this.configuration = configuration;
    }

    public HttpServiceConfiguration() {
        this.configuration = new ServiceConfiguration();
    }

    @Override
    public void setName(String name) {
        configuration.setName(name);
    }

    @Override
    public String getName() {
        return configuration.getName();
    }

    @Override
    public void addProperty(String name, String value) {
        configuration.addProperty(name, value);
    }

    @Override
    public Map<String, String> getProperties() {
        return configuration.getProperties();
    }

    @Override
    public String getProperty(String name) {
        return configuration.getProperty(name);
    }

    @Override
    public void setType(String type) {
        configuration.setType(type);
    }

    @Override
    public String getType() {
        return configuration.getType();
    }

    @Override
    public void setUri(String uri) {
        configuration.setUri(uri);
    }

    @Override
    public String getUri() {
        return configuration.getUri();
    }

    @Override
    public void addHeader(String headerName, String headerValue) {
        configuration.addHeader(headerName, headerValue);
    }

    @Override
    public Map<String, String> getHeaders() {
        return configuration.getHeaders();
    }
}
