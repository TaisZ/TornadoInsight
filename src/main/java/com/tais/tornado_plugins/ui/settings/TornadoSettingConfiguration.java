package com.tais.tornado_plugins.ui.settings;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Objects;

public class TornadoSettingConfiguration implements Configurable {
    private TornadoSettingsComponent mySettingsComponent;

    @Nls(capitalization =  Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return "TornadoVM";
    }

    @Override
    public @Nullable JComponent createComponent() {
        mySettingsComponent = new TornadoSettingsComponent();
        return mySettingsComponent.getPanel();
    }

    @Override
    public boolean isModified() {
        TornadoSettingState settings = TornadoSettingState.getInstance();
        boolean modified = !mySettingsComponent.getJava21Path().equals(settings.Java21);
        modified |= !mySettingsComponent.getTornadoEnvPath().equals(settings.TornadoRoot);
        modified |= mySettingsComponent.getParameterSize() != settings.parameterSize;
        return modified;
    }

    @Override
    public void apply() throws ConfigurationException {
        TornadoSettingState settings = TornadoSettingState.getInstance();
        String error = mySettingsComponent.isValidPath();
        if (!Objects.equals(error, "")){
            settings.isValid = false;
            throw new ConfigurationException(error);
        }
        settings.isValid = true;
        settings.TornadoRoot = mySettingsComponent.getTornadoEnvPath();
        settings.Java21 = mySettingsComponent.getJava21Path();
        settings.parameterSize = mySettingsComponent.getParameterSize();
    }

    //The method is invoked immediately after createComponent().
    @Override
    public void reset() {
        TornadoSettingState settings = TornadoSettingState.getInstance();
        mySettingsComponent.setTornadoEnvPath(settings.TornadoRoot);
        mySettingsComponent.setJava21Path(settings.Java21);
        mySettingsComponent.setParameterSize(settings.parameterSize);
    }

    @Override
    public void disposeUIResources() {
        mySettingsComponent = null;
    }
}
