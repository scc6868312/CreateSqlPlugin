package com.scc.createsqlplugin.database;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

/**
 * @author: scc
 * @description:
 * @date:2023/3/9
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class BaseDatabase {

    @XmlAttribute
    private String user;

    @XmlAttribute
    private String password;

    @XmlAttribute
    private String host;

    @XmlAttribute
    private String port;


    @XmlAttribute
    private String extendDbType;


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

    public String getExtendDbType() {
        return extendDbType;
    }

    public void setExtendDbType(String extendDbType) {
        this.extendDbType = extendDbType;
    }
}
