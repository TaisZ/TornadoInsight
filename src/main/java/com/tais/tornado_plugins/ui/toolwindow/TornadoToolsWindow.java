package com.tais.tornado_plugins.ui.toolwindow;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.tais.tornado_plugins.listener.ToolWindowOpen;
import com.tais.tornado_plugins.util.TornadoTWTask;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class TornadoToolsWindow implements ToolWindowFactory {
    private static JList list;
    private static DefaultListModel<String> listModel;
    private static TornadoVM toolsWindow;

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        toolsWindow = new TornadoVM(toolWindow);
        list = toolsWindow.getTasksList();
        listModel = (DefaultListModel<String>) list.getModel();
        Content content = ContentFactory.getInstance().
                createContent(toolsWindow.getMainPanel(), "", false);
        toolWindow.getContentManager().addContent(content);
        ToolWindowOpen.RefreshListener.init(project.getMessageBus());
        TornadoTWTask.addTask(project, listModel);
    }

    public static DefaultListModel<String> getListModel() {
        return listModel;
    }

    public static JList getList() {
        return list;
    }

    public static TornadoVM getToolsWindow() {
        return toolsWindow;
    }

}