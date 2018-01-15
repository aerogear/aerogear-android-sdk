package org.aerogear.mobile.core;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import org.aerogear.mobile.core.configuration.MobileCoreJsonParser;
import org.aerogear.mobile.core.configuration.ServiceConfiguration;
import org.aerogear.mobile.core.logging.Logger;
import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.util.AbstractSequentialList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * MobileCore is the entry point into AeroGear mobile services that are managed by the mobile-core
 * ¿feature( TODO: Get correct noun )? in OpenShift.
 *
 * Usage.java
 * ```
 * MobileCore core = new MobileCore.Builder(context, R.raw.mobile_core).build();
 * core.feature(Keycloak.class).login();// would begin a OAuth flow.
 * core.feature(MyRestService.class).upload(myData);// Would upload myData and be secured by KeyCloak
 * ```
 */
public final class MobileCore {


    private final Context context;
    private final String mobileServiceFileName;
    private final ServiceModuleRegistry serviceRegistry;

    private Map<String, ServiceConfiguration> configurationMap;
    private Map<String, ServiceModule> services = new HashMap<>();

    private MobileCore(@NonNull Context context, @NonNull String mobileServiceFileName, @NonNull ServiceModuleRegistry registryService) {
        this.context = context.getApplicationContext();
        this.mobileServiceFileName = mobileServiceFileName;
        this.serviceRegistry = registryService;
    }

    public Logger defaultLog() {
        return (String message, Exception e) -> {
            Log.e("MOBILE_CORE", message, e);
        };
    }

    public void bootstrap(Object... args) {

        try (InputStream configStream = context.getAssets().open(this.mobileServiceFileName);) {

        Set<String> servicesInitted = new HashSet<>();
        Set<String> servicesPending = new HashSet<>();

            this.configurationMap = MobileCoreJsonParser.parse(configStream);
            for (Map.Entry<String, Class<? extends ServiceModule>> serviceModuleEntry : serviceRegistry.services()) {
                String serviceName = serviceModuleEntry.getKey();
                servicesPending.add(serviceName);
            }

            boolean unresolvableServiceDetected = false;//This is a bad name/initialization.  The
                                                        // goal is to reset this at the beginning of
                                                        //each iteration of the loop and clear it
                                                        //when we instanciate a service.  If we have
                                                        //a loop where we can't start a service
                                                        //throw an exception.
            while (!servicesPending.isEmpty()) {
                unresolvableServiceDetected = true;
                String serviceUnderInstanciation = "";
                for (String serviceName : servicesPending) {
                    List<String> dependencies = serviceRegistry.getDependenciesFor(serviceName);
                    serviceUnderInstanciation = serviceName;
                    if (dependencies.isEmpty() || servicesInitted.containsAll(dependencies)) {
                        unresolvableServiceDetected = false;
                        Class<? extends ServiceModule> serviceClass = serviceRegistry.getServiceClass(serviceName);
                        ServiceModule serviceInstance = serviceClass.newInstance();
                        serviceInstance.bootstrap(this, getConfig(serviceName));
                        this.services.put(serviceName, serviceInstance);
                        servicesInitted.add(serviceName);
                        break;
                    }
                }
                if (!unresolvableServiceDetected) {
                    servicesPending.remove(serviceUnderInstanciation);
                } else {
                    throw new BootstrapException(String.format("Unresolvable service detected %s", serviceUnderInstanciation));
                }
            }

        } catch (JSONException | IOException e) {
            defaultLog().error(e.getMessage(), e);
            throw new BootstrapException(String.format("%s could not be loaded", mobileServiceFileName), e);
        } catch (IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
            throw new BootstrapException("Modules could not be started up", e);
        }


        //startup known modules
    }

    /**
     * Returns the parsed configuration object of the named configuration, or an empty ServiceConfiguration
     * @param configurationName the name of the configuration to lookup
     * @return the parsed configuration object of the named configuration, or an empty ServiceConfiguration
     */
    public ServiceConfiguration getConfig(String configurationName) {
        ServiceConfiguration config = configurationMap.get(configurationName);
        if (config == null) {
            config = new ServiceConfiguration();
            config.setName(configurationName);
            configurationMap.put(configurationName, config);
        }
        return config;
    }

    public ServiceModule getService(String simpleService) {
        return services.get(simpleService);
    }

    /**
     * Builder for MobileCore.  This class ensures that required properties are set and then creates
     * a MobileCore instance.
     * <p>
     * Usage.java
     * ```
     * MobileCore core = new MobileCore.Builder(context, R.raw.mobile_core).build();
     * ```
     */
    public static class Builder {

        private final Context context;
        private boolean built = false;
        private String mobileServiceFileName = "mobile-services.json";
        private ServiceModuleRegistry registryService;

        public Builder(@NonNull Context context) {
            this.context = context;
        }

        /**
         * The filename of the mobile service configuration file in the assets directory.
         *
         * defaults to mobile-services.json
         *
         * @return the current value, never null.
         */
        @NonNull
        public String getMobileServiceFileName() {
            return mobileServiceFileName;
        }

        /**
         * The filename of the mobile service configuration file in the assets directory.
         *
         * defaults to mobile-services.json
         *
         * @param mobileServiceFileName a new filename.  May not be null.
         * @return the current value, never null.
         */
        public Builder setMobileServiceFileName(@NonNull String mobileServiceFileName) {

            if (mobileServiceFileName == null) {
                throw new IllegalArgumentException("mobileServiceFileName may not be null");
            }

            this.mobileServiceFileName = mobileServiceFileName;
            return this;
        }

        /**
         * Builds a mobile core instance.  Please note that once this is built the Builder may not
         * be used again.
         *
         * @return a mobile core instance based on Builder parameters
         * @throws IllegalStateException if this builder has already been built.
         */
        public MobileCore build() {
            if (!built) {
                built = true;

                if (registryService == null) {
                    registryService = new ServiceModuleRegistry();//TODO: Make this getInstance or something
                }
                MobileCore core = new MobileCore(context, mobileServiceFileName, registryService);
                core.bootstrap();
                return core;
            } else {
                throw new IllegalStateException("MobileCore has already been built");
            }
        }

        public void setRegistryService(ServiceModuleRegistry registryService) {
            this.registryService = registryService;
        }

        public ServiceModuleRegistry getRegistryService() {
            return registryService;
        }
    }

}
