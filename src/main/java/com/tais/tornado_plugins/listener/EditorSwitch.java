package com.tais.tornado_plugins.listener;

import com.intellij.ide.DataManager;
import com.intellij.openapi.fileEditor.FileEditorManagerEvent;
import com.intellij.openapi.fileEditor.FileEditorManagerListener;
import com.intellij.openapi.wm.ToolWindowManager;
import com.tais.tornado_plugins.ui.toolwindow.TornadoToolsWindow;
import com.tais.tornado_plugins.util.DataKeys;
import com.tais.tornado_plugins.util.TornadoTWTask;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class EditorSwitch implements FileEditorManagerListener {

    //When editor selection changed, refresh the window tool
    @Override
    public void selectionChanged(@NotNull FileEditorManagerEvent event) {
        //ProblemMethods.getInstance().clear();
        if (ToolWindowManager.getInstance(event.getManager().
                getProject()).getToolWindow("TornadoVM") == null) return;
        TornadoTWTask.refresh(event.getManager().getProject(), event.getNewFile(), TornadoToolsWindow.getListModel());
        TornadoTWTask.refresh(event.getManager().getProject(),event.getNewFile(),
                (DefaultListModel<String>) DataManager.getInstance().getDataContext().getData(DataKeys.TORNADOINSIGHT_LIST_MODEL));
    }
}
