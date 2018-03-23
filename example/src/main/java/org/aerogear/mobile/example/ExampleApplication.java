package org.aerogear.mobile.example;

import android.app.Application;

import org.aerogear.mobile.core.MobileCore;
import org.aerogear.mobile.core.metrics.MetricsService;

public class ExampleApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        MetricsService metricsService = MobileCore.getInstance(MetricsService.class);
        metricsService.sendAppAndDeviceMetrics(
                        error -> MobileCore.getInstance().getLogger().error(error.getMessage()));
    }

    @Override
    public void onTerminate() {
        super.onTerminate();

        MobileCore.destroy();
    }

}
