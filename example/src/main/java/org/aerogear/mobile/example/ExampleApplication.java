package org.aerogear.mobile.example;

import android.app.Application;
import android.widget.Toast;

import org.aerogear.mobile.core.MobileCore;
import org.aerogear.mobile.core.metrics.MetricsPublisherListener;
import org.aerogear.mobile.core.metrics.MetricsService;

public class ExampleApplication extends Application implements MetricsPublisherListener {

    private MobileCore mobileCore;
    private MetricsService metricsService;

    @Override
    public void onCreate() {
        super.onCreate();

        mobileCore = MobileCore.init(this);
        metricsService = mobileCore.getInstance(MetricsService.class);
        metricsService.setListener(this);

        metricsService.sendAppAndDeviceMetrics();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();

        mobileCore.destroy();
    }

    public MobileCore getMobileCore() {
        return mobileCore;
    }

    public MetricsService getMetricsService() {
        return metricsService;
    }

    @Override
    public void onPublishMetricsSuccess() {
        Toast.makeText(this, "App metrics sent", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPublishMetricsError(Exception error) {
        Toast.makeText(this, "Metrics request error: " + error, Toast.LENGTH_SHORT).show();
    }
}
