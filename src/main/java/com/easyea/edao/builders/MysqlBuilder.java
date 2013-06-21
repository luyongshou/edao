/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.easyea.edao.builders;

import com.easyea.edao.annotation.GenerationType;

/**
 *
 * @author louis
 */
public class MysqlBuilder extends AbstractBuilder {
    
    @Override
    public GenerationType getGenerationType() {
        return GenerationType.IDENTITY;
    }

    @Override
    public String getLimitSql(String sql, String start, String count) {
        return sql + ".append(\" limit  \").append(" + start 
                + ").append(\",\").append(" + count + ");";
    }

    @Override
    public String getNextId(Class cls) {
        return "";
    }

    @Override
    public String getDbTypeName() {
        return "Mysql";
    }
    
}
