package org.aerogear.mobile.metrics;

import android.content.Context;

import org.aerogear.mobile.core.MobileCore;
import org.aerogear.mobile.core.ServiceModule;
import org.aerogear.mobile.core.configuration.ServiceConfiguration;
import org.aerogear.mobile.core.http.HttpRequest;
import org.aerogear.mobile.core.http.HttpResponse;
import org.aerogear.mobile.core.http.HttpServiceModule;
import org.aerogear.mobile.core.logging.Logger;
import org.aerogear.mobile.metrics.interfaces.MetricsProvider;
import org.aerogear.mobile.metrics.providers.DefaultMetricsProvider;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MetricsService implements ServiceModule {
    public final static String STORAGE_NAME = "org.aerogear.mobile.metrics";
    public final static String STORAGE_KEY = "metrics-sdk-installation-id";

    private final static String MODULE_NAME = "metrics";
    public final static String TAG = "AEROGEAR/METRICS";

    private HttpServiceModule httpService;
    private Logger logger;

    private String metricsUrl = null;

    // Copy all properties from the source object and add it to the top level
    // of the root object
    private void addToRoot(final JSONObject root, final JSONObject source) throws JSONException {
        JSONArray names = source.names();
        for (int i = 0; i < names.length(); ++i) {
            String name = names.getString(i);
            root.put(name, source.get(name));
        }
    }

    private void aggregateSingleProvider(
        final MetricsProvider provider,
        final JSONObject target,
        final Context context)
        throws JSONException {

        String namespace = provider.namespace();
        JSONObject metrics = provider.metrics(context);

        // If the namespace of a provider is null we add it's properties
        // to the root object
        if (namespace == null || namespace.length() == 0) {
            addToRoot(target, metrics);
        } else {
            target.put(namespace, metrics);
        }
    }

    // Iterate over all providers and add their JSON to the target metrics object
    private void aggregateAllProviders(final JSONObject target, final Context context)
        throws JSONException {
        for (final MetricsProvider provider: MetricsRegistry.instance().getProviders()) {
            aggregateSingleProvider(provider, target, context);
        }
    }

    /**
     * Send the metrics data to the server. The data is contained in a JSON object with the
     * following properties: clientId, appId, sdkVersion and appVersion
     *
     * Should not be overridden. Users can change the target URL in mobile-services.json
     *
     * @param context Android application context
     */
    public final void init(final Context context) {
        try {
            JSONObject metrics = new JSONObject();
            aggregateAllProviders(metrics, context);

            // Send request to backend
            HttpRequest request = httpService.newRequest();
            request.post(metricsUrl, metrics.toString().getBytes());
            HttpResponse response = request.execute();

            // Async response handling
            response.onComplete(new MetricsResponseHandler(response));
            response.waitForCompletionAndClose();
        } catch (JSONException e) {
            logger.error(TAG, e);
        }
    }

    @Override
    public void configure(final MobileCore core, final ServiceConfiguration serviceConfiguration) {
        metricsUrl = serviceConfiguration.getUri();
        httpService = core.getHttpLayer();
        logger = MobileCore.getLogger();

        MetricsRegistry.instance().registerProvider(new DefaultMetricsProvider());
    }

    @Override
    public String type() {
        return MODULE_NAME;
    }

    @Override
    public void destroy() {
    }

    private static final class MetricsResponseHandler implements Runnable {
        private final HttpResponse response;

        public MetricsResponseHandler(final HttpResponse response) {
            this.response = response;
        }

        @Override
        public void run() {
            if (response.isFailed()) {
                MobileCore.getLogger().error(TAG, response.getRequestError());
            }
        }
    }
}
