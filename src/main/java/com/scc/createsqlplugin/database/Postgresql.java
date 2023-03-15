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
public class Postgresql extends BaseDatabase {
    @XmlAttribute
    private String database;

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }
}
