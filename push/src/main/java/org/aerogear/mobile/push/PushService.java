package org.aerogear.mobile.push;

import org.aerogear.mobile.core.MobileCore;
import org.aerogear.mobile.core.ServiceModule;
import org.aerogear.mobile.core.configuration.ServiceConfiguration;

public class PushService implements ServiceModule {

    @Override
    public String type() {
        return "push";
    }

    @Override
    public void configure(MobileCore core, ServiceConfiguration serviceConfiguration) {
    }

    @Override
    public boolean requiresConfiguration() {
        return true;
    }

    @Override
    public void destroy() {
    }

}
