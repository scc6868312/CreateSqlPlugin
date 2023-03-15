package com.scc.createsqlplugin.runner;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.RunProfile;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.runners.ProgramRunner;
import com.intellij.execution.ui.ConsoleView;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

/**
 * @author: scc
 * @description:
 * @date:2023/3/15
 */
public abstract class AbstractExecuteRunner implements ProgramRunner {



    private Function<ConsoleView, Boolean> runFunction;

    private ConsoleView consoleView;


    public void setRunFunction(Function<ConsoleView, Boolean> runFunction) {
        this.runFunction = runFunction;
    }

    public ConsoleView getConsoleView() {
        return consoleView;
    }


    public void setConsoleView(ConsoleView consoleView) {
        this.consoleView = consoleView;
    }

    @Override
    public boolean canRun(@NotNull String executorId, @NotNull RunProfile profile) {
        return true;
    }

    @Override
    public void execute(@NotNull ExecutionEnvironment environment) throws ExecutionException {
        new Thread(() -> runFunction.apply(consoleView)).start();
    }
}
