package com.tais.tornado_plugins.service;

import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.psi.PsiMethod;
import com.tais.tornado_plugins.ui.toolwindow.EmptySelectionWarningDialog;
import com.tais.tornado_plugins.ui.toolwindow.TornadoVM;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class TWTasksButtonEvent extends AbstractAction {
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
    private final TornadoVM toolWindow;

    public TWTasksButtonEvent(TornadoVM toolWindow) {
        this.toolWindow = toolWindow;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        List<String> selectedValuesList = toolWindow.getTasksList().getSelectedValuesList();
        if (selectedValuesList.isEmpty()) {
            new EmptySelectionWarningDialog().show();}
//        }else {
//            ArrayList<PsiMethod> methodList = TornadoTWTask.getMethods(selectedValuesList);
//            DynamicInspection.process(project, methodList);
//        }
    }


}
