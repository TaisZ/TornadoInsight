package com.tais.tornado_plugins.util;

import com.intellij.execution.ui.ConsoleView;
import com.intellij.openapi.actionSystem.DataKey;

public class DataKeys {
    public static final DataKey<ConsoleView> TORNADO_CONSOLE_VIEW = DataKey.create("TORNADO_CONSOLE_VIEW");
}
