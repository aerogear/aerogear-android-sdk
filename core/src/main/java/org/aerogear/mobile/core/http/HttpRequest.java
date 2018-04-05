package org.aerogear.mobile.core.http;

import org.aerogear.mobile.core.reactive.Request;

/**
 * Generic interface for requests to HTTP Services.
 */

public interface HttpRequest {

    String CONTENT_TYPE_HEADER = "Content-Type";
    String JSON_MIME_TYPE = "application/json";

    HttpRequest addHeader(String key, String value);

    /**
     * Prepares the request for an HTTP GET of the given URL.
     *
     * @param url a URL for a resource.
     * @return a reactive request
     */
    Request<HttpResponse> get(String url);

    Request<HttpResponse> post(String url, byte[] body);

    Request<HttpResponse> put(String url, byte[] body);

    Request<HttpResponse> delete(String url);

}
