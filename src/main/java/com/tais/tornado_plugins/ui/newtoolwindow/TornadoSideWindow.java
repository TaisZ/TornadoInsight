package com.tais.tornado_plugins.ui.newtoolwindow;

import com.intellij.icons.AllIcons;
import com.intellij.ide.structureView.newStructureView.StructureViewComponent;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileEditor.FileEditorManagerEvent;
import com.intellij.openapi.fileEditor.FileEditorManagerListener;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.psi.SmartPsiElementPointer;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTabbedPane;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.util.messages.MessageBusConnection;
import com.tais.tornado_plugins.entity.PluginConstant;
import com.tais.tornado_plugins.listener.EditorSwitch;
import com.tais.tornado_plugins.listener.ToolWindowOpen;
import com.tais.tornado_plugins.ui.toolwindow.InspectorInfoKt;
import com.tais.tornado_plugins.util.DataKeys;
import com.tais.tornado_plugins.util.MessageBundle;
import com.tais.tornado_plugins.util.TornadoTWTask;
import com.vladsch.flexmark.ext.ins.Ins;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.List;

public class TornadoSideWindow implements ToolWindowFactory, Disposable {

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        ToolWindowContent toolWindowContent = new ToolWindowContent(toolWindow);
        InspectorInfoPanel inspectorInfoPanel = new InspectorInfoPanel(toolWindow);
        Content content = ContentFactory.getInstance().createContent(toolWindowContent, "TornadoVM Tasks", false);
        toolWindow.getContentManager().addContent(content);
        content = ContentFactory.getInstance().createContent(inspectorInfoPanel, "Inspectors Info", false);
        toolWindow.getContentManager().addContent(content);
        ToolWindowOpen.RefreshListener.init(project.getMessageBus());
        TornadoTWTask.addTask(project, toolWindowContent.getModel());
    }

    @Override
    public void dispose() {
    }

    private static class ToolWindowContent extends SimpleToolWindowPanel{

        JBList<String> TornadoList = new JBList<>();

        public ToolWindowContent(ToolWindow toolWindow) {
            super(Boolean.TRUE, Boolean.TRUE);
            SimpleToolWindowPanel simpleToolWindowPanel = new SimpleToolWindowPanel(true, true);

            DefaultListModel<String> model = new DefaultListModel<>();
            TornadoList.setModel(model);
            TornadoList.getEmptyText().setText(MessageBundle.message("ui.toolwindow.defaultText"));
            simpleToolWindowPanel.add(TornadoList);
            setContent(simpleToolWindowPanel);


            ActionToolbar test = ActionManager.getInstance().createActionToolbar(
                    "TornadoInsight Toolbar",
                    (DefaultActionGroup) ActionManager.getInstance().getAction("tornado.bar"),
                    true);
            test.setTargetComponent(simpleToolWindowPanel);
            setToolbar(test.getComponent());
       }

        @Override
        public @Nullable Object getData(@NotNull @NonNls String dataId) {
            if (DataKeys.TORNADOINSIGHT_LIST_MODEL.is(dataId)){
                return getModel();
            }
            if (DataKeys.TORNADO_SELECTED_LIST.is(dataId)){
                return TornadoList.getSelectedValuesList();
            }
            if (DataKeys.TORNADO_LIST.is(dataId)){
                return TornadoList;
            }
            return null;
        }

        public DefaultListModel<String> getModel(){
            return (DefaultListModel<String>) TornadoList.getModel();
        }
    }

    private static class InspectorInfoPanel extends SimpleToolWindowPanel{

        JScrollPane scrollPane = new JBScrollPane(InspectorInfoKt.inspectorPane());

        public InspectorInfoPanel(ToolWindow toolWindow) {
            super(Boolean.TRUE, Boolean.TRUE);
            SimpleToolWindowPanel simpleToolWindowPanel = new SimpleToolWindowPanel(true, true);
            simpleToolWindowPanel.add(scrollPane);
            setContent(simpleToolWindowPanel);
        }
    }

}



