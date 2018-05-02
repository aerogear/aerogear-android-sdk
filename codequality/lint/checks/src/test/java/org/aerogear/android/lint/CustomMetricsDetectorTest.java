package org.aerogear.android.lint;

import com.android.tools.lint.checks.infrastructure.LintDetectorTest;
import com.android.tools.lint.checks.infrastructure.TestLintResult;
import com.android.tools.lint.detector.api.Detector;
import com.android.tools.lint.detector.api.Issue;
import com.android.tools.lint.detector.api.Severity;

import org.junit.Test;

import java.util.Collections;
import java.util.List;

/**
 * Custom Metrics implementations should not
 */
public class CustomMetricsDetectorTest extends LintDetectorTest {

    private static final TestFile METRICS_INTERFACE = java(
        "package org.aerogear.mobile.core.metrics;\n" +
        "public interface Metrics<T> {}\n"
    );

    @Test
    public void testCustomMetricWarn() {
        final TestLintResult result = lint().allowCompilationErrors(false).files(
            METRICS_INTERFACE,
            java("package test.pkg; public class CustomMetric implements Metrics {}"),
            java("package test.pkg; this should not compile")
        ).run();
        result.expectCount(1, Severity.ERROR);
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
