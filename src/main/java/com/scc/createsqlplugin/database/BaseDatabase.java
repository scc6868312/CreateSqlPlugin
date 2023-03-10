package com.scc.createsqlplugin.database;

/**
 * @author: scc
 * @description:
 * @date:2023/3/9
 */
public class BaseDatabase {

    private String user;

    private String password;

    private String host;

    private String port;


    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }
}
