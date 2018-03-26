package org.aerogear.mobile.core;

import static org.robolectric.internal.ManifestFactory.createLibraryAndroidManifest;

import java.util.HashMap;
import java.util.Map;

import org.junit.runners.model.InitializationError;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.internal.ManifestFactory;
import org.robolectric.internal.ManifestIdentifier;
import org.robolectric.manifest.AndroidManifest;
import org.robolectric.res.FileFsFile;

import org.aerogear.android.core.BuildConfig;

public class AeroGearTestRunner extends RobolectricTestRunner {
    private static final Map<ManifestIdentifier, AndroidManifest> appManifestsCache =
                    new HashMap<>();

    /**
     * Creates a runner to run {@code testClass}. Looks in your working directory for your
     * AndroidManifest.xml file and res directory by default. Use the Config annotation to
     * configure.
     *
     * @param testClass the test class to be run
     * @throws InitializationError if junit says so
     */
    public AeroGearTestRunner(Class<?> testClass) throws InitializationError {
        super(testClass);
    }

    @Override
    protected AndroidManifest getAppManifest(Config config) {
        ManifestFactory manifestFactory = getManifestFactory(config);

        ManifestIdentifier identifier = manifestFactory.identify(config);
        identifier = new ManifestIdentifier(identifier.getPackageName(),
                        identifier.getManifestFile(), identifier.getResDir(),
                        FileFsFile.from(BuildConfig.PROJECT_ROOT + "src/test/assets"),
                        identifier.getLibraries());
        synchronized (appManifestsCache) {
            AndroidManifest appManifest;
            appManifest = appManifestsCache.get(identifier);

            if (appManifest == null) {
                appManifest = createLibraryAndroidManifest(identifier);
                appManifestsCache.put(identifier, appManifest);
            }

            return appManifest;
        }
    }

}
