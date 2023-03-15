package com.scc.createsqlplugin.database;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

/**
 * @author: scc
 * @description:
 * @date:2023/3/9
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class Database {

    @XmlAttribute(name = "type")
    private String type;

    @XmlElement(name = "mysql")
    private MySQL mySQL;

    @XmlElement(name = "oracle")
    private Oracle oracle;

    @XmlElement(name = "postgresql")
    private Postgresql postgresql;

    public MySQL getMySQL() {
        return mySQL;
    }

    public void setMySQL(MySQL mySQL) {
        this.mySQL = mySQL;
    }

    public Oracle getOracle() {
        return oracle;
    }

    public void setOracle(Oracle oracle) {
        this.oracle = oracle;
    }

    public Postgresql getPostgresql() {
        return postgresql;
    }

    public void setPostgresql(Postgresql postgresql) {
        this.postgresql = postgresql;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
