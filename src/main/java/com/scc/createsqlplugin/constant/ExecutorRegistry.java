package com.scc.createsqlplugin.constant;

import com.intellij.openapi.project.Project;
import com.scc.createsqlplugin.component.CustomExecutor;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: scc
 * @description:
 * @date:2023/3/16
 */
public final class ExecutorRegistry {

    private static  final ConcurrentHashMap<Project, CustomExecutor> CUSTOM_EXECUTOR_MAP = new ConcurrentHashMap<>();

    public static CustomExecutor getExecutor(Project project) {
        return CUSTOM_EXECUTOR_MAP.computeIfAbsent(project, CustomExecutor::new);
    }

}
