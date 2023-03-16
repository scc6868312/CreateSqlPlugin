package com.scc.createsqlplugin.component;

import com.intellij.execution.DefaultExecutionResult;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.ExecutionManager;
import com.intellij.execution.ExecutionResult;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.RunProfile;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.filters.TextConsoleBuilder;
import com.intellij.execution.filters.TextConsoleBuilderFactory;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.execution.ui.ExecutionConsole;
import com.intellij.execution.ui.RunContentDescriptor;
import com.intellij.execution.ui.RunnerLayoutUi;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.util.Disposer;
import com.intellij.ui.content.Content;
import com.scc.createsqlplugin.runner.AbstractExecuteRunner;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.io.OutputStream;
import java.util.function.Function;


/**
 * @author: scc
 * @description:
 * @date: 2023/3/15
 */
public class CustomExecutor implements Disposable {

    private ConsoleView consoleView = null;

    private Project project = null;

    private Runnable rerunAction;
    private Runnable stopAction;

    private Computable<Boolean> stopEnabled;


    public CustomExecutor withReturn(Runnable returnAction) {
        this.rerunAction = returnAction;
        return this;
    }

    public CustomExecutor withStop(Runnable stopAction, Computable<Boolean> stopEnabled) {
        this.stopAction = stopAction;
        this.stopEnabled = stopEnabled;
        return this;
    }

    public CustomExecutor(@NotNull Project project) {
        this.project = project;
        this.consoleView = createConsoleView(project);
    }

    private ConsoleView createConsoleView(Project project) {
        TextConsoleBuilder consoleBuilder = TextConsoleBuilderFactory.getInstance().createBuilder(project);
        ConsoleView console = consoleBuilder.getConsole();
        return console;
    }

    @Override
    public void dispose() {
        Disposer.dispose(this);
    }

    public void run(AbstractExecuteRunner runner) {
        if (project.isDisposed()) {
            return;
        }
        if (runner == null) {
            return;
        }
        runner.setConsoleView(consoleView);
        Executor executor = CustomRunExecutor.getRunExecutorInstance();
        if (executor == null) {
            return;
        }

        final RunnerLayoutUi.Factory factory = RunnerLayoutUi.Factory.getInstance(project);
        RunnerLayoutUi layoutUi = factory.create(runner.getRunnerId(), "runnerTitle", "sessionName", project);
        final JPanel consolePanel = createConsolePanel(consoleView);

        RunContentDescriptor descriptor = new RunContentDescriptor(new RunProfile() {
            @Nullable
            @Override
            public RunProfileState getState(@NotNull Executor executor, @NotNull ExecutionEnvironment environment) throws ExecutionException {
                return environment.getState();
            }

            @NotNull
            @Override
            public String getName() {
                return "sqlExecute";
            }

            @Nullable
            @Override
            public Icon getIcon() {
                return null;
            }
        }, new DefaultExecutionResult(), layoutUi);
        descriptor.setExecutionId(System.nanoTime());

        final Content content = layoutUi.createContent("contentId", consolePanel, "", AllIcons.Debugger.Console, consolePanel);
        content.setCloseable(true);
        layoutUi.addContent(content);
        layoutUi.getOptions().setLeftToolbar(createActionToolbar(consolePanel, consoleView), "RunnerToolbar");


        Disposer.register(descriptor, this);

        Disposer.register(content, consoleView);
        if (stopAction != null) {
            Disposer.register(consoleView, () -> stopAction.run());
        }

        ExecutionManager.getInstance(project).getContentManager().showRunContent(executor, descriptor);
    }

    private ActionGroup createActionToolbar(JPanel consolePanel, ConsoleView consoleView) {
        final DefaultActionGroup actionGroup = new DefaultActionGroup();
          actionGroup.add(new RerunAction(consolePanel, consoleView));
        actionGroup.add(new StopAction());
        actionGroup.add(consoleView.createConsoleActions()[2]);
        actionGroup.add(consoleView.createConsoleActions()[3]);
        actionGroup.add(consoleView.createConsoleActions()[5]);
        return actionGroup;
    }

    private JPanel createConsolePanel(ConsoleView consoleView) {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(1, 1));
        panel.add(consoleView.getComponent());
        return panel;
    }

    private class RerunAction extends AnAction implements DumbAware {
        private final ConsoleView consoleView;

        public RerunAction(JComponent consolePanel, ConsoleView consoleView) {
            super("Rerun", "Rerun", AllIcons.Actions.Restart);
            this.consoleView = consoleView;
            registerCustomShortcutSet(CommonShortcuts.getRerun(), consolePanel);
        }

        @Override
        public void actionPerformed(AnActionEvent e) {
            Disposer.dispose(consoleView);
            rerunAction.run();
        }

        @Override
        public void update(AnActionEvent e) {
            e.getPresentation().setVisible(rerunAction != null);
            e.getPresentation().setIcon(AllIcons.Actions.Restart);
        }
    }
    private class StopAction extends AnAction implements DumbAware {
        public StopAction() {
            super("Stop", "Stop", AllIcons.Actions.Suspend);
        }

        @Override
        public void actionPerformed(AnActionEvent e) {
            stopAction.run();
        }

        @Override
        public void update(AnActionEvent e) {
            e.getPresentation().setVisible(stopAction != null);
            e.getPresentation().setEnabled(stopEnabled != null && stopEnabled.compute());
        }
    }


    public ConsoleView getConsoleView() {
        return consoleView;
    }
}