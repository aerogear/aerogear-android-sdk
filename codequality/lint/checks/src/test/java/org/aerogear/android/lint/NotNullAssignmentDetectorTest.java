package org.aerogear.android.lint;

import com.android.tools.lint.checks.infrastructure.LintDetectorTest;
import com.android.tools.lint.detector.api.Detector;
import com.android.tools.lint.detector.api.Issue;

import org.junit.Test;

import java.util.Collections;
import java.util.List;

/**
 * The SanityCheck#nonNull method returns the test parameter.  If there is an assignment of that
 * parameter immediately after the check this detector will find it and alert the developer
 * that they may assign the result of the nonNull check.
 *
 * ex.
 * ```
 * nonNull(value, "SomeValue")
 * this.value = value;
 *
 * should be
 *
 * this.value = nonNull(value, "SomeValue")
 *
 * ```
 */
public class NotNullAssignmentDetectorTest extends LintDetectorTest {

    @Test
    public void testBasicClean() {


        lint().files(
            java("" +
                "package test.pkg;\n" +
                "public class TestClass1 {\n" +
                "public static class SanityCheck{public static <T> T nonNull(T test) {return test;}}" + //PsiUtil.getTopLevelClass(expression.getReceiver().getPsi())
                "    private Boolean bool;\n" +

                " public void test(Boolean bool ) {" +
                "this.bool = SanityCheck.nonNull(bool);" +
                "}" +
                "}"))
            .run()
            .expectClean();
    }
    
    @Test
    public void testBasicWarning() {


        lint().files(
            java("" +
                "package test.pkg;\n" +
                "public class TestClass1 {\n" +
                "public static class SanityCheck{public static <T> T nonNull(T test) {return test;}}" + //PsiUtil.getTopLevelClass(expression.getReceiver().getPsi())
                "    private Boolean bool;\n" +

                " public void test(Boolean bool ) {" +
                        "SanityCheck.nonNull(bool);" +
                         "this.bool = bool;" +
                        "}" +
                "}"))
            .run()
            .expect("src/test/pkg/TestClass1.java:4: Warning: You can assign the null check here. [NonNullAssignment]\n" +
                " public void test(Boolean bool ) {SanityCheck.nonNull(bool);this.bool = bool;}}\n" +
                "                                  ~~~~~~~~~~~~~~~~~~~~~~~~~\n" +
                "0 errors, 1 warnings");
    }


    @Test
    public void testBasicWarningWithoutStaticClassName() {


        lint().files(
            java("" +
                "package test.pkg;\n" +
                "import static test.pkg.SanityCheck.*;\n" +
                "public class TestClass1 {\n" +
                "    private Boolean bool;\n" +
                " public void test(Boolean bool ) {" +
                "nonNull(bool);" +
                "this.bool = bool;" +
                "}" +
                "}"),
            java("" +
                "package test.pkg;\n" +

                "public class SanityCheck{public static <T> T nonNull(T test) {return test;}}"

                )
                )

            .run()
            .expect("src/test/pkg/TestClass1.java:5: Warning: You can assign the null check here. [NonNullAssignment]\n"
                + " public void test(Boolean bool ) {nonNull(bool);this.bool = bool;}}\n"
                + "                                  ~~~~~~~~~~~~~\n"
                + "0 errors, 1 warnings");
    }

    @Override
    protected boolean allowMissingSdk() {
        return true;
    }

    @Override
    protected Detector getDetector() {
        return new NotNullAssignmentDetector();
    }

    @Override
    protected List<Issue> getIssues() {
        return Collections.singletonList(NotNullAssignmentDetector.ISSUE);
    }
}
