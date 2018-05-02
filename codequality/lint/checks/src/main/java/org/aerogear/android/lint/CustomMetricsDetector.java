package org.aerogear.android.lint;

import com.android.tools.lint.detector.api.Category;
import com.android.tools.lint.detector.api.Detector;
import com.android.tools.lint.detector.api.Implementation;
import com.android.tools.lint.detector.api.Issue;
import com.android.tools.lint.detector.api.JavaContext;
import com.android.tools.lint.detector.api.Scope;
import com.android.tools.lint.detector.api.Severity;

import org.jetbrains.uast.UClass;
import org.jetbrains.uast.UElement;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CustomMetricsDetector extends Detector implements Detector.UastScanner  {

    private static final String ISSUE_MESSAGE = "Custom Metrics records are not supported";
    private static final String METRICS_INTERFACE = "org.aerogear.mobile.core.metrics.Metrics";
    private static final String SDK_NAMESPACE = "org.aerogear.mobile";
    public static final Issue ISSUE = Issue.create(
            "CustomMetric",
            "Custom Metric implementation",
            ISSUE_MESSAGE,
            Category.CORRECTNESS,
            6,
            Severity.ERROR,
            new Implementation(
            CustomMetricsDetector.class,
            Scope.JAVA_FILE_SCOPE)
    );

    @Override
    public List<String> applicableSuperClasses() {
        // This avoids unnecessary visits
        return Collections.singletonList(METRICS_INTERFACE);
    }
    @Override
    public void visitClass(JavaContext context, UClass declaration) {
        final boolean insideSDK = declaration.getQualifiedName().startsWith(SDK_NAMESPACE);
        if(insideSDK) {
            return;
        }

        final boolean implementsMetrics = Arrays.stream(declaration.getInterfaces())
                .anyMatch(c -> c.getQualifiedName().equals(METRICS_INTERFACE));
        if (implementsMetrics) {
            context.report(ISSUE, context.getLocation((UElement) declaration), ISSUE_MESSAGE);
        }
    }
}
