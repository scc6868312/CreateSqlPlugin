package com.scc.createsqlplugin;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.PathUtil;
import com.intellij.ide.IdeView;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiDirectory;

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

        Path lastPathEle = PathUtil.getLastPathEle(FileUtil.file(path).toPath());
        String pathName = lastPathEle.getFileName().toString();
        if (!pathName.matches("[0-9][.][0-9][.][0-9][.][0-9]")) {
            Messages.showMessageDialog("请选择正确的路径生成Sql！", "Error", Messages.getErrorIcon());
            return;
        }
        String configPath = path.replaceAll("/Sql/.*", "/pluginConfig/");

        GenerateSqlDialog dialog = new GenerateSqlDialog(path, configPath);
        dialog.pack();
        dialog.show();

    }


}
