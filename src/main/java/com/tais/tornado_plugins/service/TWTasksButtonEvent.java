package com.tais.tornado_plugins.service;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiMethod;
import com.tais.tornado_plugins.mockExecution.DynamicInspection;
import com.tais.tornado_plugins.ui.EmptySelectionWarningDialog;
import com.tais.tornado_plugins.ui.TornadoToolsWindow;
import com.tais.tornado_plugins.util.TornadoTWTask;

import java.util.ArrayList;
import java.util.List;

public class TWTasksButtonEvent {
    public void pressButton(Project project) {
        List selectedValuesList = TornadoToolsWindow.getToolsWindow().getTasksList().getSelectedValuesList();
        if (selectedValuesList.isEmpty()) {
            new EmptySelectionWarningDialog().show();
        } else {
            ArrayList<PsiMethod> methodList = TornadoTWTask.getMethods(selectedValuesList);
            DynamicInspection.process(project, methodList);

        }
    }

}
