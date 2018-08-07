package org.aerogear.mobile.core.http;

public interface HttpServiceModule {

    /**
     * Creates a new HttpRequest and prepends common configuration such as certificate pinning, user
     * agent headers, etc.
     *
     * @return a new HttpRequest object
     */
    HttpRequest newRequest();
}
