package com.scc.createsqlplugin.window;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.map.multi.RowKeyTable;
import cn.hutool.core.map.multi.Table;
import cn.hutool.core.text.StrSplitter;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.MessageDialogBuilder;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.openapi.util.ThrowableComputable;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.scc.createsqlplugin.component.CreateSqlForm;
import com.scc.createsqlplugin.config.ChoiceTable;
import com.scc.createsqlplugin.config.DbDriver;
import com.scc.createsqlplugin.database.DataBaseParser;
import com.scc.createsqlplugin.database.Database;
import com.scc.createsqlplugin.database.MySQL;
import com.scc.createsqlplugin.database.Oracle;
import com.scc.createsqlplugin.database.Postgresql;
import com.scc.createsqlplugin.util.FileUtils;
import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.scc.createsqlplugin.constant.Constant.*;

/**
 * @author: scc
 * @description:
 * @date:2023/3/14
 */
public class CreateSqlWindow implements ToolWindowFactory {

    private JTextArea mysqlText;
    private JTextArea oracleText;
    private JTextArea postgreText;
    private JComboBox<String> tableBox;
    private JTextField processId;

    private JTextField processUser;

    private JTextField processDesc;
    private JButton buttonOK;
    private JButton buttonCancel;

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        toolWindow.setTitle("生成SQL");
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        CreateSqlForm createSqlForm = new CreateSqlForm();
        this.tableBox = createSqlForm.getTableBox();
        this.mysqlText = createSqlForm.getMysqlText();
        this.oracleText = createSqlForm.getOracleText();
        this.postgreText = createSqlForm.getPostgreText();
        this.processId = createSqlForm.getProcessId();
        this.processDesc = createSqlForm.getProcessDesc();
        this.processUser = createSqlForm.getProcessUser();
        this.buttonOK = createSqlForm.getButtonOK();
        this.buttonCancel = createSqlForm.getButtonCancel();
        Content content = contentFactory.createContent(createSqlForm.getContentPane(), "", false);
        content.setToolwindowTitle("投顾交易脚本工具");
        toolWindow.getContentManager().addContent(content);


        String basePath = FileUtils.getProjectBasePath(project);
        assert basePath != null;
        String configPath = basePath.concat(File.separator).concat("pluginConfig");

