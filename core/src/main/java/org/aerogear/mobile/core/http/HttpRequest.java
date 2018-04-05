package org.aerogear.mobile.core.http;

import org.aerogear.mobile.core.reactive.Request;

/**
 * Generic interface for requests to HTTP Services.
 */

public interface HttpRequest<T extends HttpResponse> {

    String CONTENT_TYPE_HEADER = "Content-Type";
    String JSON_MIME_TYPE = "application/json";

    HttpRequest addHeader(String key, String value);

    /**
     * Prepares the request for an HTTP GET of the given URL.
     *
     * @param url a URL for a resource.
     */
    Request<T> get(String url);

    Request<T> post(String url, byte[] body);

    Request<T> put(String url, byte[] body);

    Request<T> delete(String url);

}
