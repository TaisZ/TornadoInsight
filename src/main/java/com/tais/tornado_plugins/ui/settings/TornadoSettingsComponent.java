package com.tais.tornado_plugins.ui.settings;

import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.ui.IdeBorderFactory;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;
import com.intellij.util.ui.UI;

import javax.swing.*;

public class TornadoSettingsComponent {

    private final JPanel myMainPanel;
    private final TextFieldWithBrowseButton myTornadoEnv = new TextFieldWithBrowseButton();

    private final TextFieldWithBrowseButton myJava21Path = new TextFieldWithBrowseButton();
    //todo: input validation
    private final JBTextField myParameterSize = new JBTextField(4);
    public TornadoSettingsComponent() {
        myTornadoEnv.addBrowseFolderListener("TornadoVM Root Folder", "Choose the .sh file",
               null,
                new FileChooserDescriptor(false, true, false, false, false, false) {});

        myJava21Path.addBrowseFolderListener("Java21 Home", "Choose the Java_Home for Java 21",
                null,
                new FileChooserDescriptor(true,false,false,false,false,false){});


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

    public JPanel getPanel(){
        return myMainPanel;
    }

    public String getTornadoEnvPath(){
        return myTornadoEnv.getText();
    }

    public void setTornadoEnvPath(String path){
        myTornadoEnv.setText(path);
    }

    public String getJava21Path(){
        return myJava21Path.getText();
    }

    public void setJava21Path(String path){
        myJava21Path.setText(path);
    }

    public int getParameterSize(){
        if (myParameterSize.getText().isEmpty()){
            return 32;
        }
        return Integer.parseInt(myParameterSize.getText());
    }

    public void setParameterSize(int size){
        myParameterSize.setText(String.valueOf(size));
    }
}

