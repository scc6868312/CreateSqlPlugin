package com.scc.createsqlplugin.database;

/**
 * @author: scc
 * @description:
 * @date:2023/3/9
 */
public class Oracle extends BaseDatabase {
    private String serviceName;

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }
}
