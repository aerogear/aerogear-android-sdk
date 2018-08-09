package org.aerogear.mobile.security;

import org.aerogear.mobile.security.impl.DeviceCheckResultTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import org.aerogear.mobile.security.checks.BackupDisallowedCheckTest;
import org.aerogear.mobile.security.checks.DeveloperModeDisabledCheckTest;
import org.aerogear.mobile.security.checks.EncryptionCheckTest;
import org.aerogear.mobile.security.checks.NoDebuggerCheckTest;
import org.aerogear.mobile.security.checks.NonRootedCheckTest;
import org.aerogear.mobile.security.checks.NotInEmulatorCheckTest;
import org.aerogear.mobile.security.checks.ScreenLockCheckTest;
import org.aerogear.mobile.security.metrics.DeviceCheckResultMetricTest;

/**
 * Suite containing all unit tests in security
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({BackupDisallowedCheckTest.class, NoDebuggerCheckTest.class,
                DeveloperModeDisabledCheckTest.class, NotInEmulatorCheckTest.class,
                EncryptionCheckTest.class, NonRootedCheckTest.class, ScreenLockCheckTest.class,
                DeviceCheckResultTest.class, DeviceCheckResultMetricTest.class,
                DeviceCheckExecutorTest.class})
public class UnitTestSuite {

}
