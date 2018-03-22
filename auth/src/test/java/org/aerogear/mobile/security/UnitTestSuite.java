package org.aerogear.mobile.security;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import org.aerogear.mobile.security.checks.AllowBackupFlagCheckTest;
import org.aerogear.mobile.security.checks.DebuggerCheckTest;
import org.aerogear.mobile.security.checks.DeveloperModeCheckTest;
import org.aerogear.mobile.security.checks.EmulatorCheckTest;
import org.aerogear.mobile.security.checks.EncryptionCheckTest;
import org.aerogear.mobile.security.checks.RootedCheckTest;
import org.aerogear.mobile.security.checks.ScreenLockCheckTest;
import org.aerogear.mobile.security.impl.SecurityCheckResultTest;
import org.aerogear.mobile.security.metrics.SecurityCheckResultMetricTest;

/**
 * Suite containing all unit tests in security
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({AllowBackupFlagCheckTest.class, DebuggerCheckTest.class,
                DeveloperModeCheckTest.class, EmulatorCheckTest.class, EncryptionCheckTest.class,
                RootedCheckTest.class, ScreenLockCheckTest.class, SecurityCheckResultTest.class,
                SecurityCheckResultMetricTest.class, SecurityCheckExecutorTest.class})
public class UnitTestSuite {

}
