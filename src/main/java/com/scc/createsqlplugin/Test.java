package com.scc.createsqlplugin;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 * @author: scc
 * @description:
 * @date:2023/3/9
 */
public class Test {
    public static void main(String[] args) {
        TGSqlParser sqlParser = new TGSqlParser(EDbVendor.dbvpostgresql);
        sqlParser.setSqltext("-- 增加索引\n" +
                "drop function if exists func_db_pg_iot();\n" +
                "create function func_db_pg_iot()\n" +
                "\treturns void\n" +
                "\tlanguage plpgsql\n" +
                "\tas $BODY$\n" +
                "declare\n" +
                "    v_rowcount integer;\n" +
                "begin\n" +
                "    select count(*) into v_rowcount FROM information_schema.columns WHERE table_schema=current_schema() and table_catalog=current_database() and table_name='its_prod_code';\n" +
                "    if v_rowcount > 0 then\n" +
                "        select count(*) into v_rowcount FROM pg_indexes WHERE schemaname=current_schema() and tablename='its_prod_code' and indexname='idx_prodcode_mpc';\n" +
                "        if v_rowcount = 0 then\n" +
                "\t\t\tinsert into a(a) values(1);\n" +
                "        end if;\n" +
                "    end if;\n" +
                "return;\n" +
                "end $BODY$;\n" +
                "select func_db_pg_iot();\n" +
                "drop function if exists func_db_pg_iot();");
        boolean result = sqlParser.parse() != 0;
        System.out.println(sqlParser.getErrormessage());

    }
}
