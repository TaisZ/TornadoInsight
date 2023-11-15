package com.tais.tornado_plugins.dynamicInspection;

import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.impl.PsiManagerImpl;
import com.tais.tornado_plugins.util.TornadoTWTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class DynamicInspection {
    public static void process(Project project, ArrayList<PsiMethod> methodArrayList){
        try {
            CodeGenerator.fileCreationHandler(project,methodArrayList, TornadoTWTask.getImportCodeBlock());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
