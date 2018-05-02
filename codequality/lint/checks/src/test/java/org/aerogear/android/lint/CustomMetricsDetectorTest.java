package org.aerogear.android.lint;

import com.android.tools.lint.checks.infrastructure.LintDetectorTest;
import com.android.tools.lint.checks.infrastructure.TestLintResult;
import com.android.tools.lint.detector.api.Detector;
import com.android.tools.lint.detector.api.Issue;
import com.android.tools.lint.detector.api.Severity;

import org.junit.Test;

import java.util.Collections;
import java.util.List;

public class CustomMetricsDetectorTest extends LintDetectorTest {

    private static final TestFile METRICS_INTERFACE = java(
        "package org.aerogear.mobile.core.metrics;\n" +
        "public interface Metrics<T> {}\n"
    );

    @Test
    public void testSDKMetric() {
        final TestLintResult result = lint().files(
                METRICS_INTERFACE,
                java("package org.aerogear.mobile.some.module;\n" +
                        "import org.aerogear.mobile.core.metrics.Metrics;\n" +
                        "public class SDKModuleMetric implements Metrics<String> {}")
        ).run().expectClean();
    }

    @Test
    public void testCustomMetricWarn() {
        final TestLintResult result = lint().files(
            METRICS_INTERFACE,
            java("package test.pkg;\n" +
            "import org.aerogear.mobile.core.metrics.Metrics;\n" +
            "public class CustomMetric implements Metrics<String> {}")
        ).run().expectCount(1, Severity.ERROR)
        .expect("src/test/pkg/CustomMetric.java:3: Error: Custom Metrics records are not supported [CustomMetric]\n" +
                "public class CustomMetric implements Metrics<String> {}\n" +
                "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n" +
                "1 errors, 0 warnings\n");
    }

    @Override
    protected boolean allowMissingSdk() {
        return true;
    }

    @Override
    protected Detector getDetector() {
        return new CustomMetricsDetector();
    }

    @Override
    protected List<Issue> getIssues() {
        return Collections.singletonList(CustomMetricsDetector.ISSUE);
    }
}
