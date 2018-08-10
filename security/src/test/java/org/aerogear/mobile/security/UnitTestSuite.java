package org.aerogear.mobile.security;

import org.aerogear.mobile.security.impl.DeviceCheckResultTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import org.aerogear.mobile.security.checks.BackupEnabledCheckTest;
import org.aerogear.mobile.security.checks.DeveloperModeEnabledCheckTest;
import org.aerogear.mobile.security.checks.EncryptionEnabledCheckTest;
import org.aerogear.mobile.security.checks.DebuggerEnabledCheckTest;
import org.aerogear.mobile.security.checks.RootEnabledCheckTest;
import org.aerogear.mobile.security.checks.IsEmulatorCheckTest;
import org.aerogear.mobile.security.checks.ScreenLockEnabledCheckTest;
import org.aerogear.mobile.security.metrics.DeviceCheckResultMetricTest;

/**
 * Suite containing all unit tests in security
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({BackupEnabledCheckTest.class, DebuggerEnabledCheckTest.class,
                DeveloperModeEnabledCheckTest.class, IsEmulatorCheckTest.class,
                EncryptionEnabledCheckTest.class, RootEnabledCheckTest.class, ScreenLockEnabledCheckTest.class,
                DeviceCheckResultTest.class, DeviceCheckResultMetricTest.class,
                DeviceCheckExecutorTest.class})
public class UnitTestSuite {

}
