package com.scc.createsqlplugin.database;

import cn.hutool.core.io.FileUtil;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.InputStream;

import static com.scc.createsqlplugin.constant.Constant.*;


/**
 * @author: scc
 * @description:
 * @date:2023/3/9
 */
public class DataBaseParser {

    public static DatabaseEntity parseDatabase(String path) {

        try (InputStream input = FileUtil.getInputStream(path + File.separator + "database.xml")) {
            JAXBContext jaxbContext = JAXBContext.newInstance(DatabaseEntity.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            DatabaseEntity databaseEntity = (DatabaseEntity) unmarshaller.unmarshal(input);
            System.out.println(databaseEntity.toString());
            return databaseEntity;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Database findDatabase(String path, String type) {
        DatabaseEntity databaseEntity = parseDatabase(path);
        return databaseEntity.getDatabaseList().stream().filter(database -> database.getType().equalsIgnoreCase(type)).findFirst().orElseThrow(() -> new RuntimeException("Could not find database"));
    }


    public static String parseJdbcUrlByDatasource(BaseDatabase database, String dbType) {
        String jdbcUrl = "";
        switch (dbType) {
            case MYSQL_DB:
                MySQL mysql = (MySQL) database;
                assert mysql != null;
                jdbcUrl = "jdbc:mysql://" + mysql.getHost() + ":" + mysql.getPort() + "/" + mysql.getDatabase();
                break;
            case ORACLE_DB:
                Oracle oracle = (Oracle) database;
                assert oracle != null;
                jdbcUrl = "jdbc:oracle:thin:@//" + oracle.getHost() + ":" + oracle.getPort() + "/" + oracle.getServiceName();
                break;
            case POSTGRESQL_DB:
                Postgresql postgresql = (Postgresql) database;
                assert postgresql != null;
                jdbcUrl = "jdbc:postgresql://" + postgresql.getHost() + ":" + postgresql.getPort() + "/" + postgresql.getDatabase();
                break;
            default:
                break;
        }
        return jdbcUrl;
    }
}
