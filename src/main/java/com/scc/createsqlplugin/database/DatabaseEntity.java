package com.scc.createsqlplugin.database;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * @author: scc
 * @description:
 * @date:2023/3/9
 */
@XmlRootElement(name = "databases")
@XmlAccessorType(XmlAccessType.FIELD)
public class DatabaseEntity {

    @XmlElement(name = "database")
    private List<Database> databaseList;
    @XmlAttribute
    private String ip;
    @XmlAttribute
    private String port;
    @XmlAttribute
    private String username;
    @XmlAttribute
    private String password;


    public List<Database> getDatabaseList() {
        return databaseList;
    }

    public void setDatabaseList(List<Database> databaseList) {
        this.databaseList = databaseList;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
