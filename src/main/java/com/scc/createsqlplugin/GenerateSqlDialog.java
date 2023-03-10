package com.scc.createsqlplugin;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.map.multi.RowKeyTable;
import cn.hutool.core.map.multi.Table;
import cn.hutool.core.text.StrSplitter;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.MessageDialogBuilder;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.ValidationInfo;
import com.scc.createsqlplugin.database.DataBaseParser;
import com.scc.createsqlplugin.database.Database;
import com.scc.createsqlplugin.database.MySQL;
import com.scc.createsqlplugin.database.Oracle;
import com.scc.createsqlplugin.database.Postgresql;
import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GenerateSqlDialog extends DialogWrapper {

    private JTextField processIdTextField;

    private JTextField processVersionTextField;

    private JTextField processUserTextField;

    private JTextField processDescTextField;

    private JTextArea mysqlTextArea;
    private JTextArea oracleTextArea;
    private JTextArea postgresqlTextArea;

    private JComboBox<String> tableComboBox;
    private CustomOKAction okAction;
    private DialogWrapper.DialogWrapperExitAction exitAction;
    private Table<String, String, String> choiceTable;
    private String directoryPath;

    private String configPath;

    private Map<String, String> tableTypeMap;

    public static final String MYSQL_DB = "mysql";
    public static final String ORACLE_DB = "oracle";
    public static final String POSTGRESQL_DB = "postgresql";


    public GenerateSqlDialog(String directoryPath, String configPath) {
        super(true);
        this.directoryPath = directoryPath;
        this.configPath = configPath;
        init();
        setTitle("生成sql");
        choiceTable = new RowKeyTable<>();
        tableTypeMap = new HashMap<>();
        ChoiceTable.parseChoiceTable(configPath).forEach(table -> {
            choiceTable.put(table.getRowKey().getKey(), table.getColumnKey(), table.getValue());
            tableTypeMap.put(table.getRowKey().getKey(), table.getRowKey().getValue());
        });

        choiceTable.rowKeySet().forEach(tableComboBox::addItem);
    }


    /**
     * 创建视图
     *
     * @return
     */
    @Nullable
    @Override
    protected JComponent createCenterPanel() {
      /*  JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(789, 909));
        GridLayoutManager gridLayoutManager = new GridLayoutManager(8, 2, JBUI.insets(10), -1, -1);
        gridLayoutManager.setSameSizeHorizontally(false);
        gridLayoutManager.setSameSizeHorizontally(false);
        panel.setLayout(gridLayoutManager);

        JLabel tableLabel = new JLabel("请选择表");
        GridConstraints tableGrid = new GridConstraints();
        tableGrid.setRow(0);
        tableGrid.setColumn(0);
        tableGrid.setRowSpan(1);
        tableGrid.setColSpan(1);
        tableGrid.setVSizePolicy(0);
        tableGrid.setHSizePolicy(0);
        tableGrid.setAnchor(0);
        tableGrid.setFill(0);
        tableGrid.setIndent(0);
        tableGrid.setUseParentLayout(false);
        panel.add(tableLabel, tableGrid);

        tableComboBox.setAutoscrolls(true);
        GridConstraints tableComboxGrid = new GridConstraints();
        tableComboxGrid.setRow(0);
        tableComboxGrid.setColumn(1);
        tableComboxGrid.setRowSpan(1);
        tableComboxGrid.setColSpan(1);
        tableComboxGrid.setVSizePolicy(0);
        tableComboxGrid.setHSizePolicy(2);
        tableComboxGrid.setAnchor(9);
        tableComboxGrid.setFill(0);
        tableComboxGrid.setIndent(0);
        tableComboxGrid.setUseParentLayout(false);
        panel.add(tableComboBox, tableComboxGrid);


        JLabel processIdLabel = new JLabel("修改单");
        GridConstraints processIdGrid = new GridConstraints();
        processIdGrid.setRow(1);
        processIdGrid.setColumn(0);
        processIdGrid.setRowSpan(1);
        processIdGrid.setColSpan(1);
        processIdGrid.setVSizePolicy(0);
        processIdGrid.setHSizePolicy(0);
        processIdGrid.setAnchor(0);
        processIdGrid.setFill(0);
        processIdGrid.setIndent(0);
        processIdGrid.setUseParentLayout(false);
        panel.add(processIdLabel, processIdGrid);


        processIdTextField = new JTextField();
        processIdTextField.setPreferredSize(new Dimension(150, -1));
        GridConstraints processIdTextGrid = new GridConstraints();
        processIdTextGrid.setRow(1);
        processIdTextGrid.setColumn(1);
        processIdTextGrid.setRowSpan(1);
        processIdTextGrid.setColSpan(1);
        processIdTextGrid.setVSizePolicy(0);
        processIdTextGrid.setHSizePolicy(6);
        processIdTextGrid.setAnchor(8);
        processIdTextGrid.setFill(0);
        processIdTextGrid.setIndent(0);
        processIdTextGrid.setUseParentLayout(false);
        panel.add(processIdTextField, processIdTextGrid);

        JLabel processVersionLabel = new JLabel("修改版本");
        GridConstraints processVersionGrid = new GridConstraints();
        processVersionGrid.setRow(2);
        processVersionGrid.setColumn(0);
        processVersionGrid.setRowSpan(1);
        processVersionGrid.setColSpan(1);
        processVersionGrid.setVSizePolicy(0);
        processVersionGrid.setHSizePolicy(0);
        processVersionGrid.setAnchor(0);
        processVersionGrid.setFill(0);
        processVersionGrid.setIndent(0);
        processVersionGrid.setUseParentLayout(false);
        panel.add(processVersionLabel, processVersionGrid);

        processVersionTextField = new JTextField();
        processVersionTextField.setPreferredSize(new Dimension(150, -1));
        GridConstraints processVersionTextGrid = new GridConstraints();
        processVersionTextGrid.setRow(2);
        processVersionTextGrid.setColumn(1);
        processVersionTextGrid.setRowSpan(1);
        processVersionTextGrid.setColSpan(1);
        processVersionTextGrid.setVSizePolicy(0);
        processVersionTextGrid.setHSizePolicy(6);
        processVersionTextGrid.setAnchor(8);
        processVersionTextGrid.setFill(0);
        processVersionTextGrid.setIndent(0);
        processVersionTextGrid.setUseParentLayout(false);
        panel.add(processVersionTextField, processVersionTextGrid);

        JLabel processUserLabel = new JLabel("修改人");
        GridConstraints processUserGrid = new GridConstraints();
        processUserGrid.setRow(3);
        processUserGrid.setColumn(0);
        processUserGrid.setRowSpan(1);
        processUserGrid.setColSpan(1);
        processUserGrid.setVSizePolicy(0);
        processUserGrid.setHSizePolicy(0);
        processUserGrid.setAnchor(0);
        processUserGrid.setFill(0);
        processUserGrid.setIndent(0);
        processUserGrid.setUseParentLayout(false);
        panel.add(processUserLabel, processUserGrid);

        processUserTextField = new JTextField();
        processUserTextField.setPreferredSize(new Dimension(150, -1));
        GridConstraints processUserTextGrid = new GridConstraints();
        processUserTextGrid.setRow(3);
        processUserTextGrid.setColumn(1);
        processUserTextGrid.setRowSpan(1);
        processUserTextGrid.setColSpan(1);
        processUserTextGrid.setVSizePolicy(0);
        processUserTextGrid.setHSizePolicy(6);
        processUserTextGrid.setAnchor(8);
        processUserTextGrid.setFill(0);
        processUserTextGrid.setIndent(0);
        processUserTextGrid.setUseParentLayout(false);
        panel.add(processUserTextField, processUserTextGrid);


        JLabel processDescLabel = new JLabel("修改内容");
        GridConstraints processDescGrid = new GridConstraints();
        processDescGrid.setRow(4);
        processDescGrid.setColumn(0);
        processDescGrid.setRowSpan(1);
        processDescGrid.setColSpan(1);
        processDescGrid.setVSizePolicy(0);
        processDescGrid.setHSizePolicy(0);
        processDescGrid.setAnchor(0);
        processDescGrid.setFill(1);
        processDescGrid.setIndent(0);
        processDescGrid.setUseParentLayout(false);
        panel.add(processDescLabel, processDescGrid);


        processDescTextField = new JTextField();
        GridConstraints processDescTextGrid = new GridConstraints();
        processDescTextGrid.setRow(4);
        processDescTextGrid.setColumn(1);
        processDescTextGrid.setRowSpan(1);
        processDescTextGrid.setColSpan(1);
        processDescTextGrid.setVSizePolicy(0);
        processDescTextGrid.setHSizePolicy(6);
        processDescTextGrid.setAnchor(8);
        processDescTextGrid.setFill(1);
        processDescTextGrid.setIndent(0);
        processDescTextGrid.setUseParentLayout(false);
        panel.add(processDescTextField, processDescTextGrid);


        JLabel mysqlLabel = new JLabel("mysql");
        GridConstraints mysqlGrid = new GridConstraints();
        mysqlGrid.setRow(5);
        mysqlGrid.setColumn(0);
        mysqlGrid.setRowSpan(1);
        mysqlGrid.setColSpan(1);
        mysqlGrid.setVSizePolicy(0);
        mysqlGrid.setHSizePolicy(0);
        mysqlGrid.setAnchor(0);
        mysqlGrid.setFill(0);
        mysqlGrid.setIndent(0);
        mysqlGrid.setUseParentLayout(false);
        panel.add(mysqlLabel, mysqlGrid);


        mysqlTextArea = new JTextPane();
        mysqlTextArea.setSelectedTextColor(new JBColor(() -> new Color(0x075BDB)));
        mysqlTextArea.setSelectionColor(new JBColor(() -> new Color(-13745298)));
        mysqlTextArea.setPreferredSize(new Dimension(500, 172));
        mysqlTextArea.setToolTipText("请输入mysql脚本");
        mysqlTextArea.setDropMode(DropMode.USE_SELECTION);
        JScrollPane mysqlScrollPane = new JBScrollPane(mysqlTextArea);

        GridConstraints mysqlTextGrid = new GridConstraints();
        mysqlTextGrid.setRow(5);
        mysqlTextGrid.setColumn(1);
        mysqlTextGrid.setRowSpan(1);
        mysqlTextGrid.setColSpan(1);
        mysqlTextGrid.setVSizePolicy(7);
        mysqlTextGrid.setHSizePolicy(7);
        mysqlTextGrid.setAnchor(0);
        mysqlTextGrid.setFill(3);
        mysqlTextGrid.setIndent(0);
        mysqlTextGrid.setUseParentLayout(false);
        panel.add(mysqlScrollPane, mysqlTextGrid);


        JLabel oracleLabel = new JLabel("oracle");
        GridConstraints oracleGrid = new GridConstraints();
        oracleGrid.setRow(6);
        oracleGrid.setColumn(0);
        oracleGrid.setRowSpan(1);
        oracleGrid.setColSpan(1);
        oracleGrid.setVSizePolicy(0);
        oracleGrid.setHSizePolicy(0);
        oracleGrid.setAnchor(0);
        oracleGrid.setFill(0);
        oracleGrid.setIndent(0);
        oracleGrid.setUseParentLayout(false);
        panel.add(oracleLabel, oracleGrid);


        oracleTextArea = new JTextPane();
        oracleTextArea.setSelectedTextColor(new JBColor(() -> new Color(0x075BDB)));
        oracleTextArea.setSelectionColor(new JBColor(() -> new Color(-13745298)));
        oracleTextArea.setPreferredSize(new Dimension(500, 188));
        oracleTextArea.setToolTipText("请输入oracle脚本");
        oracleTextArea.setDropMode(DropMode.USE_SELECTION);
        oracleTextArea.setEditorKit(new WrapEditorKit());
        JScrollPane oracleScrollPane = new JBScrollPane(oracleTextArea);
        GridConstraints oracleTextGrid = new GridConstraints();
        oracleTextGrid.setRow(6);
        oracleTextGrid.setColumn(1);
        oracleTextGrid.setRowSpan(1);
        oracleTextGrid.setColSpan(1);
        oracleTextGrid.setVSizePolicy(7);
        oracleTextGrid.setHSizePolicy(7);
        oracleTextGrid.setAnchor(0);
        oracleTextGrid.setFill(3);
        oracleTextGrid.setIndent(0);
        oracleTextGrid.setUseParentLayout(false);
        panel.add(oracleScrollPane, oracleTextGrid);

        JLabel postgresqlLabel = new JLabel("postgresql");
        GridConstraints postgresqlGrid = new GridConstraints();
        postgresqlGrid.setRow(7);
        postgresqlGrid.setColumn(0);
        postgresqlGrid.setRowSpan(1);
        postgresqlGrid.setColSpan(1);
        postgresqlGrid.setVSizePolicy(0);
        postgresqlGrid.setHSizePolicy(0);
        postgresqlGrid.setAnchor(0);
        postgresqlGrid.setFill(0);
        postgresqlGrid.setIndent(0);
        postgresqlGrid.setUseParentLayout(false);
        panel.add(postgresqlLabel, postgresqlGrid);


        postgresqlTextArea = new JTextPane();

        postgresqlTextArea.setSelectedTextColor(new JBColor(() -> new Color(0x075BDB)));
        postgresqlTextArea.setSelectionColor(new JBColor(() -> new Color(-13745298)));
        postgresqlTextArea.setPreferredSize(new Dimension(500, 188));
        postgresqlTextArea.setToolTipText("请输入postgresql脚本");
        postgresqlTextArea.setDropMode(DropMode.USE_SELECTION);
        postgresqlTextArea.setEditorKit(new WrapEditorKit());

        JScrollPane postgresqlScrollPane = new JBScrollPane(postgresqlTextArea);
        GridConstraints postgresqlTextGrid = new GridConstraints();
        postgresqlTextGrid.setRow(7);
        postgresqlTextGrid.setColumn(1);
        postgresqlTextGrid.setRowSpan(1);
        postgresqlTextGrid.setColSpan(1);
        postgresqlTextGrid.setVSizePolicy(7);
        postgresqlTextGrid.setHSizePolicy(7);
        postgresqlTextGrid.setAnchor(0);
        postgresqlTextGrid.setFill(3);
        postgresqlTextGrid.setIndent(0);
        postgresqlTextGrid.setUseParentLayout(false);
        panel.add(postgresqlScrollPane, postgresqlTextGrid);*/
        CreateDialog createDialog = new CreateDialog();
        this.tableComboBox = createDialog.getTableBox();
        this.mysqlTextArea = createDialog.getMysqlText();
        this.oracleTextArea = createDialog.getOracleText();
        this.postgresqlTextArea = createDialog.getPostgreText();
        this.processIdTextField = createDialog.getProcessId();
        this.processVersionTextField = createDialog.getProcessVersion();
        this.processDescTextField = createDialog.getProcessDesc();
        this.processUserTextField = createDialog.getProcessUser();
        return createDialog.getContentPane();

    }

    /**
     * 校验数据
     *
     * @return 通过必须返回null，不通过返回一个 ValidationInfo 信息
     */
    @Nullable
    @Override
    protected ValidationInfo doValidate() {
        String table = (String) tableComboBox.getSelectedItem();
        String mysqlText = mysqlTextArea.getText();
        String oracleText = oracleTextArea.getText();
        String postgresqlText = postgresqlTextArea.getText();
        String ticketVersionText = processVersionTextField.getText();
        String ticketDescText = processDescTextField.getText();
        String ticketIdText = processIdTextField.getText();
        String ticketUserText = processUserTextField.getText();

        String errorMsg = "";
        if (StringUtils.isBlank(table)) {
            errorMsg = "请选择表！";
        } else if (StringUtils.isBlank(ticketIdText)) {
            errorMsg = "请输入修改单！";
        } else if (StringUtils.isBlank(ticketUserText)) {
            errorMsg = "请输入修改人！";
        } else if (StringUtils.isBlank(ticketVersionText)) {
            errorMsg = "请输入修改版本！";
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

    /**
     * 覆盖默认的ok/cancel按钮
     *
     * @return
     */
    @NotNull
    @Override
    protected Action[] createActions() {
        exitAction = new DialogWrapper.DialogWrapperExitAction("取消", CANCEL_EXIT_CODE);
        okAction = new GenerateSqlDialog.CustomOKAction();
        // 设置默认的焦点按钮
        okAction.putValue(DialogWrapper.DEFAULT_ACTION, true);
        return new Action[]{okAction, exitAction};
    }


    /**
     * 自定义 ok Action
     */
    protected class CustomOKAction extends DialogWrapper.DialogWrapperAction {

        protected CustomOKAction() {
            super("执行");
        }

        @Override
        protected void doAction(ActionEvent e) {
            // 点击ok的时候进行数据校验
            ValidationInfo validationInfo = doValidate();
            if (validationInfo != null) {
                Messages.showMessageDialog(validationInfo.message, "校验不通过", Messages.getErrorIcon());
            } else {
                MessageDialogBuilder.YesNo yesNo = MessageDialogBuilder.yesNo("确认", "请确认是否要生成sql？");
                if (yesNo.isYes()) {
                    String table = (String) tableComboBox.getSelectedItem();
                    String mysqlText = mysqlTextArea.getText();
                    String oracleText = oracleTextArea.getText();
                    String postgresqlText = postgresqlTextArea.getText();
                    String ticketVersionText = processVersionTextField.getText();
                    String ticketDescText = processDescTextField.getText();
                    String ticketIdText = processIdTextField.getText();
                    String ticketUserText = processUserTextField.getText();

                    String mysqlPath = choiceTable.get(table, MYSQL_DB);
                    String oraclePath = choiceTable.get(table, ORACLE_DB);
                    String postgresqlPath = choiceTable.get(table, POSTGRESQL_DB);


                    String tableType = tableTypeMap.get(table);
                    Database database = DataBaseParser.findDatabase(configPath, tableType);
                    MySQL mySQL = database.getDatabaseList().stream().filter(baseDatabase -> baseDatabase instanceof MySQL).map(baseDatabase -> (MySQL) baseDatabase).findFirst().orElse(null);
                    Oracle oracle = database.getDatabaseList().stream().filter(baseDatabase -> baseDatabase instanceof Oracle).map(baseDatabase -> (Oracle) baseDatabase).findFirst().orElse(null);
                    Postgresql postgresql = database.getDatabaseList().stream().filter(baseDatabase -> baseDatabase instanceof Postgresql).map(baseDatabase -> (Postgresql) baseDatabase).findFirst().orElse(null);

                    assert mySQL != null;
                    String mysqlError = executeInDb(MYSQL_DB, DataBaseParser.parseJdbcUrlByDatasource(mySQL, MYSQL_DB), mySQL.getUser(), mySQL.getPassword(), mysqlText);

                    if (StringUtils.isNotBlank(mysqlError)) {
                        MessageDialogBuilder.YesNo error = MessageDialogBuilder.yesNo("ERROR", "执行mysql脚本到数据库出错:\n" + mysqlError);
                        error.yesText("忽略并继续生成");
                        error.noText("退出");
                        if (!error.isYes()) {
                            return;
                        }
                    }
                    assert oracle != null;
                    String oracleError = executeInDb(ORACLE_DB, DataBaseParser.parseJdbcUrlByDatasource(oracle, ORACLE_DB), oracle.getUser(), oracle.getPassword(), oracleText);
                    if (StringUtils.isNotBlank(oracleError)) {
                        MessageDialogBuilder.YesNo error = MessageDialogBuilder.yesNo("ERROR", "执行oracle脚本到数据库出错:\n" + oracleError);
                        error.yesText("忽略并继续生成");
                        error.noText("退出");
                        if (!error.isYes()) {
                            return;
                        }
                    }

                    assert postgresql != null;
                    String pgError = executeInDb(POSTGRESQL_DB, DataBaseParser.parseJdbcUrlByDatasource(postgresql, POSTGRESQL_DB), postgresql.getUser(), postgresql.getPassword(), postgresqlText);
                    if (StringUtils.isNotBlank(pgError)) {
                        MessageDialogBuilder.YesNo error = MessageDialogBuilder.yesNo("ERROR", "执行postgresql脚本到数据库出错:\n" + pgError);
                        error.yesText("忽略并继续生成");
                        error.noText("退出");
                        if (!error.isYes()) {
                            return;
                        }
                    }

                    putSqlToFile(mysqlText, ticketVersionText, ticketDescText, ticketIdText, ticketUserText, mysqlPath);
                    putSqlToFile(oracleText, ticketVersionText, ticketDescText, ticketIdText, ticketUserText, oraclePath);
                    putSqlToFile(postgresqlText, ticketVersionText, ticketDescText, ticketIdText, ticketUserText, postgresqlPath);


                    Messages.showMessageDialog("生成sql成功！", "SUCCESS", Messages.getInformationIcon());
                    close(CANCEL_EXIT_CODE);
                }


            }
        }
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

    private void putSqlToFile(String sqlText, String ticketVersionText, String ticketDescText, String ticketIdText, String ticketUserText, String path) {
        String content = FileUtil.readUtf8String(directoryPath + "/" + path);
        StringBuilder sb = new StringBuilder();
        String description = "-- V" + ticketVersionText + "   " + LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE) + "     " + ticketIdText + "   " + ticketUserText + "          " + ticketUserText + "          " + ticketDescText;
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
        FileUtil.writeUtf8String(sb.toString(), directoryPath + "/" + path);
    }


}
