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
public class MysqlBuilder extends AbstractBuilder {
    
    public MysqlBuilder(DaoManager manager) {
        super(manager);
    }

    @Override
    protected String getNextIdSql(String seqName) {
        return "";
    }

    @Override
    public String getMapDaoCode() throws Exception {
        return super.getMapDaoCode("Mysql");
    }

    @Override
    public DbProductName getDbProductName() {
        return DbProductName.Mysql;
    }
    
}
