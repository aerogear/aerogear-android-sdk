package org.aerogear.mobile.core;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import org.aerogear.mobile.core.unit.MobileCoreTest;
import org.aerogear.mobile.core.unit.ReactiveCaseTest;
import org.aerogear.mobile.core.unit.configuration.MobileCoreParserTest;
import org.aerogear.mobile.core.unit.configuration.ServiceConfigurationTest;
import org.aerogear.mobile.core.unit.http.OkHttpServiceModuleTest;
import org.aerogear.mobile.core.unit.metrics.MetricsServiceTest;
import org.aerogear.mobile.core.unit.metrics.impl.AppMetricsTest;
import org.aerogear.mobile.core.unit.metrics.impl.DeviceMetricsTest;
import org.aerogear.mobile.core.unit.utils.SanityCheckTest;

/**
 * Suite containing all unit tests in core
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({MobileCoreTest.class, MobileCoreParserTest.class,
                ServiceConfigurationTest.class, OkHttpServiceModuleTest.class,
                ReactiveCaseTest.class, MetricsServiceTest.class, AppMetricsTest.class,
                DeviceMetricsTest.class, SanityCheckTest.class})
public class UnitTestSuite {

}
