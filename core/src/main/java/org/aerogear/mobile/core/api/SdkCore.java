package org.aerogear.mobile.core.api;


import android.content.Context;

import org.aerogear.mobile.core.configuration.MobileCoreJsonParser;
import org.aerogear.mobile.core.configuration.ServiceConfig;
import org.aerogear.mobile.core.configuration.ServiceConfiguration;
import org.aerogear.mobile.core.executor.AppExecutors;
import org.aerogear.mobile.core.http.NetworkApi;
import org.aerogear.mobile.core.logging.Logger;
import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.Executor;

public class SdkCore implements Core {

    private String configurationFileName = "mobile-services.json";
    private Context context;
    private Map<String, ServiceConfiguration> configurationMap;

    public SdkCore(Context context){
        this.context = context;
        initConfig();
    }

    public SdkCore(Context context, String configurationFileName){
        this.context = context;
        this.configurationFileName = configurationFileName;
        initConfig();
    }

    private void initConfig() {
        try (InputStream configStream = context.getAssets().open(this.configurationFileName);) {
            this.configurationMap = MobileCoreJsonParser.parse(configStream);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public ServiceConfig getConfiguration(String moduleType) {
        return this.configurationMap.get(moduleType);
    }


    @Override
    public Executor getAppExecutors() {
        return AppExecutors.mainThread();
    }

    @Override
    public NetworkApi getNetworkLayer() {
        return new NetworkApi();
    }

    @Override
    public Logger getLogger() {
        return new Logger() {
            @Override
            public void error(String message, Exception e) {
                //TODO
            }
        };
    }


}
