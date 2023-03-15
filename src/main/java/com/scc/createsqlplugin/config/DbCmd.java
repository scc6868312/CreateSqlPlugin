package com.scc.createsqlplugin.config;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.scc.createsqlplugin.database.BaseDatabase;
import com.scc.createsqlplugin.database.MySQL;
import com.scc.createsqlplugin.database.Oracle;
import com.scc.createsqlplugin.database.Postgresql;
import org.apache.commons.lang3.StringUtils;

import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * @ProjectName its-tools
 * @Package common
 * @Description
 * @Author plus
 * @Date 2022/8/16 15:07
 * @UpdateRemark The modified content
 * @Version 1.0
 * <p>
 * Copyright © 2022 Hundsun Technologies Inc. All Rights Reserved
 **/
public class DbCmd {
    static Function<Oracle, String> oracleCmd = ((dataBaseEntry) ->
            // echo exit | ./sqlplus hs_ice/hundsun@10.20.44.131:1521/orcl @/home/dbClient/oracle/script/ljhrunsql/ice_ice_1002_sequencePatch.sql
            "source ~/.bash_profile && echo exit | ./sqlplus " + dataBaseEntry.getUser() + "/"
                    + dataBaseEntry.getPassword() + "@" + dataBaseEntry.getHost() + ":" + dataBaseEntry.getPort()
                    + "/" + dataBaseEntry.getServiceName() + " @");

    static Function<MySQL, String> mysqlCmd = ((dataBaseEntry) ->
            // ./mysql -h10.20.34.179 -P15002 -utest -p123456 --default-character-set=utf8 --max_allowed_packet=512M hs_ice < filename
            "./mysql -h" + dataBaseEntry.getHost() + " -P" + dataBaseEntry.getPort()
                    + " -u" + dataBaseEntry.getUser() + " -p" + dataBaseEntry.getPassword()
                    + " --default-character-set=utf8 --max_allowed_packet=512M " + dataBaseEntry.getDatabase() + " -v < ");

    static Function<Postgresql, String> postgresqlCmd = ((dataBaseEntry) ->
            // ./gsql -v ON_ERROR_STOP=1 -h{ip} -p{port} -d{database} -U{user} -W{paassword} -f{filename}
            "./psql -v ON_ERROR_STOP=1 -h " + dataBaseEntry.getHost() + " -p" + dataBaseEntry.getPort() + " -d " + dataBaseEntry.getDatabase()
                    + " -U " + dataBaseEntry.getUser() + " -W " + dataBaseEntry.getPassword() + " -f ");
    static Function<Postgresql, String> lightdbCmd = ((dataBaseEntry) ->
            // ./ltsql -h 10.20.29.37 -p 5432 -U hsiot  -d hs_iot_data -f /home/dbClient/postgresql/script/scc/its_iot_5013FileModel_pg.sql -a
            "PGPASSWORD=" + dataBaseEntry.getPassword() + " ./ltsql -h " + dataBaseEntry.getHost() + " -p " + dataBaseEntry.getPort() + " -U " + dataBaseEntry.getUser()
                    + " -d " + dataBaseEntry.getDatabase() + " -w -a -f");


    public static Table<String, String, Function<? extends BaseDatabase, String>> dbCmd = HashBasedTable.create();


    static {
        dbCmd.put("oracle", "!", oracleCmd);
        dbCmd.put("mysql", "!", mysqlCmd);
        dbCmd.put("postgresql", "!", postgresqlCmd);
        dbCmd.put("postgresql", "lightdb", lightdbCmd);
    }


    public static Function<? extends BaseDatabase, String> getCmd(String dbType, String extendDbType) {
        if (StringUtils.isBlank(extendDbType)) {
            extendDbType = "!";
        }
        return dbCmd.get(dbType, extendDbType);
    }


    public static <T extends BaseDatabase> String getCmd(String dbType, String extendDbType, T database) {
        if (StringUtils.isBlank(extendDbType)) {
            extendDbType = "!";
        }
        return ((Function<T, String>) dbCmd.get(dbType, extendDbType)).apply(database);

    }


}
