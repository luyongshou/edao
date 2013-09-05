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
public class PostgresqlDdlManager extends AbstractDdlManager {
    
    
    
    public PostgresqlDdlManager(Class entity, Ddl ddl) {
        super(entity, ddl);
    }

    public void syncDdl(Connection con) throws Exception {
        Ddl ddl = new PostgresqlDdl();
        this.syncDdl(ddl, con);
    }

    public boolean getPartitionEnable() {
        return true;
    }
    
}
