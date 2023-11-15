package com.tais.tornado_plugins.message;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.messages.Topic;

public interface TornadoTaskRefreshListener {
    Topic<TornadoTaskRefreshListener> REFRESH_TOPIC =
            Topic.create("Tornado task update", TornadoTaskRefreshListener.class);

    void refresh();

    void refresh(Project project, VirtualFile newFile);
}
