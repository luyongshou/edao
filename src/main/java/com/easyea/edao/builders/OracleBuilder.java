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
public class OracleBuilder extends AbstractBuilder {
    
    public OracleBuilder(DaoManager manager) {
        super(manager);
    }

    @Override
    protected String getNextIdSql(String seqName) {
        return "SELECT " + seqName + ".nextval AS nid FROM DUAL";
    }

    @Override
    public String getMapDaoCode() throws Exception {
        return super.getMapDaoCode("Oracle");
    }

    @Override
    public DbProductName getDbProductName() {
        return DbProductName.Oracle;
    }
    
}
