package com.tais.tornado_plugins.listener;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationAction;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManagerListener;
import com.tais.tornado_plugins.ui.settings.TornadoSettingState;
import org.jetbrains.annotations.NotNull;

public class TornadoSettingListener implements ProjectManagerListener {

    @Override
    public void projectOpened(@NotNull Project project) {
        if (TornadoSettingState.getInstance().TornadoRoot == null) {
            TornadoSettingState.getInstance().isValid = false;
            Notification notification = new Notification("Print", "TornadoVM",
                    "Please configure the TornadoVM environment variable file", NotificationType.INFORMATION);
            notification.addAction(new OpenTornadoSettingAction());
            Notifications.Bus.notify(notification, project);
        }else {
            TornadoSettingState.getInstance().isValid = true;
        }
    }

     static class OpenTornadoSettingAction extends NotificationAction {

        public OpenTornadoSettingAction() {
            super("Configure TornadoVM");
        }

        @Override
        public void actionPerformed(@NotNull AnActionEvent e, @NotNull Notification notification) {
            // To show specific Configurable, TornadoVM
            ShowSettingsUtil.getInstance().showSettingsDialog(e.getProject(), "TornadoVM");
            notification.expire();
        }
    }
}
