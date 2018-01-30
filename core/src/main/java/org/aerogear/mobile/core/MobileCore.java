package org.aerogear.mobile.core;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import org.aerogear.mobile.core.configuration.MobileCoreJsonParser;
import org.aerogear.mobile.core.configuration.ServiceConfiguration;
import org.aerogear.mobile.core.http.HttpServiceModule;
import org.aerogear.mobile.core.http.OkHttpServiceModule;
import org.aerogear.mobile.core.logging.Logger;
import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * MobileCore is the entry point into AeroGear mobile services that are managed by the mobile-core
 * feature( TODO: Get correct noun )? in OpenShift.
 * <p>
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

    private MobileCore(@NonNull Context context, @NonNull String mobileServiceFileName, @NonNull ServiceModuleRegistry serviceRegistry) {
        this.context = context.getApplicationContext();
        this.mobileServiceFileName = mobileServiceFileName;
        this.serviceRegistry = serviceRegistry;
    }

    public Logger defaultLog() {
        return (String message, Exception e) -> {
            Log.e("MOBILE_CORE", message, e);
        };
    }

    public void bootstrap() {

        try (InputStream configStream = context.getAssets().open(this.mobileServiceFileName);) {

            //Services which have been started
            Set<String> servicesBootstrapped = new HashSet<>();
            //Services named in the configuration file
            List<String> declaredServices = new ArrayList<>();

            this.configurationMap = MobileCoreJsonParser.parse(configStream);
            addCoreServices();

            declaredServices.addAll(configurationMap.keySet());

            declaredServices = sortServicesIntoBootstrapOrder(declaredServices);

            for (String serviceName : declaredServices) {
                ServiceModule serviceInstance = serviceRegistry.getServiceModule(serviceName);

                if (serviceInstance == null) {
                    Class<? extends ServiceModule> serviceClass = serviceRegistry.getServiceClass(serviceName);
                    if (serviceClass == null) {
                        throw new BootstrapException(String.format("Service with name %s does not have a type in the ServiceRegistry.", serviceName));
                    }
                    serviceInstance = serviceClass.newInstance();
                }

                serviceInstance.bootstrap(this, getConfig(serviceName));

                this.services.put(serviceName, serviceInstance);
                servicesBootstrapped.add(serviceName);

            }

        } catch (JSONException | IOException e) {
            throw new BootstrapException(String.format("%s could not be loaded", mobileServiceFileName), e);
        } catch (IllegalAccessException | InstantiationException e) {
            throw new BootstrapException("Modules could not be started up", e);
        }

    }

    /**
     * There are some services that are "core" and usually won't appear in mobile-services.json
     */
    private void addCoreServices() {
        if (this.configurationMap.get("http") == null) {
            this.configurationMap.put("http", new ServiceConfiguration());
        }
    }

    /**
     * This method sorts a list of servers into the order they will need to be initialized in.
     *
     * @param declaredServices a list of services to be sorted into the order they will be
     *                         initialized in.
     * @return a sorted list of services.
     * @throws BootstrapException if circular or undefined dependencies are detected.
     */
    private List<String> sortServicesIntoBootstrapOrder(List<String> declaredServices) {
        List<String> workingDeclaredServicesList = new ArrayList<>(declaredServices);
        List<String> sortedServices = new ArrayList<>(declaredServices.size());

        while (!workingDeclaredServicesList.isEmpty()) {
            sortedServices.add(popNextService(workingDeclaredServicesList, sortedServices));
        }

        return sortedServices;
    }

    /**
     * Removes a service from the list that can be instanciated or has all of its dependencies in
     * sortedServices.
     *
     * @param workingList    a mutable list to find the first element in that can be resolved given
     *                       the values in sortedServices
     * @param sortedServices services which have had their dependencies check and are in initialization
     *                       order
     * @return the next service name
     * @throws BootstrapException if circular or undefined dependencies are detected.
     */
    private String popNextService(List<String> workingList, List<String> sortedServices) {
        boolean unresolvableServiceDetected = true;
        String serviceUnderInstanciation = "";

        for (String serviceName : workingList) {
            List<String> dependencies = serviceRegistry.getDependenciesFor(serviceName);
            serviceUnderInstanciation = serviceName;
            if (dependencies.isEmpty() || sortedServices.containsAll(dependencies)) {
                unresolvableServiceDetected = false;
                break;
            }
        }
        if (!unresolvableServiceDetected) {
            workingList.remove(serviceUnderInstanciation);
        } else {
            throw new BootstrapException(String.format("Unresolvable service detected %s", serviceUnderInstanciation));
        }

        return serviceUnderInstanciation;
    }

    /**
     * Returns the parsed configuration object of the named configuration, or an empty ServiceConfiguration
     *
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

    @NonNull
    public ServiceModule getService(String serviceName) {
        return services.get(serviceName);
    }

    /**
     * Returns the names of all configured services
     *
     * @return a list of service names.
     */
    @NonNull
    public List<String> getServiceNames() {
        return new ArrayList<>(services.keySet());
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
        private ServiceModuleRegistry serviceRegistry;

        public Builder(@NonNull Context context) {
            this.context = context;
        }

        /**
         * The filename of the mobile service configuration file in the assets directory.
         * <p>
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
         * <p>
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

                if (serviceRegistry == null) {
                    serviceRegistry = ServiceModuleRegistry.getInstance();
                }
                MobileCore core = new MobileCore(context, mobileServiceFileName, serviceRegistry);

                serviceRegistry.registerServiceModule("http", OkHttpServiceModule.class);

                core.bootstrap();
                return core;
            } else {
                throw new IllegalStateException("MobileCore has already been built");
            }
        }

        public Builder setServiceRegistry(ServiceModuleRegistry registryService) {
            this.serviceRegistry = registryService;
            return this;
        }

        public ServiceModuleRegistry getServiceRegistry() {
            return serviceRegistry;
        }
    }

}
