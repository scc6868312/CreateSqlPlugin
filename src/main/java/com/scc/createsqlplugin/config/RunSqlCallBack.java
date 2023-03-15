package com.scc.createsqlplugin.config;

import org.apache.commons.lang3.StringUtils;
import org.apache.velocity.VelocityContext;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * @ProjectName its-tools
 * @Package common
 * @Description
 * @Author plus
 * @Date 2022/8/17 18:41
 * @UpdateRemark The modified content
 * @Version 1.0
 * <p>
 * Copyright © 2022 Hundsun Technologies Inc. All Rights Reserved
 **/
public interface RunSqlCallBack {
    /**
     * 根据dbType，获取要执行的文件
     *
     * @param dbType
     * @param executeUser
     * @return
     * @throws IOException
     */
    List<File> getFileList(String dbType, String executeUser) throws IOException;

}
