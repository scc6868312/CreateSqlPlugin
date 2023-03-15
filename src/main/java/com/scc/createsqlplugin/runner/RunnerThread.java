package com.scc.createsqlplugin.runner;

import com.intellij.execution.ui.ConsoleView;

import java.util.concurrent.Callable;
import java.util.function.Function;

/**
 * @author: scc
 * @description:
 * @date:2023/3/15
 */
public class RunnerThread implements Callable<Boolean> {
    private final ConsoleView consoleView;

    private final Function<ConsoleView, Boolean> runFunction;

    public RunnerThread(Function<ConsoleView, Boolean> runFunction, ConsoleView consoleView) {
        this.runFunction = runFunction;
        this.consoleView = consoleView;
    }

    @Override
    public Boolean call() throws Exception {
        return runFunction.apply(consoleView);
    }
}
