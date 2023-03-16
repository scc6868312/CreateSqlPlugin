package com.scc.createsqlplugin.executesql;

import cn.hutool.core.comparator.VersionComparator;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.execution.ui.ConsoleViewContentType;
import com.scc.createsqlplugin.config.DbCmd;
import com.scc.createsqlplugin.config.ReadFileCallBack;
import com.scc.createsqlplugin.config.RunSqlCallBack;
import com.scc.createsqlplugin.database.BaseDatabase;
import com.scc.createsqlplugin.database.DataBaseParser;
import com.scc.createsqlplugin.database.Database;
import com.scc.createsqlplugin.database.DatabaseEntity;
import com.scc.createsqlplugin.util.FileUtils;
import com.scc.createsqlplugin.util.JschUtil;
import com.scc.createsqlplugin.util.VelocityUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.scc.createsqlplugin.util.FileUtils.getPathFiles;


/**
 * @ProjectName its-tools
 * @Package com.hundsun.hswealth.controller
 * @Description
 * @Author plus
 * @Date 2022/8/16 14:58
 * @UpdateRemark The modified content
 * @Version 1.0
 * <p>
 * Copyright © 2022 Hundsun Technologies Inc. All Rights Reserved
 **/
public class SqlScriptController {


    /**
     * 跑当前版本的脚本
     *
     * @throws Exception
     */
    public static Boolean runCurrVersionScripts(String path, ConsoleView consoleView) {
        String rootPathPrefix = FileUtils.getProjectBasePath(path);

        RunSqlCallBack runSqlCallBack = (dbType, executeUser) -> {
            // 获取pom的版本
            String version = FileUtils.getVersion(path);

            String fileDirectory = String.join(File.separator, Arrays.asList(rootPathPrefix, "Sql", "SqlPatch", version, dbType));
            // 先扫描TablePatch，再扫描SqlPatch

            List<File> fileList = new ArrayList<>();
            fileList.addAll(getPathFiles(fileDirectory + File.separator + "TablePatch"));
            fileList.addAll(getPathFiles(fileDirectory + File.separator + "DataPatch"));
            return fileList.stream().filter(file -> file.getName().startsWith(executeUser)).collect(Collectors.toList());
        };
        return runScripts(rootPathPrefix.concat(File.separator).concat("pluginConfig"), runSqlCallBack, consoleView);
    }

    /**
     * 跑基线脚本
     *
     * @return
     * @throws Exception
     */
    public static boolean runAllVersionScripts(String path, ConsoleView consoleView) {
        String rootPathPrefix = FileUtils.getProjectBasePath(path);
        RunSqlCallBack runSqlCallBack = (dbType, executeUser) -> {
            // 先执行install
            String installFileDirectory = String.join(File.separator, Arrays.asList(rootPathPrefix, "Sql", "Install", dbType));
            List<File> fileList = new ArrayList<>();
            // 先扫描install的DBScript，然后InitData
            fileList.addAll(getPathFiles(installFileDirectory + File.separator + "DBScript"));
            fileList.addAll(getPathFiles(installFileDirectory + File.separator + "InitData"));
            // 扫描所有的sqlPatch版本目录
            String sqlPatchFileDirectory = String.join(File.separator, Arrays.asList(rootPathPrefix, "Sql", "SqlPatch"));
            File sqlPatchDirFile = Paths.get(sqlPatchFileDirectory).toFile();
            File[] files = sqlPatchDirFile.listFiles();
            if (files != null) {
                Stream.of(files).filter(File::isDirectory).sorted((o1, o2) -> VersionComparator.INSTANCE.compare(o1.getName(), o2.getName())).forEach(file -> {
                    String filePath = file.getAbsolutePath() + File.separator + dbType;
                    fileList.addAll(getPathFiles(filePath + File.separator + "TablePatch"));
                    fileList.addAll(getPathFiles(filePath + File.separator + "DataPatch"));
                });
            }
            return fileList.stream().filter(file -> file.getName().startsWith(executeUser)).collect(Collectors.toList());
        };
        return runScripts(rootPathPrefix.concat(File.separator).concat("pluginConfig"), runSqlCallBack, consoleView);
    }

