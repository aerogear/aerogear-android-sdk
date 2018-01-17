package org.aerogear.mobile.core.api;

import org.aerogear.mobile.core.configuration.ServiceConfig;
import org.aerogear.mobile.core.http.NetworkApi;
import org.aerogear.mobile.core.logging.Logger;

import java.util.concurrent.Executor;

/**
 * Core interface
 *
 * Usage
 *
 *  Core coreSdk = new SdkCore(context);
 *  ISyncSDK syncSDK = new SyncSDK(context,coreSdk);
 *
 *  Alternative: Initiate core directly in Service
 *
 *  public SyncSDK(ApplicationContext context){
 *     Core coreSdk = new SdkCore(context);
 *  }
 *
 *
 */
public interface Core {

    NetworkApi getNetworkLayer();

    Logger getLogger();

    ServiceConfig getConfiguration(String moduleType);

    Executor getAppExecutors();

}
