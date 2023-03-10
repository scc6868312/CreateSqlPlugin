package com.scc.createsqlplugin.database;

import java.util.List;

/**
 * @author: scc
 * @description:
 * @date:2023/3/9
 */
public class Database {

    private String type;

    private List<BaseDatabase>  databaseList;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<BaseDatabase> getDatabaseList() {
        return databaseList;
    }

    public void setDatabaseList(List<BaseDatabase> databaseList) {
        this.databaseList = databaseList;
    }
}
