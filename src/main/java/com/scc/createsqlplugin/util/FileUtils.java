package com.scc.createsqlplugin.util;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.PathUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.XmlUtil;
import com.intellij.openapi.project.Project;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.filefilter.FileFileFilter;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileFilter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @ProjectName its-tools
 * @Package com.hundsun.hswealth.utils
 * @Description
 * @Author plus
 * @Date 2022/8/16 15:34
 * @UpdateRemark The modified content
 * @Version 1.0
 * <p>
 * Copyright © 2022 Hundsun Technologies Inc. All Rights Reserved
 **/
public class FileUtils {

    /**
     * 比较截取的第二个_后的名字
     */
    static Comparator<File> fileComparator = (file1, file2) -> {
        String name1 = StrUtil.subAfter(
                StrUtil.subAfter(file1.getName(), "_", false),
                "_", false);
        String name2 = StrUtil.subAfter(
                StrUtil.subAfter(file2.getName(), "_", false),
                "_", false);
        return name1.compareTo(name2);
    };


    /**
     * 获取路径下的文件-
     *
     * @param path
     * @return
     */
    public static List<File> getPathFiles(String path) {
        File[] files = Paths.get(path).toFile().listFiles(pathname -> !pathname.getName().startsWith("."));
        if (files == null) {
            return new ArrayList<>();
        }
        return Arrays.stream(files).sorted(fileComparator).collect(Collectors.toList());
    }


    /**
     * 获取folderPath下，文件名以endWith结尾的文件名列表
     *
     * @param folderPath
     * @param scanFiles
     * @param endWith
     * @return
     */
    public static List<String> scanFilesWithRecursion(String folderPath, List<String> scanFiles, String endWith) {
        File directory = new File(folderPath);
        File[] filelist = directory.listFiles(pathname -> !pathname.getName().startsWith("."));
        for (int i = 0; i < filelist.length; i++) {
            if (filelist[i].isDirectory()) {
                scanFilesWithRecursion(filelist[i].getAbsolutePath(), scanFiles, endWith);
            } else {
                String fileName = filelist[i].getName();
                if (fileName.endsWith(endWith)) {
                    scanFiles.add(fileName.substring(0, fileName.indexOf(endWith)));
                }
            }
        }
        return scanFiles;
    }


    /**
     * 获取工程基础路径
     *
     * @param path
     * @return
     */
    public static String getProjectBasePath(String path) {
        if (StringUtils.isBlank(path)) {
            return StringUtils.EMPTY;
        }
        return path.replaceAll("(Sql|DevCodes|ProcessZips|pluginConfig|build).*", StringUtils.EMPTY);
    }


    /**
     * 获取工程基础路径
     *
     * @param project
     * @return
     */
    public static String getProjectBasePath(Project project) {
        if (project == null) {
            return StringUtils.EMPTY;
        }
        return project.getBasePath();
    }

    /**
     * 获取微服务名
     *
     * @param path
     * @return
     */
    public static String getServiceName(String path) {
        String projectBasePath = getProjectBasePath(path);
        if (StringUtils.isBlank(projectBasePath)) {
            return StringUtils.EMPTY;
        }
        List<File> devCodes = PathUtil.loopFiles(Paths.get(projectBasePath.concat(File.separator).concat("DevCodes")), 1, null);
        if (CollectionUtils.isNotEmpty(devCodes)) {
            return devCodes.stream().filter(file -> file.getName().startsWith("hswealth-")).findFirst().map(file -> file.getName().replace("hswealth-", StringUtils.EMPTY)).orElse(StringUtils.EMPTY);
        }
        return StringUtils.EMPTY;
    }

    /**
     * 获取版本
     *
     * @param path
     * @return
     */
    public static String getVersion(String path) {
        String projectBasePath = getProjectBasePath(path);
        if (StringUtils.isBlank(projectBasePath)) {
            return StringUtils.EMPTY;
        }
        String serviceName = getServiceName(path);
        if (StringUtils.isBlank(serviceName)) {
            return StringUtils.EMPTY;
        }
        Map<String, Object> xml = XmlUtil.xmlToMap(XmlUtil.readXML(projectBasePath.concat(File.separator).concat("DevCodes").concat(File.separator).concat("hswealth-").concat(serviceName).concat(File.separator).concat("pom.xml")));
        String version = (String) ((Map<String, Object>) xml.get("project")).get("version");
        return version.replaceAll("-SNAPSHOT", StringUtils.EMPTY);
    }

}
