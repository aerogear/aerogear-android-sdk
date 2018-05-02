package org.aerogear.android.lint;

import com.android.tools.lint.detector.api.Category;
import com.android.tools.lint.detector.api.ClassContext;
import com.android.tools.lint.detector.api.Detector;
import com.android.tools.lint.detector.api.Implementation;
import com.android.tools.lint.detector.api.Issue;
import com.android.tools.lint.detector.api.Scope;
import com.android.tools.lint.detector.api.Severity;

import org.objectweb.asm.tree.ClassNode;

public class CustomMetricsDetector extends Detector implements Detector.ClassScanner  {

    private static final String ISSUE_MESSAGE = "Custom Metrics records are not supported";
    public static final Issue ISSUE = Issue.create(
            "CustomMetric",
            "Custom Metric implementation",
            ISSUE_MESSAGE,
            Category.CORRECTNESS,
            6,
            Severity.WARNING,new Implementation(
                    CustomMetricsDetector.class,
                    Scope.JAVA_FILE_SCOPE)
    );

    private static final String METRICS_INTERFACE = "org/aerogear/mobile/core/metrics/Metrics";
    private static final String SDK_PREFIX = "org/aerogear/mobile";
    @Override
    public void checkClass(ClassContext context, ClassNode classNode) {
        if(classNode.interfaces.contains(METRICS_INTERFACE) &&
                !classNode.name.startsWith(SDK_PREFIX)) {
            context.report(ISSUE, context.getLocation(classNode), ISSUE_MESSAGE);
        }
    }
}
