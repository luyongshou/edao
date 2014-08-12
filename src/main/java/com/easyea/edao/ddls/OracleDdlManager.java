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
public class OracleDdlManager extends AbstractDdlManager {
    
    public OracleDdlManager(Class entity, Ddl ddl) {
        super(entity, ddl);
    }

    @Override
    public void syncDdl(Connection con) throws Exception {
        Ddl ddl = new OracleDdl();
        this.syncDdl(ddl, con);
    }

    @Override
    public boolean getPartitionEnable() {
        return false;
    }
    
}
