package com.tais.tornado_plugins.service;

import com.intellij.openapi.project.Project;

public class TWTasksButtonEvent {
    public void pressButton(Project project) {
        throw new RuntimeException("1");
//        List selectedValuesList = TornadoToolsWindow.getToolsWindow().getTasksList().getSelectedValuesList();
//        if (selectedValuesList.isEmpty()) {
//            new EmptySelectionWarningDialog().show();
//        } else {
//            ArrayList<PsiMethod> methodList = TornadoTWTask.getMethods(selectedValuesList);
//            DynamicInspection.process(project, methodList);
//
//        }
    }

}