    /**
     * 跑脚本的流程
     *
     * @param configPath
     * @param runSqlCallBack
     * @param consoleView
     * @throws Exception
     */
    private static boolean runScripts(String configPath, RunSqlCallBack runSqlCallBack, ConsoleView consoleView) {
        try {
            // 1.解析文件
            DatabaseEntity runScriptEntry = DataBaseParser.parseDatabase(configPath);
            // fileName ->  db -> noteList
            Map<String, Map<String, List<String>>> fileDbNotes = new HashMap<>();

            String ip = runScriptEntry.getIp();
            String port = runScriptEntry.getPort();
            String userName = runScriptEntry.getUsername();
            String passWord = runScriptEntry.getPassword();
            String scriptDir = getExecuteUser();
            List<Database> dataBaseEntryList = runScriptEntry.getDatabaseList();

            for (Database dataBaseEntry : dataBaseEntryList) {
                String executeUser = dataBaseEntry.getType();

                List<File> mysqlList = runSqlCallBack.getFileList("mysql", executeUser);
                executeFile(fileDbNotes, ip, port, userName, passWord, scriptDir, dataBaseEntry.getMySQL(), mysqlList, executeUser,"mysql", consoleView);

                List<File> oracleList = runSqlCallBack.getFileList("oracle", executeUser);
                executeFile(fileDbNotes, ip, port, userName, passWord, scriptDir, dataBaseEntry.getOracle(), oracleList, executeUser, "oracle", consoleView);

                List<File> postgresqlList = runSqlCallBack.getFileList("postgresql", executeUser);
                executeFile(fileDbNotes, ip, port, userName, passWord, scriptDir, dataBaseEntry.getPostgresql(), postgresqlList, executeUser, "postgresql", consoleView);
            }
        } catch (Exception e) {
            consoleView.print(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")) + "|- 执行sql时出现错误:" + e.getMessage() + "\n" + Arrays.stream(e.getStackTrace()).map(stackTraceElement -> "            " + stackTraceElement.toString()).collect(Collectors.joining("\n")), ConsoleViewContentType.LOG_ERROR_OUTPUT);
            consoleView.requestScrollingToEnd();
            return false;
        }
        return true;
    }

    private static void executeFile(Map<String, Map<String, List<String>>> fileDbNotes, String ip, String port, String userName, String passWord, String scriptDir, BaseDatabase dataBaseEntry, List<File> fileList, String executeUser, String dbType, ConsoleView consoleView) throws Exception {
        if (CollectionUtils.isEmpty(fileList)) {
            consoleView.print(String.format("服务类型：%s，数据库类型：%s，没有可执行的脚本，请检查，若没问题可忽略\n", executeUser, dbType), ConsoleViewContentType.LOG_WARNING_OUTPUT);
            return;
        }
        // 连接服务器
        JschUtil jSchUtil = new JschUtil(ip, Integer.parseInt(port), userName, passWord);
        boolean connect = jSchUtil.connect();
        if (!connect) {
            throw new Exception("连接ssh失败");
        }
        // 去对应数据库的执行文件目录下
        String linuxScriptDir = String.join("/", Arrays.asList("home", "dbClient", dbType, "script"));
        String cdToLinuxScriptDir = "cd /" + linuxScriptDir + " && ";
        String ls = jSchUtil.execCommand(cdToLinuxScriptDir + "ls", null);
        List<String> fileDirList = Arrays.asList(ls.split("\n"));
        if (!fileDirList.contains(scriptDir)) {
            // 创建目录
            String mkdirCmd = cdToLinuxScriptDir + "mkdir -p " + scriptDir;
            jSchUtil.execCommand(mkdirCmd, null);
        }
        for (File file : fileList) {
            // 如果是.vm结尾，则需要替换参数
            // oracle 需要加 whenever sqlerror exit rollback
            String localFileAbsolutePath = file.getAbsolutePath();
            String localUploadFilePath = localFileAbsolutePath;
            String fileName = file.getName();
            boolean isVmFile = fileName.contains(".vm");
            boolean isOracle = dbType.equals("oracle");
            boolean isMysql = dbType.equals("mysql");
            String mysqlLontextPattern = "longtext.+?default ' '";
            Pattern pattern = Pattern.compile(mysqlLontextPattern);
            List<String> lineNotes = new ArrayList<>();
            String result = StringUtils.EMPTY;
            try {

                String parentPath = file.getParent();
                String tmpFileName = fileName;
                if (isVmFile) {
                    tmpFileName = fileName.substring(0, fileName.lastIndexOf(".vm"));
                    localUploadFilePath = parentPath + File.separator + tmpFileName;
                    try (BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(localUploadFilePath), StandardCharsets.UTF_8));) {
                        VelocityUtil.replaceVm(parentPath, fileName, bufferedWriter);
                    }
                }

                String vmTmpFile = localUploadFilePath;
                try (BufferedReader bufferedReader = new BufferedReader(new FileReader(localUploadFilePath));) {
                    if (isOracle) {
                        tmpFileName = "tmp." + tmpFileName;
                        localUploadFilePath = parentPath + File.separator + tmpFileName;
                        try (BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(localUploadFilePath), StandardCharsets.UTF_8));) {
                            bufferedWriter.write("whenever sqlerror exit rollback");
                            bufferedWriter.newLine();
                            readFileDeal(bufferedReader, lineNotes, line -> {
                                bufferedWriter.write(line);
                                bufferedWriter.newLine();
                            });
                        }
                    } else if (isMysql) {
                        tmpFileName = "tmp." + tmpFileName;
                        localUploadFilePath = parentPath + File.separator + tmpFileName;
                        try (BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(localUploadFilePath), StandardCharsets.UTF_8));) {
                            readFileDeal(bufferedReader, lineNotes, line -> {
                                Matcher matcher = pattern.matcher(line);
                                if (matcher.find()) {
                                    line = matcher.replaceAll("longtext");
                                }
                                bufferedWriter.write(line);
                                bufferedWriter.newLine();
                            });
                        }

                    }
                } finally {
                    if (isVmFile && (isOracle || isMysql)) {
                        FileUtil.del(vmTmpFile);
                    }
                }

                fileDbNotes.computeIfAbsent(localFileAbsolutePath, v -> new HashMap<>()).computeIfAbsent(dbType, v -> new ArrayList<>()).addAll(lineNotes);
                // 文件上传
                String remoteFileName = "/" + String.join("/", linuxScriptDir, scriptDir, file.getName());
                jSchUtil.upload(localUploadFilePath, remoteFileName);

                String cdBin = "cd /" + String.join("/", Arrays.asList("home", "dbClient", dbType, "bin")) + " && ";
                String cmd = cdBin + DbCmd.getCmd(dbType, dataBaseEntry.getExtendDbType(), dataBaseEntry) + remoteFileName;
                consoleView.print("开始执行命令：" + cmd + "\n", ConsoleViewContentType.NORMAL_OUTPUT);
                jSchUtil.execCommand(cmd, consoleView);
                consoleView.print("执行命令：" + cmd + "结束\n", ConsoleViewContentType.NORMAL_OUTPUT);
            } catch (Exception e) {
                throw new Exception("执行 " + localFileAbsolutePath + " 脚本出错，错误信息：" + e.getMessage());
            } finally {
                // 删除本地文件
                if (isVmFile || isOracle || isMysql) {
                    FileUtil.del(localUploadFilePath);
                }
            }

        }
    }


    /**
     * 读注释处理
     *
     * @param bufferedReader
     * @param lineNotes
     * @param readFileCallBack
     * @throws Exception
     */
    private static void readFileDeal(BufferedReader bufferedReader, List<String> lineNotes, ReadFileCallBack readFileCallBack) throws Exception {
        String line;
        boolean lineNeed = false;
        while ((line = bufferedReader.readLine()) != null) {
            // 下一行需要统计
            if (line.matches(".*--.*修.版本.*")) {
                lineNeed = true;
            }
            if (lineNeed) {
                if (line.contains("--")) {
                    lineNotes.add(line);
                } else {
                    lineNeed = false;
                }
            }
            if (readFileCallBack != null) {
                readFileCallBack.otherDeal(line);
            }
        }
    }

    public static boolean runAssignableFileScripts(String path, ConsoleView consoleView) {
        String rootPathPrefix = FileUtils.getProjectBasePath(path);
        RunSqlCallBack runSqlCallBack = (dbType, executeUser) -> FileUtil.loopFiles(path).stream().filter(file -> {
            String tmpDbType = dbType;
            String fileUser = StrUtil.subBefore(file.getName(), "_", false);
            if (file.getAbsolutePath().contains("Others")) {
                fileUser = FileUtils.getServiceName(path);
                tmpDbType = tmpDbType.equals("postgresql") ? "pg" : tmpDbType;
            }
            return executeUser.equals(fileUser) && file.getAbsolutePath().contains(tmpDbType);
        }).collect(Collectors.toList());
        return runScripts(rootPathPrefix.concat(File.separator).concat("pluginConfig"), runSqlCallBack, consoleView);
    }

    /**
     * 获取执行用户
     *
     * @return
     * @throws Exception
     */
    public static String getExecuteUser() throws Exception {
        Map<String, String> envMap = System.getenv();
        return Optional.ofNullable(envMap.getOrDefault("USER", envMap.get("USERNAME"))).orElseThrow(() -> new Exception("获取系统用户错误"));
    }
}
