/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.easyea.edao.builders;

import com.easyea.edao.DaoManager;
import com.easyea.edao.DbProductName;

/**
 *
 * @author louis
 */
public class PostgresqlBuilder extends AbstractBuilder {
    
    public PostgresqlBuilder(DaoManager manager) {
        super(manager);
    }

    @Override
    public String getMapDaoCode() throws Exception {
        return super.getMapDaoCode("Postgresql");
    }
    
    @Override
    public DbProductName getDbProductName() {
        return DbProductName.Postgresql;
    }

    @Override
    protected String getNextIdSql(String seqName) {
        return "SELECT NEXTVAL('" + seqName + "') AS nid";
    }

    @Override
    protected String getLimitSQLFun() {
        StringBuilder fun = new StringBuilder();
        fun.append("private String getLimitSql(String sql, long start, int count) {\n");
        fun.append("\t\treturn sql += \" limit \" + count + \" offset \" + start;\n");
        fun.append("\t}");
        return fun.toString();
    }

    
}
