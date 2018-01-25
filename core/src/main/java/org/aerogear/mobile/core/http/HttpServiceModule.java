package org.aerogear.mobile.core.http;

import org.aerogear.mobile.core.MobileCore;
import org.aerogear.mobile.core.ServiceModule;
import org.aerogear.mobile.core.configuration.ServiceConfiguration;

public interface HttpServiceModule extends ServiceModule {

    /**
     * Creates a new HttpRequest and prepends common configuration such as certificate pinning,
     * user agent headers, etc.
     * @return a new HttpRequest object
     */
    HttpRequest newRequest();
}
