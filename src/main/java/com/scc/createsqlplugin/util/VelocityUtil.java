package com.scc.createsqlplugin.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import java.io.BufferedWriter;
import java.util.Arrays;
import java.util.List;

import static org.apache.velocity.runtime.RuntimeConstants.FILE_RESOURCE_LOADER_PATH;

/**
 * @ProjectName its-tools
 * @Package com.hundsun.hswealth.utils
 * @Description
 * @Author lvjh27574
 * @Date 2022/8/17 21:49
 * @UpdateRemark The modified content
 * @Version 1.0
 * <p>
 * Copyright © 2022 Hundsun Technologies Inc. All Rights Reserved
 **/
public class VelocityUtil {

    /**
     * 替换vm文件中的变量
     *
     * @param path
     * @param fileName
     * @param bufferedWriter
     */
    public static void replaceVm(String path, String fileName, BufferedWriter bufferedWriter) {
        // 初始化模板引擎
        VelocityEngine velocityEngine = new VelocityEngine();
        velocityEngine.setProperty(FILE_RESOURCE_LOADER_PATH, path);
        velocityEngine.init();
        // 获取模板文件
        Template template = velocityEngine.getTemplate(fileName, "UTF-8");
        // 设置变量，velocityContext是一个类似map的结构
        VelocityContext velocityContext = buildVelocityContext();
        // 写入渲染的数据
        template.merge(velocityContext, bufferedWriter);
    }

    /**
     * 设置文件中的变量
     *
     * @return
     */
    private static VelocityContext buildVelocityContext() {
        VelocityContext velocityContext = new VelocityContext();
        // 微服务
        List<String> serviceList = Arrays.asList("ITS", "IOT", "IAS", "IMS", "EIA", "OMS", "OMC", "ICE", "IDS");
        for (String service : serviceList) {
            // 表空间
            String data = String.format("HS_%s_DATA", service);
            velocityContext.put(data, data);
            // 索引空间
            String idx = String.format("HS_%s_IDX", service);
            velocityContext.put(idx, idx);
            String lowService = service.toLowerCase();
            // g
            String group = String.format("app_%s_group", lowService);
            velocityContext.put(group, "g");
            // s
            String name = String.format("app_%s_name", lowService);
            velocityContext.put(name, "hswealth." + lowService);
            // v
            String version = String.format("app_%s_version", lowService);
            velocityContext.put(version, "v");
        }
        return velocityContext;
    }


}
