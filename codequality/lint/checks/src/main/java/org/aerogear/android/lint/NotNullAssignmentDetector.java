package org.aerogear.android.lint;

import com.android.tools.lint.client.api.UElementHandler;
import com.android.tools.lint.detector.api.Category;
import com.android.tools.lint.detector.api.Detector;
import com.android.tools.lint.detector.api.Implementation;
import com.android.tools.lint.detector.api.Issue;
import com.android.tools.lint.detector.api.JavaContext;
import com.android.tools.lint.detector.api.Location;
import com.android.tools.lint.detector.api.Scope;
import com.android.tools.lint.detector.api.Severity;
import com.intellij.psi.JavaElementVisitor;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiMethodCallExpression;

import org.jetbrains.uast.UBlockExpression;
import org.jetbrains.uast.UCallExpression;
import org.jetbrains.uast.UElement;
import org.jetbrains.uast.UExpression;
import org.jetbrains.uast.UIdentifier;
import org.jetbrains.uast.USimpleNameReferenceExpression;
import org.jetbrains.uast.java.JavaUAssignmentExpression;
import org.jetbrains.uast.java.JavaUCodeBlockExpression;
import org.jetbrains.uast.java.JavaUSimpleNameReferenceExpression;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The SanityCheck#nonNull method returns the test parameter.  If there is an assignment of that
 * parameter immediately after the check this detector will find it and alert the developer
 * that they may assign the result of the nonNull check.
 * <p>
 * ex.
 * ```
 * nonNull(value, "SomeValue")
 * this.value = value;
 * <p>
 * should be
 * <p>
 * this.value = nonNull(value, "SomeValue")
 * <p>
 * ```
 */
public class NotNullAssignmentDetector extends Detector implements Detector.UastScanner {

    private static final String SANITY_CHECK_CLASS = "SanityCheck";

    /**
     * Issue describing the problem and pointing to the detector implementation
     */
    public static final Issue ISSUE = Issue.create(
        // ID: used in @SuppressLint warnings etc
        "NonNullAssignment",

        // Title -- shown in the IDE's preference dialog, as category headers in the
        // Analysis results window, etc
        "Assign NonNull",

        // Full explanation of the issue; you can use some markdown markup such as
        // `monospace`, *italic*, and **bold**.
        "SanityChecks.NonNull returns the value under check and may be assigned here rather than elsewhere.",
        Category.CORRECTNESS,
        6,
        Severity.WARNING,
        new Implementation(
            NotNullAssignmentDetector.class,
            Scope.JAVA_FILE_SCOPE));


    @Override
    public List<Class<? extends UElement>> getApplicableUastTypes() {
        return Collections.singletonList(UCallExpression.class);
    }

    @Override
    public void visitMethod(JavaContext context, JavaElementVisitor visitor, PsiMethodCallExpression call, PsiMethod method) {
        super.visitMethod(context, visitor, call, method);
    }

    private JavaUAssignmentExpression findAssignment(UElement expression) {
        if (expression.getUastParent() == null) {
            return null;
        }
        if (expression.getUastParent() instanceof JavaUAssignmentExpression) {
            return (JavaUAssignmentExpression) expression.getUastParent();
        }

        return findAssignment(expression.getUastParent());
    }

    @Override
    public UElementHandler createUastHandler(JavaContext context) {

        return new UElementHandler() {
            @Override
            public void visitCallExpression(UCallExpression expression) {
                if (expression == null || expression.getMethodName() == null) {
                    return;
                }
                if (expression.getMethodName().equals("nonNull")) {

                    //<heck that the method is on the SanityCheck class
                    String methodClassName;
                    try {
                        methodClassName = expression.resolve().getContainingClass().getName();
                    } catch (NullPointerException exception) {
                        exception.printStackTrace();
                        return;
                    }

                    if (!methodClassName.equals(SANITY_CHECK_CLASS)) {
                        return;
                    }

                    //Get the left hand side, if an assignment is found no error
                    JavaUAssignmentExpression assignment = findAssignment(expression);
                    if (assignment != null) {
                        return;
                    }
                    //Check that we are checking a variable and not the result of a calculation
                    if ((expression.getValueArguments().size() < 1) || !(expression.getValueArguments().get(0) instanceof JavaUSimpleNameReferenceExpression)) {
                        return;
                    }

                    UBlockExpression block = findBlock(expression);
                    String variableIdentifier = ((JavaUSimpleNameReferenceExpression) expression.getValueArguments().get(0)).getIdentifier();

                    if (assignmentHappens(block, variableIdentifier)) {
                        Location location = context.getLocation(expression);
                        context.report(ISSUE, expression, location, "You can assign the null check here.");
                        //log issue
                    }

                }
            }
        };
    }

    private boolean assignmentHappens(UBlockExpression block, String variableIdentifier) {
        return  block.getExpressions().stream()
                    .filter((expression) -> expression instanceof JavaUAssignmentExpression)
                    .anyMatch((assignment) -> {
                        UExpression rhs = ((JavaUAssignmentExpression) assignment).getRightOperand();
                        if (rhs instanceof JavaUSimpleNameReferenceExpression) {
                          return (((JavaUSimpleNameReferenceExpression) rhs).getIdentifier().equals(variableIdentifier));
                        }
                        return false;
                    });
    }

    private JavaUCodeBlockExpression findBlock(UElement expression) {
        if (expression.getUastParent() == null) {
            return null;
        }
        if (expression.getUastParent() instanceof JavaUCodeBlockExpression) {
            return (JavaUCodeBlockExpression) expression.getUastParent();
        }

        return findBlock(expression.getUastParent());
    }

}
