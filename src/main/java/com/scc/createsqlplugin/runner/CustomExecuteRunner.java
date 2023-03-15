package com.scc.createsqlplugin.runner;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

/**
 * @author: scc
 * @description:
 * @date:2023/3/15
 */
public class CustomExecuteRunner extends AbstractExecuteRunner{
    @Override
    public @NotNull @NonNls String getRunnerId() {
        return "customRunner";
    }
}
