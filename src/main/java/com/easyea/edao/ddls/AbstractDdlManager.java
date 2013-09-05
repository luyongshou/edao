/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.easyea.edao.ddls;

import com.easyea.edao.Ddl;
import com.easyea.edao.DdlManager;
import com.easyea.edao.annotation.partition.NumberRangePartition;
import com.easyea.edao.annotation.partition.TimeRangePartition;
import com.easyea.edao.exception.EntityException;
import com.easyea.edao.partition.NumberRange;
import com.easyea.edao.partition.PartitionParam;
import com.easyea.edao.partition.TimeRange;
import com.easyea.edao.util.ClassUtil;
import com.easyea.edao.util.FieldInfo;
import com.easyea.logger.Logger;
import com.easyea.logger.LoggerFactory;
import java.lang.annotation.Annotation;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
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
    protected List<String>   tables;
    protected boolean        isPartition;
    protected boolean        isSync;
    protected boolean        isSyncField;
    protected PartitionParam partitionParam;
    protected Date           lastSyncTime;
    protected Date           nextSyncTime;
    protected Class          entity;
    protected Ddl            ddl;
    
    public AbstractDdlManager(Class entity, Ddl ddl) {
        this.tables      = null;
        this.entity      = entity;
        this.isSync      = false;
        this.isSyncField = false;
        this.ddl         = ddl;
        boolean isPart   = false;
        PartitionParam   partParam = null;
        try {
            partParam = this.parsePartitionParam();
        } catch (Exception e) {
            logger.error("get partition param error!", e);
        }
        if (partParam != null) {
            partitionParam = partParam;
            isPart = true;
        } else {
            partitionParam = null;
        }
        this.isPartition = isPart;
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
        if (tables == null) {
            tables = new ArrayList<String>();
        }
        if (!this.isSync) {
            List<String> sqls = ddl.getEntityCreateDdl(entity);
            if (logger.isDebugEnabled()) {
                logger.debug("sqls=[{}] syncDdl", sqls);
            }
            try {
                this.isSync = this.createTable(sqls, con);
                tables.add(tbName.toLowerCase(Locale.ENGLISH));
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
    
    public Ddl getDdl() {
        return this.ddl;
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
    
    public boolean hasPartitionTable(String table) {
        return tables.contains(table.toLowerCase(Locale.ENGLISH));
    }
    
    public void createPartitionTable(Class entity, String extName, Connection con) 
            throws SQLException, Exception {
        if (ddl != null) {
            List<String> sqls = ddl.getEntityPartitionDdl(entity, extName);
            if (sqls != null && !sqls.isEmpty()) {
                Statement stmt = null;
                try {
                    stmt = con.createStatement();
                    for (String sql : sqls) {
                        stmt.executeUpdate(sql);
                    }
                    String tableName = ClassUtil.getTableName(entity);
                    tables.add((tableName + extName).toLowerCase(Locale.ENGLISH));
                } catch (SQLException sqle) {
                    throw sqle;
                } finally {
                    if (stmt != null) {
                        try {stmt.close();} catch (Exception e) {}
                    }
                }
            }
        }
    }
    
    public PartitionParam parsePartitionParam() throws EntityException, Exception {
        Annotation[] anns = entity.getAnnotations();
        if (anns != null) {
            for (Annotation ann : anns) {
                if (ann instanceof NumberRangePartition) {
                    NumberRangePartition nump = (NumberRangePartition)ann;
                    String field = nump.field();
                    Class  ftype = null;
                    NumberRange param = new NumberRange();
                    param.setField(field);
                    List<FieldInfo> fis = ClassUtil.getFields(entity);
                    if (fis != null && !fis.isEmpty()) {
                        for (FieldInfo fi : fis) {
                            if (fi.getName().equals(field)) {
                                ftype = fi.getType();
                            }
                        }
                    }
                    if (ftype == null) {
                        throw new Exception("entity not declare \"" + field + "\"");
                    }
                    int count = nump.count();
                    if (count < 1) {
                        count = 1;
                    }
                    param.setFieldType(ftype);
                    param.setCount(count);
                    param.setCustomerRange(null);
                    param.setInterval(nump.interval());
                    return param;
                } else if (ann instanceof TimeRangePartition) {
                    TimeRangePartition nump = (TimeRangePartition)ann;
                    String field = nump.field();
                    Class  ftype = null;
                    TimeRange param = new TimeRange();
                    param.setField(field);
                    List<FieldInfo> fis = ClassUtil.getFields(entity);
                    if (fis != null && !fis.isEmpty()) {
                        for (FieldInfo fi : fis) {
                            if (fi.getName().equals(field)) {
                                ftype = fi.getType();
                            }
                        }
                    }
                    if (ftype == null) {
                        throw new Exception("entity not declare \"" + field + "\"");
                    }
                    int count= nump.count();
                    if (count < 1) {
                        count = 1;
                    }
                    param.setFieldType(ftype);
                    param.setCount(count);
                    param.setCustomerRange(null);
                    param.setInterval(nump.interval());
                    return param;
                }
            }
        }
        return null;
    }
    
    public PartitionParam getPartitionParam() 
            throws EntityException, Exception {
        return this.partitionParam;
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
