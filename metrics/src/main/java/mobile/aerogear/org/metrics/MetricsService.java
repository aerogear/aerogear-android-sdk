package mobile.aerogear.org.metrics;

import android.util.Log;

import org.aerogear.mobile.core.MobileCore;
import org.aerogear.mobile.core.ServiceModule;
import org.aerogear.mobile.core.configuration.ServiceConfiguration;
import org.aerogear.mobile.core.http.HttpServiceModule;

public class MetricsService implements ServiceModule {
    private HttpServiceModule httpService;
    private String metricsUrl;

    @Override
    public void bootstrap(MobileCore core, ServiceConfiguration configuration) {
        httpService = (HttpServiceModule) core.getService("http");
        metricsUrl = configuration.getUri();

        Log.d("METRICS", metricsUrl);
    }
}
