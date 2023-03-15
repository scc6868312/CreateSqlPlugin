package com.scc.createsqlplugin.util;

import com.intellij.execution.ui.ConsoleView;
import com.intellij.execution.ui.ConsoleViewContentType;
import com.jcraft.jsch.*;

import java.io.InputStream;
import java.util.Optional;
import java.util.Properties;

/**
 * @ProjectName ice
 * @Package com.hundsun.hswealth.utils
 * @Description
 * @Author plus
 * @Date 2022/8/5 15:47
 * @UpdateRemark The modified content
 * @Version 1.0
 * <p>
 * Copyright © 2022 Hundsun Technologies Inc. All Rights Reserved
 **/
public class JschUtil {
    private JSch jSch;

    // session对象
    private Session session;

    // JAVA与主机的连接通道
    private Channel channel;

    // sftp通道
    ChannelSftp chSftp;

    // 主机ip
    private String host;

    // 主机端口号
    private int port;

    // 主机账号
    private String username;

    // 主机密码
    private String password;

    public JschUtil(String host, int port, String username, String password) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
    }

    public JschUtil() {
    }

    /**
     * 检测是否可以和主机通信
     *
     * @return
     */
    public boolean connect() throws Exception {

        jSch = new JSch();

        boolean reulst = false;

        try {

            // 根据主机账号、ip、端口获取一个Session对象
            session = jSch.getSession(username, host, port);

            // 存放主机密码
            session.setPassword(password);

            // 首次连接，去掉公钥确认
            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);

            // 超时连接时间为3秒
            session.setTimeout(3000);

            // 进行连接
            session.connect();

            // 获取连接结果
            reulst = session.isConnected();

            if (!reulst) {
                throw new Exception("连接失败");
            }

        } finally {
            close();
        }
        return reulst;
    }

    /**
     * 关闭连接
     */
    public void close() {

        if (channel != null && channel.isConnected()) {
            channel.disconnect();
        }

        if (session != null && session.isConnected()) {
            session.disconnect();
        }

        if (chSftp != null && chSftp.isConnected()) {
            chSftp.quit();
        }

    }

    /**
     * 执行shell命令
     *
     * @param command
     * @param consoleView
     * @return
     */
    public String execCommand(String command, ConsoleView consoleView) throws Exception {

        jSch = new JSch();

        // 存放执行命令结果
        StringBuffer result = new StringBuffer();
        String partResult = "";
        int exitStatus = 0;

        boolean successFlag = true;
        try {

            // 根据主机账号、ip、端口获取一个Session对象
            session = jSch.getSession(username, host, port);

            // 存放主机密码
            session.setPassword(password);

            // 去掉首次连接确认
            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);

            // 超时连接时间为3秒
            session.setTimeout(3000);

            // 进行连接
            session.connect();

            channel = session.openChannel("exec");
            ((ChannelExec) channel).setCommand(command);

            channel.setInputStream(null);
            // 错误信息输出流，用于输出错误的信息，当exitstatus<0的时候
            ((ChannelExec) channel).setErrStream(System.err);

            // 执行命令，等待执行结果
            channel.connect();

            // 获取命令执行结果
            InputStream in = channel.getInputStream();

            /**
             * 通过channel获取信息的方式，采用官方Demo代码
             */
            byte[] tmp = new byte[1024];
            while (true) {
                while (in.available() > 0) {
                    int i = in.read(tmp, 0, 1024);
                    if (i < 0) {
                        break;
                    }
                    partResult = new String(tmp, 0, i);
                    result.append(partResult);
                    String finalPartResult = partResult;
                    Optional.ofNullable(consoleView).ifPresent(view -> view.print(finalPartResult + "\n", ConsoleViewContentType.NORMAL_OUTPUT));
                    if (partResult.contains("ERROR") || partResult.contains("ORA-")) {
                        successFlag = false;
                    }
                }
                // 从channel获取全部信息之后，channel会自动关闭
                if (channel.isClosed()) {
                    if (in.available() > 0) {
                        continue;
                    }
                    exitStatus = channel.getExitStatus();
                    if (exitStatus != 0 || !successFlag) {
                        throw new Exception("执行命令" + command + "的，exitStatus：" + exitStatus + "，partResult：\n" + partResult);
                    }
                    break;
                }
                try {
                    Thread.sleep(1000);
                } catch (Exception ignored) {
                }
            }
        } finally {
            close();
        }

        return result.toString();
    }

    /**
     * 文件上传至主机
     *
     * @param directory  当前文件路径
     * @param uploadFile 上传至主机的路径
     */
    public void upload(String directory, String uploadFile) throws Exception {
        // 创建JSch对象
        jSch = new JSch();

        try {
            // 根据主机账号、ip、端口获取一个Session对象
            session = jSch.getSession(username, host, port);

            // 存放主机密码
            session.setPassword(password);

            // 去掉首次连接确认
            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);

            // 超时连接时间为3秒
            session.setTimeout(3000);

            // 进行连接
            session.connect();

            // 打开SFTP通道
            chSftp = (ChannelSftp) session.openChannel("sftp");

            // 建立STFP连接
            chSftp.connect();

            // 设置编码格式
            chSftp.setFilenameEncoding("UTF-8");

            /**
             * 说明：
             * 1、当前文件上传信息没有任何反馈，如果没有异常则代表成功
             * 2、如果需要判断是否读取成功的进度，可参考https://blog.csdn.net/coding99/article/details/52416373?locationNum=13&fps=1
             * 3、将src文件上传到dst路径中
             */
            chSftp.put(directory, uploadFile);
        } finally {
            close();
        }
    }

    /**
     * 将主机文件下载至本地
     *
     * @param directory    下载到本地的位置
     * @param downloadFile 下载文件在虚拟机的位置
     */
    public void download(String directory, String downloadFile) throws Exception {

        try {
            jSch = new JSch();

            // 根据主机账号、ip、端口获取一个Session对象
            session = jSch.getSession(username, host, port);

            // 存放主机密码
            session.setPassword(password);

            // 去掉首次连接确认
            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);

            // 超时连接时间为3秒
            session.setTimeout(3000);

            // 进行连接
            session.connect();

            // 打开SFTP通道
            chSftp = (ChannelSftp) session.openChannel("sftp");

            // 建立SFTP通道的连接
            chSftp.connect();

            // 设置编码格式
            chSftp.setFilenameEncoding("UTF-8");

            /**
             * 说明：
             * 1、当前上读取文件信息没有任何反馈，如果没有异常则代表成功
             * 2、如果需要判断是否读取成功的进度，可参考https://blog.csdn.net/coding99/article/details/52416373?locationNum=13&fps=1
             * 3、将src文件下载到dst路径中
             */
            chSftp.get(directory, downloadFile);

        } finally {
            close();
        }
    }
}


