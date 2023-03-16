package com.scc.createsqlplugin.action;

import com.intellij.execution.Executor;
import com.intellij.execution.impl.RunManagerImpl;
import com.intellij.execution.impl.RunnerAndConfigurationSettingsImpl;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.runners.ProgramRunner;
import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.ide.IdeView;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDirectory;
import com.scc.createsqlplugin.component.CustomExecutor;
import com.scc.createsqlplugin.component.CustomRunExecutor;
import com.scc.createsqlplugin.constant.ExecutorRegistry;
import com.scc.createsqlplugin.executesql.SqlScriptController;
import com.scc.createsqlplugin.runner.AbstractExecuteRunner;
import com.scc.createsqlplugin.runner.CurrExecuteRunner;
import com.scc.createsqlplugin.util.ConfigUtil;
import org.jetbrains.annotations.NotNull;

/**
 * @author: scc
 * @description:
 * @date:2023/3/14
 */
public class ExecuteCurrVersionAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project == null) {
            return;
        }

        IdeView ideView = e.getRequiredData(LangDataKeys.IDE_VIEW);

        PsiDirectory directory = ideView.getOrChooseDirectory();
        String path = directory.getVirtualFile().getPath();
        runExecutor(project, path);
    }


    public void runExecutor(Project project, String path) {
        CustomExecutor customExecutor = ExecutorRegistry.getExecutor(project);
        // 设置restart和stop
        customExecutor.withReturn(() -> runExecutor(project, path))
                .withStop(() -> ConfigUtil.setRunning(project, false), () -> ConfigUtil.getRunning(project));
        AbstractExecuteRunner abstractExecuteRunner = new CurrExecuteRunner();
        abstractExecuteRunner.setRunFunction(consoleView -> SqlScriptController.runCurrVersionScripts(path, consoleView));
        Executor executor = CustomRunExecutor.getRunExecutorInstance();
        if (executor == null) {
            return;
        }
        ExecutionEnvironment executionEnvironment = new ExecutionEnvironment(executor, abstractExecuteRunner, new RunnerAndConfigurationSettingsImpl(RunManagerImpl.getInstanceImpl(project)), project);
        customExecutor.run(abstractExecuteRunner);
        try {
            abstractExecuteRunner.execute(executionEnvironment);
        } catch (Exception ex) {
            ex.printStackTrace();
            customExecutor.getConsoleView().print("执行sql时出错" + ex.getMessage(), ConsoleViewContentType.ERROR_OUTPUT);
        }

    }
}
