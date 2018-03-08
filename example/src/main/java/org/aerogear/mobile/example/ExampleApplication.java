package org.aerogear.mobile.example;

import java.text.MessageFormat;

import android.app.Application;
import android.widget.Toast;

import org.aerogear.mobile.core.MobileCore;
import org.aerogear.mobile.core.http.HttpResponse;
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
    public void onPublishMetricsSuccess(HttpResponse httpResponse) {
        String text = MessageFormat.format("Metrics response: {0}: {1}", httpResponse.getStatus(),
                        httpResponse.stringBody());
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPublishMetricsError(Exception error) {
        Toast.makeText(this, "Metrics request error: " + error, Toast.LENGTH_SHORT).show();
    }
}
