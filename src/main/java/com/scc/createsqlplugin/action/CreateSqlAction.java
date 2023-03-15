package com.scc.createsqlplugin.action;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.PathUtil;
import com.intellij.ide.IdeView;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiDirectory;
import com.scc.createsqlplugin.database.DataBaseParser;
import com.scc.createsqlplugin.database.DatabaseEntity;

import java.nio.file.Path;

/**
 * @author: scc
 * @description:
 * @date:2023/3/7
 */
public class CreateSqlAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {


        IdeView ideView = e.getRequiredData(LangDataKeys.IDE_VIEW);

        PsiDirectory directory = ideView.getOrChooseDirectory();
        String path = directory.getVirtualFile().getPath();

    }


}
