package org.aerogear.mobile.core.configuration;

import java.util.Map;

/**
 * Service configuration
 */
public interface ServiceConfig {
    String getName();

    Map<String, String> getProperties();

    String getProperty(String name);

    String getType();

    String getUri();

    Map<String, String> getHeaders();
}
