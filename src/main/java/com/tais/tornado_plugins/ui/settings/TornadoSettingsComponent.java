package com.tais.tornado_plugins.ui.settings;

import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.CapturingProcessHandler;
import com.intellij.execution.process.ProcessOutput;
import com.intellij.execution.util.ExecUtil;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.ui.IdeBorderFactory;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;
import com.intellij.util.ui.UI;

import javax.swing.*;
import java.util.concurrent.atomic.AtomicReference;

public class TornadoSettingsComponent {

    private final JPanel myMainPanel;

    private final TextFieldWithBrowseButton myTornadoEnv = new TextFieldWithBrowseButton();

    private final TextFieldWithBrowseButton myJava21Path = new TextFieldWithBrowseButton();

    private final JBTextField myParameterSize = new JBTextField(4);

    public TornadoSettingsComponent() {
        myTornadoEnv.addBrowseFolderListener("TornadoVM Root Folder", "Choose the .sh file",
                null,
                new FileChooserDescriptor(false, true, false, false, false, false) {
                });

        myJava21Path.addBrowseFolderListener("Java21 Home", "Choose the Java_Home for Java 21",
                null,
                new FileChooserDescriptor(false, true, false, false, false, false) {
                });


        String INNER_COMMENT = "<p>The environment variable file for TornadoVM is usually \\\"TornadoVM/setvars.sh\\\"." +
                "This file allows the plugin to call your host's TornadoVM for further analysis of Tornado methods.</p>";

        JPanel innerGrid = UI.PanelFactory.grid().splitColumns()
                .add(UI.PanelFactory.panel(myTornadoEnv).withLabel("TornadoVM root: "))
                .add(UI.PanelFactory.panel(myJava21Path).withLabel("Path to Java 21: "))
                .createPanel();

        JPanel panel = UI.PanelFactory.panel(innerGrid).withComment(INNER_COMMENT).createPanel();
        panel.setBorder(IdeBorderFactory.createTitledBorder("TornadoVM Runtime"));
        JPanel Java21 = UI.PanelFactory.panel(myParameterSize).withLabel("Parameter size: ").withComment("<p>Parameter size</p>").createPanel();
        Java21.setBorder(IdeBorderFactory.createTitledBorder("Dynamic Inspection"));


        myMainPanel = FormBuilder.createFormBuilder()
                .addComponent(panel)
                .addComponent(Java21)
                .addComponentFillVertically(new JPanel(), 0)
                .getPanel();
    }

    public JPanel getPanel() {
        return myMainPanel;
    }

    public String getTornadoEnvPath() {
        return myTornadoEnv.getText();
    }

    public void setTornadoEnvPath(String path) {
        myTornadoEnv.setText(path);
    }

    public String getJava21Path() {
        return myJava21Path.getText();
    }

    public void setJava21Path(String path) {
        myJava21Path.setText(path);
    }

    public int getParameterSize() {
        if (myParameterSize.getText().isEmpty()) {
            return 32;
        }
        return Integer.parseInt(myParameterSize.getText());
    }

    public void setParameterSize(int size) {
        myParameterSize.setText(String.valueOf(size));
    }

    public String isValidPath() {
        String path = myTornadoEnv.getText() + "/setvars.sh";
        String JavaPath = myJava21Path.getText();
        AtomicReference<String> stringAtomicReference = new AtomicReference<>();
        stringAtomicReference.set("");
        if (StringUtil.isEmpty(path))
            return "Empty TornadoVM Path";
        if (StringUtil.isEmpty(JavaPath))
            return "Empty Java Path";

        ProgressManager.getInstance().runProcessWithProgressSynchronously(() -> {
            GeneralCommandLine commandLine = new GeneralCommandLine();
            commandLine.setExePath("/bin/sh");
            commandLine.addParameter("-c");
            commandLine.addParameter("source " + path + ";tornado --device");
            try {
                CapturingProcessHandler handler = new CapturingProcessHandler(commandLine);
                ProcessOutput output = handler.runProcess();
                if (output.getExitCode() != 0) {
                    stringAtomicReference.set("Invalid TornadoVM path");
                }
            } catch (Exception e) {
                stringAtomicReference.set("Invalid TornadoVM path!");
            }

            commandLine = new GeneralCommandLine();
            commandLine.setExePath(JavaPath + "/java");
            commandLine.addParameter("-version");
            try {
                ProcessOutput processOutput = ExecUtil.execAndGetOutput(commandLine);
                if (!processOutput.toString().contains("java version \"21\"")) {
                    stringAtomicReference.set("Java version is not 21");
                }
            } catch (Exception e) {
                stringAtomicReference.set("Invalid Java path!");
            }
        }, "Validating..", true, null);
        return stringAtomicReference.get();
    }
}

