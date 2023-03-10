package com.scc.createsqlplugin.database;

/**
 * @author: scc
 * @description:
 * @date:2023/3/9
 */
public class Postgresql extends BaseDatabase {
    private String database;

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }
}
