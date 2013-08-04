/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.easyea.edao.ddls;

import com.easyea.edao.Ddl;
import com.easyea.edao.annotation.Column;
import com.easyea.edao.exception.EntityException;
import com.easyea.edao.util.ClassUtil;
import com.easyea.edao.util.FieldInfo;
import com.easyea.logger.Logger;
import com.easyea.logger.LoggerFactory;
import java.lang.annotation.Annotation;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 *
 * @author louis
 */
public abstract class AbstractDdl implements Ddl {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 利用JDBC获取一个连接中所有的与持久化bean使用的所有表名
     *
     * @param con
     * @param catalog
     * @param schema
     * @param tableName 持久化Bean的表名不带分区扩展名的基础名称
     * @param types
     * @return
     * @throws EntityException
     * @throws Exception
     */
    public List<String> getTablesByJdbc(Connection con,
            String catalog,
            String schema,
            String tableName,
            String[] types)
            throws EntityException, Exception {
        List<String> tbs = null;
        try {
            DatabaseMetaData dbData = con.getMetaData();
            ResultSet rs = dbData.getTables(catalog,
                    schema,
                    tableName + "%",
                    types);
            ResultSetMetaData rsmData = rs.getMetaData();
            int tbNameIndex = 0;
            for (int i = 1; i <= rsmData.getColumnCount(); i++) {
                if ("table_name".equalsIgnoreCase(rsmData.getColumnLabel(i))) {
                    tbNameIndex = i;
                }
            }
            tableName = tableName.toLowerCase(Locale.ENGLISH);
            while (rs.next()) {
                if (tbs == null) {
                    tbs = new ArrayList<String>();
                }
                String tname = rs.getString(tbNameIndex);
                if (tname != null && tname.length() > 0) {
                    tname = tname.toLowerCase(Locale.ENGLISH);
                    if (tableName.equals(tname)
                            || tname.startsWith(tableName + "__")) {
                        tbs.add(tname);
                    }
                }
            }
        } catch (SQLException e) {
            logger.error("{}", e);
            throw e;
        }
        return tbs;
    }
    
    public abstract String getTableName(Class entity) 
            throws EntityException, Exception;
    
    /**
     * 通过JDBC获取一个表的所有的字段名
     * @param con 数据库连接
     * @param tableName 数据表名，表名区分大小写，传入时需要制定大小写
     * @return
     * @throws Exception 
     */
    public List<String> getFieldsByJdbc(Connection con, String tableName)
            throws Exception {
        List<String> fields = new ArrayList<String>();
        try {
            ResultSet rs = con.getMetaData().getColumns(null, null, tableName, null);
            if (rs != null) {
                ResultSetMetaData rsmData = rs.getMetaData();
                int fieldIndex = 0;
                for (int i = 1; i <= rsmData.getColumnCount(); i++) {
                    if (rsmData.getColumnLabel(i).equalsIgnoreCase("COLUMN_NAME")) {
                        fieldIndex = i;
                    }
                }
                while (rs.next()) {
                    fields.add(rs.getString(fieldIndex).toLowerCase(Locale.ENGLISH));
                }
            }
        } catch (SQLException sqle) {
            throw sqle;
        }
        return fields;
    }
    
    public List<String> getAddColumnSqls(String tableName, FieldInfo field) {
        List<String> sqls = new ArrayList<String>();
        if (field != null) {
            Class  ftype   = field.getType();
            String colName = ClassUtil.getColumnName(field);
            colName = colName.toUpperCase(Locale.ENGLISH);
            Annotation[] anns = field.getAnnotations();
            Column fCol = null;
            if (anns != null) {
                for (Annotation ann : anns) {
                    if (ann instanceof Column) {
                        fCol = (Column)ann;
                    }
                }
            }
            if (ftype.equals(Long.class) || ftype.toString().equals("long")) {
                appendLongColumSqls(tableName, colName, fCol, sqls);
            } else if (ftype.equals(Integer.class) || ftype.toString().equals("int")) {
                appendIntColumSqls(tableName, colName, fCol, sqls);
            } else if (ftype.equals(String.class)) {
                appendStringColumSqls(tableName, colName, anns, sqls);
            } else if (ftype.equals(Float.class) || ftype.toString().equals("float")) {
                appendFloatColumSqls(tableName, colName, fCol, sqls);
            } else if (ftype.equals(Double.class) || ftype.toString().equals("double")) {
                appendDoubleColumSqls(tableName, colName, fCol, sqls);
            } else if (ftype.equals(Date.class)) {
                appendDateColumSqls(tableName, colName, anns, sqls);
            } else if (ftype.equals(Boolean.class) || ftype.toString().equals("boolean")) {
                appendBooleanColumSqls(tableName, colName, fCol, sqls);
            }
        }
        return sqls;
    }
    
    public List<String> getEntityUpdateDdl(Class entity, Connection con) 
            throws EntityException, Exception {
        List<String> sqls = new ArrayList<String>();
        List<FieldInfo> fields = ClassUtil.getFields(entity);
        if (fields != null) {
            List<String> cols = null;
            try {
                cols = this.getColumns(entity, con);
            } catch (Exception e) {
                throw e;
            }
            
            if (cols != null) {
                for (FieldInfo fi : fields) {
                    String col = ClassUtil.getColumnName(fi);
                    if (!cols.contains(col)) {
                        sqls.addAll(this.getAddColumnSqls(this.getTableName(entity), fi));
                    }
                }
            }
        }
        return sqls;
    }
    
    protected abstract void appendBooleanColumSqls(String       tableName, 
                                                   String       colName, 
                                                   Column       fCol,
                                                   List<String> sqls);
    
    protected abstract void appendDateColumSqls(String       tableName, 
                                                String       colName, 
                                                Annotation[] anns,
                                                List<String> sqls);
    
    protected abstract void appendStringColumSqls(String       tableName, 
                                                  String       colName, 
                                                  Annotation[] anns,
                                                  List<String> sqls);
    
    protected abstract void appendDoubleColumSqls(String       tableName, 
                                                  String       colName, 
                                                  Column       fCol,
                                                  List<String> sqls);
    
    protected abstract void appendFloatColumSqls(String       tableName, 
                                                 String       colName, 
                                                 Column       fCol,
                                                 List<String> sqls);
    
    protected abstract void appendLongColumSqls(String       tableName, 
                                                String       colName, 
                                                Column       fCol,
                                                List<String> sqls);
    
    protected abstract void appendIntColumSqls(String       tableName, 
                                               String       colName, 
                                               Column       fCol,
                                               List<String> sqls);
}
