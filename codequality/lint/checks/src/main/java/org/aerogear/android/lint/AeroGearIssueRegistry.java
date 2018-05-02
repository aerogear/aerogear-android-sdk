package org.aerogear.android.lint;

import com.android.tools.lint.client.api.IssueRegistry;
import com.android.tools.lint.detector.api.Issue;

import java.util.Arrays;
import java.util.List;

/**
 * Created by secon on 3/16/2018.
 */

public class AeroGearIssueRegistry extends IssueRegistry {
    @Override
    public List<Issue> getIssues() {
        return Arrays.asList(NotNullAssignmentDetector.ISSUE, CustomMetricsDetector.ISSUE);
    }
}
