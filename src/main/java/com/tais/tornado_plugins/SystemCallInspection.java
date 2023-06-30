package com.tais.tornado_plugins;

import com.intellij.codeInspection.AbstractBaseJavaLocalInspectionTool;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.JavaElementVisitor;
import com.intellij.psi.JavaRecursiveElementVisitor;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiReferenceExpression;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class SystemCallInspection extends AbstractBaseJavaLocalInspectionTool {
    public @NotNull PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, boolean isOnTheFly) {
        return new JavaElementVisitor() {
            @Override
            public void visitAnnotation(PsiAnnotation annotation) {
                super.visitAnnotation(annotation);
                if (Objects.requireNonNull(annotation.getQualifiedName()).endsWith("Parallel") ||
                        annotation.getQualifiedName().endsWith("Reduce")){
                    PsiMethod parent = PsiTreeUtil.getParentOfType(annotation,PsiMethod.class);
                    assert parent != null;
                    parent.accept(new JavaRecursiveElementVisitor() {
                        public void visitReferenceExpression(PsiReferenceExpression expression) {
                            super.visitReferenceExpression(expression);
                            PsiElement resolved = expression.resolve();
                            if (resolved instanceof PsiField) {
                                PsiField field = (PsiField) resolved;
                                PsiClass containingClass = field.getContainingClass();
                                if (containingClass != null && "java.lang.System".equals(containingClass.getQualifiedName())) {
                                    holder.registerProblem(expression,
                                            "TornadoVM: TornadoVM does not support System class",
                                            ProblemHighlightType.ERROR);
                                }
                            }
                        }
                    });
                }
            };
        };
    }
}
