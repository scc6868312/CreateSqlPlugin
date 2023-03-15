package com.scc.createsqlplugin.component;

import javax.swing.*;
import java.awt.event.*;

public class CreateSqlForm extends JDialog {
    private JPanel contentPane;
    private JTextArea mysqlText;
    private JComboBox<String> tableBox;
    private JTextArea oracleText;
    private JTextArea postgreText;

    private JTextField processId;


    private JTextField processUser;

    private JTextField processDesc;
    private JButton buttonOK;
    private JButton buttonCancel;

    public CreateSqlForm() {
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
        CreateSqlForm dialog = new CreateSqlForm();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }


    @Override
    public JPanel getContentPane() {
        return contentPane;
    }


    public JTextArea getMysqlText() {
        return mysqlText;
    }


    public JComboBox<String> getTableBox() {
        return tableBox;
    }


    public JTextArea getOracleText() {
        return oracleText;
    }


    public JTextArea getPostgreText() {
        return postgreText;
    }


    public JTextField getProcessId() {
        return processId;
    }



    public JTextField getProcessUser() {
        return processUser;
    }



    public JTextField getProcessDesc() {
        return processDesc;
    }


    public JButton getButtonOK() {
        return buttonOK;
    }

    public JButton getButtonCancel() {
        return buttonCancel;
    }
}
