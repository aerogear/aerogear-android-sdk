package org.aerogear.mobile.core;

import java.util.HashMap;
import java.util.Map;

/**
 * Registry of service modules.  It is responsible for maintaining a mapping of service modules to
 * their types as well as the dependencies each module expects.
 */
public final class ServiceModuleRegistry {
    private static final Map<String, Class<? extends ServiceModule>> serviceTypeMap = new HashMap<>();
    private static final Map<String, String> dependencyMap = new HashMap<>();
    /**
     * Register the looking for a service module to a class.
     *
     * @param type the type of module to use
     * @param moduleClass the class that implements a service module type
     * @param dependsOn the other serviceTypes this Service Depends on.
     */
    public static void registerServiceModule(String type, Class<? extends ServiceModule> moduleClass, String... dependsOn) {
        serviceTypeMap.put(type, moduleClass);
        for (String dependency : dependsOn) {
            dependencyMap.put(type, dependency);
        }
    }

}
