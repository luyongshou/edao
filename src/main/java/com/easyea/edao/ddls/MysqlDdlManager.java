/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.easyea.edao.ddls;

import com.easyea.edao.Ddl;
import java.sql.Connection;

/**
 *
 * @author louis
 */
public class MysqlDdlManager extends AbstractDdlManager {
    
    public MysqlDdlManager(Class entity) {
        super(entity);
    }

    public void syncDdl(Connection con) throws Exception {
        Ddl ddl = new MysqlDdl();
        this.syncDdl(ddl, con);
    }
    
}
