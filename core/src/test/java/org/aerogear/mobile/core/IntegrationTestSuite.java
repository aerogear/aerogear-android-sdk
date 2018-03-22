package org.aerogear.mobile.core;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import org.aerogear.mobile.core.integration.MetricsServiceIntegrationTest;

/**
 * Suite containing all integration tests in core
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({MetricsServiceIntegrationTest.class})
public class IntegrationTestSuite {

}
