package com.scc.createsqlplugin;

/**
 * @author: scc
 * @description:
 * @date:2023/3/9
 */
public enum DbDriver {

    MYSQL("mysql", "com.mysql.cj.jdbc.Driver"),
    ORACLE("oracle", "oracle.jdbc.driver.OracleDriver"),
    POSTGRES("postgresql", "org.postgresql.Driver");

    private final String dbType;
    private final String driverName;

    DbDriver(String dbType, String driverName) {
        this.dbType = dbType;
        this.driverName = driverName;
    }

    public String getDriverName() {
        return driverName;
    }

    public static DbDriver getDriverByDbType(String dbType) {
        for (DbDriver dbDriver : DbDriver.values()) {
            if (dbDriver.getDbType().equals(dbType)) {
                return dbDriver;
            }
        }
        return null;
    }

    public String getDbType() {
        return dbType;
    }
}
