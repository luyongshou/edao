/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.easyea.edao.builders;

import com.easyea.edao.DaoManager;
import com.easyea.edao.DbProductName;
import com.easyea.edao.exception.EntityException;
import com.easyea.edao.exception.ViewException;
import com.easyea.edao.managers.DefaultManager;
import java.util.logging.Level;
import java.util.logging.Logger;

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
    
    public static void main(String[] args) {
        DefaultManager manager = DefaultManager.getInstance();
        
        MysqlBuilder builder = new MysqlBuilder(manager);
        
        
    }

    @Override
    protected String getNextIdSql(String seqName) {
        return "SELECT NEXTVAL('" + seqName + "') AS nid";
    }

    
}
