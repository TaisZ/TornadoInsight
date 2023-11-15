package com.tais.tornado_plugins.service;

import com.intellij.analysis.problemsView.Problem;
import com.intellij.analysis.problemsView.ProblemsCollector;
import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.impl.PsiManagerImpl;
import com.tais.tornado_plugins.message.TornadoTaskRefreshListener;
import com.tais.tornado_plugins.util.DataKeys;
import com.tais.tornado_plugins.util.TornadoTWTask;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.Collection;
import java.util.Objects;

public class RefreshAction extends AnAction{

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Objects.requireNonNull(e.getProject()).getMessageBus().
                syncPublisher(TornadoTaskRefreshListener.REFRESH_TOPIC).refresh();
    }
}