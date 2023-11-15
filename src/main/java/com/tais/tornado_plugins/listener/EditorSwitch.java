package com.tais.tornado_plugins.listener;

import com.intellij.ide.DataManager;
import com.intellij.openapi.fileEditor.FileEditorManagerEvent;
import com.intellij.openapi.fileEditor.FileEditorManagerListener;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindowManager;
import com.tais.tornado_plugins.message.TornadoTaskRefreshListener;
import com.tais.tornado_plugins.util.DataKeys;
import com.tais.tornado_plugins.util.TornadoTWTask;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class EditorSwitch implements FileEditorManagerListener {

    //When editor selection changed, refresh the window tool
    @Override
    public void selectionChanged(@NotNull FileEditorManagerEvent event) {
        //ProblemMethods.getInstance().clear();
        Project project = event.getManager().getProject();
        if (ToolWindowManager.getInstance(project).getToolWindow("TornadoVM") == null ||
            event.getNewFile() == null) return;


        TornadoTaskRefreshListener tornadoTaskRefreshListener =
                project.getMessageBus().syncPublisher(TornadoTaskRefreshListener.REFRESH_TOPIC);
        tornadoTaskRefreshListener.refresh(project,event.getNewFile());
    }

}
