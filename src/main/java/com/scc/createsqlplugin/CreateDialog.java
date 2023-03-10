package com.scc.createsqlplugin;

import javax.swing.*;
import java.awt.event.*;

public class CreateDialog extends JDialog {
    private JPanel contentPane;
    private JTextArea mysqlText;
    private JComboBox<String> tableBox;
    private JTextArea oracleText;
    private JTextArea postgreText;

    private JTextField processId;

    private JTextField processVersion;

    private JTextField processUser;

    private JTextField processDesc;

    public CreateDialog() {
        setContentPane(contentPane);
        setModal(true);


        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onOK() {
        // add your code here
        dispose();
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    public static void main(String[] args) {
        CreateDialog dialog = new CreateDialog();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }


    @Override
    public JPanel getContentPane() {
        return contentPane;
    }

    public void setContentPane(JPanel contentPane) {
        this.contentPane = contentPane;
    }

    public JTextArea getMysqlText() {
        return mysqlText;
    }

    public void setMysqlText(JTextArea mysqlText) {
        this.mysqlText = mysqlText;
    }

    public JComboBox<String> getTableBox() {
        return tableBox;
    }

    public void setTableBox(JComboBox<String> tableBox) {
        this.tableBox = tableBox;
    }

    public JTextArea getOracleText() {
        return oracleText;
    }

    public void setOracleText(JTextArea oracleText) {
        this.oracleText = oracleText;
    }

    public JTextArea getPostgreText() {
        return postgreText;
    }

    public void setPostgreText(JTextArea postgreText) {
        this.postgreText = postgreText;
    }

    public JTextField getProcessId() {
        return processId;
    }

    public void setProcessId(JTextField processId) {
        this.processId = processId;
    }

    public JTextField getProcessVersion() {
        return processVersion;
    }

    public void setProcessVersion(JTextField processVersion) {
        this.processVersion = processVersion;
    }

    public JTextField getProcessUser() {
        return processUser;
    }

    public void setProcessUser(JTextField processUser) {
        this.processUser = processUser;
    }

    public JTextField getProcessDesc() {
        return processDesc;
    }

    public void setProcessDesc(JTextField processDesc) {
        this.processDesc = processDesc;
    }
}
