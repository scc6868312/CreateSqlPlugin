package com.scc.createsqlplugin.database;

import cn.hutool.core.util.XmlUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

import static com.scc.createsqlplugin.GenerateSqlDialog.*;

/**
 * @author: scc
 * @description:
 * @date:2023/3/9
 */
public class DataBaseParser {

    public static DatabaseEntity parseDatabase(String path) {
        DatabaseEntity entity = new DatabaseEntity();

        entity.setDatabases(new ArrayList<>());
        Document document = XmlUtil.readXML(path + "database.xml");
        Element documentElement = document.getDocumentElement();
        NodeList nodeList = documentElement.getElementsByTagName("database");
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            putAttribute(entity.getDatabases(), node);
        }
        return entity;
    }

    private static void putAttribute(List<Database> databases, Node node) {
        Database database = new Database();
        List<BaseDatabase> dbList = new ArrayList<>();
        Element element = (Element) node;
        String type = element.getAttribute("type");
        database.setType(type);
        NodeList childNodes = element.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node item = childNodes.item(i);
            String dbType = item.getNodeName();
            BaseDatabase baseDatabase = null;
            switch (dbType) {
                case MYSQL_DB:
                    MySQL mySQL = new MySQL();
                    mySQL.setDatabase(((Element) item).getAttribute("database"));
                    baseDatabase = mySQL;
                    break;
                case ORACLE_DB:
                    Oracle oracle = new Oracle();
                    oracle.setServiceName(((Element) item).getAttribute("serviceName"));
                    baseDatabase = oracle;
                    break;
                case POSTGRESQL_DB:
                    Postgresql postgresql = new Postgresql();
                    postgresql.setDatabase(((Element) item).getAttribute("database"));
                    baseDatabase = postgresql;
                    break;
                default:
                    continue;
            }
            baseDatabase.setHost(((Element) item).getAttribute("host"));
            baseDatabase.setPort(((Element) item).getAttribute("port"));
            baseDatabase.setUser(((Element) item).getAttribute("user"));
            baseDatabase.setPassword(((Element) item).getAttribute("password"));
            dbList.add(baseDatabase);
        }
        database.setDatabaseList(dbList);
        databases.add(database);

    }

    public static Database findDatabase(String path, String type) {
        DatabaseEntity databaseEntity = parseDatabase(path);
        return databaseEntity.getDatabases().stream().filter(database -> database.getType().equalsIgnoreCase(type)).findFirst().orElseThrow(() -> new RuntimeException("Could not find database"));
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
