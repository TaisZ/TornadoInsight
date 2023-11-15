package com.tais.tornado_plugins.listener;

import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ex.ToolWindowManagerListener;
import com.intellij.util.messages.MessageBus;
import com.tais.tornado_plugins.message.TornadoTaskRefreshListener;
import com.tais.tornado_plugins.util.TornadoTWTask;
import org.jetbrains.annotations.NotNull;

public class ToolWindowOpen implements ToolWindowManagerListener {

    @Override
    public void toolWindowShown(@NotNull ToolWindow toolWindow) {
        ToolWindowManagerListener.super.toolWindowShown(toolWindow);
        if (toolWindow.getId().equals("TornadoVM")) {
            toolWindow.getProject().getMessageBus().syncPublisher(TornadoTaskRefreshListener.REFRESH_TOPIC).refresh();
        }
    }
}
