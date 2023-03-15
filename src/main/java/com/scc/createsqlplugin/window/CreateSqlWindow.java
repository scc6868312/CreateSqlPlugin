package com.scc.createsqlplugin.window;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.scc.createsqlplugin.component.CreateSqlForm;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * @author: scc
 * @description:
 * @date:2023/3/14
 */
public class CreateSqlWindow implements ToolWindowFactory {

    private JTextArea mysqlText;
    private JComboBox<String> tableBox;
    private JTextArea oracleText;
    private JTextArea postgreText;

    private JTextField processId;

    private JTextField processVersion;

    private JTextField processUser;

    private JTextField processDesc;
    private JButton buttonOK;
    private JButton buttonCancel;

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        toolWindow.setTitle("生成SQL");
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        CreateSqlForm createSqlForm = new CreateSqlForm();
        Content content = contentFactory.createContent(createSqlForm.getContentPane(), "", false);
        toolWindow.getContentManager().addContent(content);
    }

    @Override
    public void init(@NotNull ToolWindow toolWindow) {

        ToolWindowFactory.super.init(toolWindow);
    }

    @Override
    public boolean shouldBeAvailable(@NotNull Project project) {
        return ToolWindowFactory.super.shouldBeAvailable(project);
    }

    @Override
    public boolean isDoNotActivateOnStart() {
        return ToolWindowFactory.super.isDoNotActivateOnStart();
    }

}
