package com.scc.createsqlplugin.database;

import java.util.List;

/**
 * @author: scc
 * @description:
 * @date:2023/3/9
 */
public class DatabaseEntity {
    private List<Database> databases;

    public List<Database> getDatabases() {
        return databases;
    }

    public void setDatabases(List<Database> databases) {
        this.databases = databases;
    }
}
