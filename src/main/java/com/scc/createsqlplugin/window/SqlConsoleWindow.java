package com.scc.createsqlplugin.window;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.scc.createsqlplugin.component.SqlConsoleForm;
import org.jetbrains.annotations.NotNull;

/**
 * @author: scc
 * @description:
 * @date:2023/3/14
 */
public class SqlConsoleWindow implements ToolWindowFactory {
    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        toolWindow.setTitle("Sql执行日志");
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        SqlConsoleForm sqlConsoleForm = new SqlConsoleForm();
        Content content = contentFactory.createContent(sqlConsoleForm.getContentPanel(), "", false);
        toolWindow.getContentManager().addContent(content);
    }


}
