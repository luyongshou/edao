/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.easyea.edao.ddls;

import com.easyea.edao.Ddl;
import com.easyea.edao.DdlManager;
import com.easyea.edao.util.ClassUtil;
import com.easyea.logger.Logger;
import com.easyea.logger.LoggerFactory;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 *
 * @author louis
 */
public abstract class AbstractDdlManager implements DdlManager {
    
    Logger logger = LoggerFactory.getLogger(this.getClass());
    
    /**
     * 数据库中数据表的列表
     */
    protected List<String> tables;
    protected boolean isPartition;
    protected boolean isSync;
    protected boolean isSyncField;
    protected Date    lastSyncTime;
    protected Date    nextSyncTime;
    protected Class   entity;
    
    public AbstractDdlManager(Class entity) {
        this.tables      = null;
        this.entity      = entity;
        this.isSync      = false;
        this.isSyncField = false;
    }

    public boolean getIsPartition() {
        return isPartition;
    }

    public Date getLastSyncTime() {
        return lastSyncTime;
    }

    public Date getNextSyncTime() {
        return nextSyncTime;
    }

    public boolean isSync() {
        return isSync;
    }
    
    public boolean isSyncField() {
        return isSyncField;
    }
    
    public void syncDdl(Ddl ddl, Connection con) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("entity=[{}] syncDdl", entity.getName());
        }
        tables = ddl.getTables(entity, con);
        String tbName = ClassUtil.getTableName(entity);
        if (tables != null && !tables.isEmpty()) {
            if (tables.contains(tbName.toLowerCase(Locale.ENGLISH))) {
                this.isSync = true;
            }
        }
        if (!this.isSync) {
            List<String> sqls = ddl.getEntityCreateDdl(entity);
            if (logger.isDebugEnabled()) {
                logger.debug("sqls=[{}] syncDdl", sqls);
            }
            try {
                this.isSync = this.createTable(sqls, con);
            } catch (Exception e) {
                throw e;
            }
        }
        if (this.isSync) {
            if (!this.isSyncField) {
                doSyncColumn(ddl, con);
            }
        }
    }
    
    private boolean doSyncColumn(Ddl ddl, Connection con) throws Exception {
        boolean isFieldSync = false;
        List<String> sqls = ddl.getEntityUpdateDdl(entity, con);
        if (sqls != null && !sqls.isEmpty()) {
            Statement stmt = null;
            try {
                boolean isAuto = con.getAutoCommit();
                if (isAuto) {
                    con.setAutoCommit(false);
                }
                stmt = con.createStatement();
                for (String sql : sqls) {
                    stmt.executeUpdate(sql);
                }
                con.commit();
                if (isAuto) {
                    con.setAutoCommit(true);
                }
                return true;
            } catch (SQLException e) {
                try {con.rollback();} catch (Exception ex) {}
                throw e;
            } catch (Exception e) {
                throw e;
            } finally {
                if (stmt != null) {
                    try {stmt.close();} catch (Exception e) {}
                }
            }
        } else {
            isFieldSync = true;
        }
        return isFieldSync;
    }
    
    public boolean createTable(List<String> sqls, Connection con) throws Exception {
        Statement stmt = null;
        if (sqls == null || sqls.isEmpty()) {
            return false;
        }
        try {
            boolean isAuto = con.getAutoCommit();
            if (isAuto) {
                con.setAutoCommit(false);
            }
            stmt = con.createStatement();
            for (String sql : sqls) {
                stmt.executeUpdate(sql);
            }
            con.commit();
            if (isAuto) {
                con.setAutoCommit(true);
            }
            return true;
        } catch (SQLException e) {
            try {con.rollback();} catch (Exception ex) {}
            throw e;
        } catch (Exception e) {
            throw e;
        } finally {
            if (stmt != null) {
                try {stmt.close();} catch (Exception e) {}
            }
        }
    }
}
