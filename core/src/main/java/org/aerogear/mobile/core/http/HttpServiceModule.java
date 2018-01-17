package org.aerogear.mobile.core.http;

import org.aerogear.mobile.core.MobileCore;
import org.aerogear.mobile.core.ServiceModule;
import org.aerogear.mobile.core.configuration.ServiceConfiguration;

public class HttpServiceModule implements ServiceModule {

    @Override
    public void bootstrap(MobileCore core, ServiceConfiguration configuration, Object... args) {

    }

    public String get(String url) {
        return null;
    }

}