        Table<String, String, String> choiceTable = new RowKeyTable<>();
        Map<String, String> tableTypeMap = new HashMap<>();
        ChoiceTable.parseChoiceTable(configPath).forEach(table -> {
            choiceTable.put(table.getRowKey().getKey(), table.getColumnKey(), table.getValue());
            tableTypeMap.put(table.getRowKey().getKey(), table.getRowKey().getValue());
        });
        choiceTable.rowKeySet().forEach(tableBox::addItem);
        tableBox.setSelectedIndex(-1);
        buttonOK.addActionListener(e -> onOK(configPath, choiceTable, tableTypeMap, project));
        buttonCancel.addActionListener(e -> onCancel(configPath));
    }

    @Override
    public void init(@NotNull ToolWindow toolWindow) {
        ToolWindowFactory.super.init(toolWindow);
    }

    private void onCancel(String configPath) {
        this.tableBox.removeAllItems();
        Table<String, String, String> choiceTable = new RowKeyTable<>();
        Map<String, String> tableTypeMap = new HashMap<>();
        ChoiceTable.parseChoiceTable(configPath).forEach(table -> {
            choiceTable.put(table.getRowKey().getKey(), table.getColumnKey(), table.getValue());
            tableTypeMap.put(table.getRowKey().getKey(), table.getRowKey().getValue());
        });
        choiceTable.rowKeySet().forEach(tableBox::addItem);
        this.mysqlText.setText("");
        this.oracleText.setText("");
        this.postgreText.setText("");
        this.processId.setText("");
        this.processUser.setText("");
        this.processDesc.setText("");
        this.tableBox.setSelectedIndex(-1);
        Messages.showInfoMessage("重置成功", "SUCCESS");
    }

    private void onOK(String configPath, Table<String, String, String> choiceTable, Map<String, String> tableTypeMap, Project project) {
        ValidationInfo validationInfo = doValidate();
        if (validationInfo != null) {
            Messages.showMessageDialog(validationInfo.message, "校验不通过", Messages.getErrorIcon());
        } else {
            MessageDialogBuilder.YesNo yesNo = MessageDialogBuilder.yesNo("确认", "请确认是否要生成sql？");
            if (yesNo.isYes()) {
                String table = (String) this.tableBox.getSelectedItem();
                String mysqlText = this.mysqlText.getText();
                String oracleText = this.oracleText.getText();
                String postgresqlText = this.postgreText.getText();
                String ticketDescText = this.processDesc.getText();
                String ticketIdText = this.processId.getText();
                String ticketUserText = this.processUser.getText();

                String mysqlPath = choiceTable.get(table, MYSQL_DB);
                String oraclePath = choiceTable.get(table, ORACLE_DB);
                String postgresqlPath = choiceTable.get(table, POSTGRESQL_DB);


                String tableType = tableTypeMap.get(table);
                Database database = DataBaseParser.findDatabase(configPath, tableType);
                MySQL mySQL = database.getMySQL();
                Oracle oracle = database.getOracle();
                Postgresql postgresql = database.getPostgresql();

                try {
                    ProgressManager.getInstance().runProcessWithProgressSynchronously((ThrowableComputable<String, Exception>) () -> {

                        assert mySQL != null;
                        String mysqlError = executeInDb(MYSQL_DB, DataBaseParser.parseJdbcUrlByDatasource(mySQL, MYSQL_DB), mySQL.getUser(), mySQL.getPassword(), mysqlText);

                        if (StringUtils.isNotBlank(mysqlError)) {
                            throw new RuntimeException(mysqlError);
                        }
                        return null;
                    }, "正在执行mysql脚本中", false, project);
                } catch (Exception ex) {
                    MessageDialogBuilder.YesNo error = MessageDialogBuilder.yesNo("ERROR", "执行mysql脚本到数据库出错:\n" + ex.getMessage());
                    error.yesText("忽略并继续生成");
                    error.noText("退出");
                    if (!error.isYes()) {
                        return;
                    }
                }

                try {
                    ProgressManager.getInstance().runProcessWithProgressSynchronously((ThrowableComputable<String, Exception>) () -> {
                        assert oracle != null;
                        String oracleError = executeInDb(ORACLE_DB, DataBaseParser.parseJdbcUrlByDatasource(oracle, ORACLE_DB), oracle.getUser(), oracle.getPassword(), oracleText);
                        if (StringUtils.isNotBlank(oracleError)) {
                            throw new RuntimeException(oracleError);
                        }
                        return null;
                    }, "正在执行oracle脚本中", false, project);
                } catch (Exception ex2) {
                    MessageDialogBuilder.YesNo error = MessageDialogBuilder.yesNo("ERROR", "执行oracle脚本到数据库出错:\n" + ex2.getMessage());
                    error.yesText("忽略并继续生成");
                    error.noText("退出");
                    if (!error.isYes()) {
                        return;
                    }
                }

                try {
                    ProgressManager.getInstance().runProcessWithProgressSynchronously((ThrowableComputable<String, Exception>) () -> {
                        assert postgresql != null;
                        String pgError = executeInDb(POSTGRESQL_DB, DataBaseParser.parseJdbcUrlByDatasource(postgresql, POSTGRESQL_DB), postgresql.getUser(), postgresql.getPassword(), postgresqlText);
                        if (StringUtils.isNotBlank(pgError)) {
                            throw new RuntimeException(pgError);
                        }
                        return null;
                    }, "正在执行postgresql脚本中", false, project);
                } catch (Exception ex3) {
                    MessageDialogBuilder.YesNo error = MessageDialogBuilder.yesNo("ERROR", "执行postgresql脚本到数据库出错:\n" + ex3.getMessage());
                    error.yesText("忽略并继续生成");
                    error.noText("退出");
                    if (!error.isYes()) {
                        return;
                    }
                }


                putSqlToFile(mysqlText, ticketDescText, ticketIdText, ticketUserText, mysqlPath, project);
                putSqlToFile(oracleText, ticketDescText, ticketIdText, ticketUserText, oraclePath, project);
                putSqlToFile(postgresqlText, ticketDescText, ticketIdText, ticketUserText, postgresqlPath, project);


                Messages.showMessageDialog("生成sql成功！", "SUCCESS", Messages.getInformationIcon());
                this.mysqlText.setText("");
                this.oracleText.setText("");
                this.postgreText.setText("");
                this.processId.setText("");
                this.processUser.setText("");
                this.processDesc.setText("");
                this.tableBox.setSelectedIndex(-1);
            }


        }
    }

    private ValidationInfo doValidate() {
        String table = (String) this.tableBox.getSelectedItem();
        String mysqlText = this.mysqlText.getText();
        String oracleText = this.oracleText.getText();
        String postgresqlText = this.postgreText.getText();
        String ticketDescText = this.processDesc.getText();
        String ticketIdText = this.processId.getText();
        String ticketUserText = this.processUser.getText();

        String errorMsg = "";
        if (StringUtils.isBlank(table)) {
            errorMsg = "请选择表！";
        } else if (StringUtils.isBlank(ticketIdText)) {
            errorMsg = "请输入修改单！";
        } else if (StringUtils.isBlank(ticketUserText)) {
            errorMsg = "请输入修改人！";
        } else if (StringUtils.isBlank(ticketDescText)) {
            errorMsg = "请输入修改描述！";
        } else if (StringUtils.isBlank(mysqlText) || checkSqlText(mysqlText, EDbVendor.dbvmysql)) {
            errorMsg = "Mysql脚本不合法，请检查mysql脚本！";
        } else if (StringUtils.isBlank(oracleText) || checkSqlText(oracleText, EDbVendor.dbvoracle)) {
            errorMsg = "Oracle脚本不合法，请检查oracle脚本！";
        } else if (StringUtils.isBlank(postgresqlText) || (!table.equals("DDL") && checkSqlText(postgresqlText, EDbVendor.dbvpostgresql))) {
            errorMsg = "Postgresql脚本不合法，请检查postgresql脚本！";
        }

        if (StringUtils.isBlank(errorMsg)) {
            return null;
        } else {
            return new ValidationInfo(errorMsg);
        }
    }

    private boolean checkSqlText(String sqlText, EDbVendor eDbVendor) {
        TGSqlParser sqlParser = new TGSqlParser(eDbVendor);
        sqlParser.setSqltext(sqlText);
        boolean result = sqlParser.parse() != 0;
        System.out.println(sqlParser.getErrormessage());
        return result;
    }


    private String executeInDb(String dbType, String jdbcPath, String user, String password, String sql) {
        try {
            Class.forName(DbDriver.getDriverByDbType(dbType).getDriverName());
            Connection connection = DriverManager.getConnection(jdbcPath, user, password);
            switch (dbType) {
                case MYSQL_DB:
                    PreparedStatement statement = null;
                    connection.setAutoCommit(true);
                    Matcher myMatch = Pattern.compile("delimiter[\\s\\S]+?delimiter.+?;").matcher(sql);
                    List<String> delimiterList = new ArrayList<String>();
                    while (myMatch.find()) {
                        delimiterList.add(myMatch.group(0).replaceAll("delimiter.*", "").replaceAll("end(.+?|)[$][$]", "end;"));
                    }

                    sql = sql.replaceAll("delimiter[\\s\\S]+?delimiter.+?;", "&scc&;");
                    List<String> split = StrSplitter.split(sql, ";\n", true, true);
                    int i = 0;
                    for (String s : split) {
                        if (s.equals("&scc&")) {
                            statement = connection.prepareStatement(delimiterList.get(i));
                            statement.executeUpdate();
                            statement.close();
                            i++;
                        } else {
                            statement = connection.prepareStatement(s);
                            statement.execute();
                            statement.close();
                        }
                    }
                    break;
                case ORACLE_DB:
                    Matcher orMatch = Pattern.compile("declare[\\s\\S]+?end([\\s\\S]|);").matcher(sql);
                    PreparedStatement prepareStatement = connection.prepareStatement("select distinct tablespace_name from dba_indexes where table_owner = '" + user.toUpperCase() + "' and rownum = 1");
                    ResultSet resultSet = prepareStatement.executeQuery();
                    String indexSpace = null;
                    while (resultSet.next()) {
                        indexSpace = resultSet.getString(1);
                    }

                    resultSet.close();
                    prepareStatement.close();

                    PreparedStatement tableStatement = connection.prepareStatement("select default_tablespace from dba_users where username='" + user.toUpperCase() + "'");
                    ResultSet tableResult = tableStatement.executeQuery();
                    String tableSpace = null;
                    while (tableResult.next()) {
                        tableSpace = tableResult.getString(1);
                    }
                    tableResult.close();
                    tableStatement.close();

                    while (orMatch.find()) {
                        assert tableSpace != null;
                        assert indexSpace != null;
                        String matchSql = orMatch.group(0).replaceAll("[$][{]HS_.+_DATA[}]", tableSpace).replaceAll("[$][{]HS_.+_IDX[}]", indexSpace);
                        statement = connection.prepareStatement(matchSql);
                        statement.executeUpdate();
                        statement.close();
                    }
                    break;
                case POSTGRESQL_DB:
                    List<String> functionList = new ArrayList<String>();
                    Matcher pgMatch = Pattern.compile("create function[\\s\\S]+?end([\\s\\S]|)[$]BODY[$];").matcher(sql);
                    while (pgMatch.find()) {
                        functionList.add(pgMatch.group(0));
                    }

                    sql = sql.replaceAll("create function[\\s\\S]+?end([\\s\\S]|)[$]BODY[$];", "&scc&;");
                    List<String> stringList = StrSplitter.split(sql, ";\n", true, true);
                    int index = 0;
                    for (String s : stringList) {
                        if (s.equals("&scc&")) {
                            statement = connection.prepareStatement(functionList.get(index));
                            statement.executeUpdate();
                            statement.close();
                            index++;
                        } else {
                            statement = connection.prepareStatement(s);
                            statement.execute();
                            statement.close();
                        }
                    }
                    break;
            }

            connection.close();
            return "";
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }

    }

    private void putSqlToFile(String sqlText, String ticketDescText, String ticketIdText, String ticketUserText, String path, Project project) {
        String content = FileUtil.readUtf8String(FileUtils.getCurrVersionPatchPath(project) + File.separator + path);
        StringBuilder sb = new StringBuilder();
        Matcher matcher = Pattern.compile("V[0-9][.][0-9][.][0-9]+[.][0-9]+").matcher(content);
        String versionString = null;
        while (matcher.find()) {
            versionString = matcher.group(0);
        }
        String[] split = versionString.split("[.]", -1);
        split[split.length - 1] = Integer.parseInt(split[split.length - 1]) + 1 + "";
        String ticketVersionText = Arrays.stream(split).collect(Collectors.joining("."));
        String description = "-- " + ticketVersionText + "   " + LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE) + "     " + ticketIdText + "   " + ticketUserText + "          " + ticketUserText + "          " + ticketDescText;
        sb.append(content, 0, content.indexOf("-- 修改版本       修改日期     修改单          申请人            修改人           修改说明") + 69)
                .append("\n")
                .append(description)
                .append(content, content.indexOf("-- 修改版本       修改日期     修改单          申请人            修改人           修改说明") + 69, content.indexOf("begin\n" +
                        "  commit;\n" +
                        "end;\n" +
                        "/") > 0 ? content.indexOf("begin\n" +
                        "  commit;\n" +
                        "end;\n" +
                        "/") : content.length())
                .append("\n").append(description).append("  begin\n")
                .append(sqlText).append("\n")
                .append(description).append(" end")
                .append(content.indexOf("begin\n" +
                        "  commit;\n" +
                        "end;\n" +
                        "/") > 0 ? "\nbegin\n" +
                        "  commit;\n" +
                        "end;\n" +
                        "/" : "");
        FileUtil.writeUtf8String(sb.toString(), FileUtils.getCurrVersionPatchPath(project) + File.separator + path);
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
