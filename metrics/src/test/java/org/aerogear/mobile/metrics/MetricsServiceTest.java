package org.aerogear.mobile.metrics;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import org.aerogear.mobile.core.MobileCore;
import org.aerogear.mobile.core.configuration.ServiceConfiguration;
import org.aerogear.mobile.core.http.HttpRequest;
import org.aerogear.mobile.core.http.HttpResponse;
import org.aerogear.mobile.core.http.HttpServiceModule;
import org.aerogear.mobile.core.logging.Logger;
import org.aerogear.mobile.core.logging.LoggerAdapter;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class MetricsServiceTest {
    private final static String MOCK_PKG = "org.aerogear.test";
    private final static String MOCK_URL = "https://dummy";
    private final static String MOCK_VSN = "1.2.3";
    private final static String MOCK_CID = "12345";

    private Logger logger = new LoggerAdapter();

    @Mock
    private MobileCore core;

    @Mock
    private ServiceConfiguration config;

    @Mock
    private HttpServiceModule httpService;

    @Mock
    private HttpRequest request;

    @Mock
    private HttpResponse response;

    @Mock
    private Context context;

    @Mock
    private SharedPreferences sharedPreferences;

    private JSONObject expectedResponse() throws JSONException {
        final JSONObject result = new JSONObject();
        result.put("clientId", MOCK_CID);
        result.put("appId", MOCK_PKG);
        result.put("appVersion", MOCK_VSN);
        result.put("sdkVersion", MobileCore.getSdkVersion());
        result.put("platform", "android");
        result.put("platformVersion", Build.VERSION.SDK_INT);
        return result;
    }

    @Before
    public void setup() throws JSONException {
        when(core.getHttpLayer()).thenReturn(httpService);

        when(httpService.newRequest()).thenReturn(request);
        when(request.execute()).thenReturn(response);

        when(core.getAppVersion()).thenReturn(MOCK_VSN);
        when(context.getPackageName()).thenReturn(MOCK_PKG);
        when(config.getUri()).thenReturn(MOCK_URL);

        when(sharedPreferences.getString(MetricsService.STORAGE_KEY, null))
            .thenReturn(MOCK_CID);

        when(context.getSharedPreferences(MetricsService.STORAGE_NAME, Context.MODE_PRIVATE))
            .thenReturn(sharedPreferences);
    }

    @Test
    public void testMetricsService_json() throws JSONException {
        MetricsService service = new MetricsService();
        service.configure(core, config);
        service.init(context);

        // Verify that the data that would be posted is equal to
        // the expected data
        verify(request).post(MOCK_URL, expectedResponse().toString().getBytes());
    }
}
