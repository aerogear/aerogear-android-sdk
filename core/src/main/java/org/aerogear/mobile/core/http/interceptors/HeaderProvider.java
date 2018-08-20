package org.aerogear.mobile.core.http.interceptors;

import java.util.Map;

/**
 * Interface used to provide additional headers for request
 */
public interface HeaderProvider {

    /**
     * Used to add additional headers to request
     *
     * @return headers
     */
    Map<String, String> getHeaders();
}
