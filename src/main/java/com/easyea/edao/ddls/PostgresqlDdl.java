/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.easyea.edao.ddls;

import com.easyea.edao.annotation.Column;
import com.easyea.edao.annotation.Id;
import com.easyea.edao.annotation.Lob;
import com.easyea.edao.annotation.Temporal;
import com.easyea.edao.annotation.TemporalType;
import com.easyea.edao.exception.EntityException;
import com.easyea.edao.partition.PartitionParam;
import com.easyea.edao.util.ClassUtil;
import com.easyea.edao.util.FieldInfo;
import com.easyea.internal.CodeBuilder;
import java.lang.annotation.Annotation;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 *
 * @author louis
 */
public class PostgresqlDdl extends AbstractDdl {

    @Override
    public List<String> getEntityCreateDdl(Class entity) 
            throws EntityException, Exception {
        List<String> sqls = new ArrayList<String>();
        CodeBuilder sql = new CodeBuilder();

        //DDLManager ddlM = DDLManagerFactory.getDDLManager(con);
        String tbName = ClassUtil.getTableName(entity).toLowerCase();
        //List tbs = ddlM.getTables();
        
        List<FieldInfo> aField = ClassUtil.getFields(entity);
        ArrayList aF = new ArrayList();
        String    id = "";
        for (FieldInfo finfo : aField) {
            String f = finfo.getName();
            if (!f.equals("id")) {
                aF.add(f);
            } else {
                id = f;
            }
        }
        String shemas = "public";
        if (id.length() > 0) {
            sql.a("CREATE TABLE ").a(shemas).a(".")
                    .a(tbName).a(" (").r(1);
            Collections.sort(aF);
            aF.add(0, id);

            boolean isPrikey = false;
            String  flength  = "";
            String  prikey   = "";
            Column  dcolumn  = null;
            for (FieldInfo finfo : aField) {
                isPrikey = false;

                String       f     = finfo.getName();
                Annotation[] aann  = finfo.getAnnotations();
                Object       ftype = finfo.getType();
                String       fname = "";
                boolean      isLob = false;
                TemporalType tempType = null;
                if (aann != null) {
                    for (Annotation ann : aann) {
                        isLob = false;
                        if (ann instanceof Id) {
                            isPrikey = true;
                        }
                        if (ann instanceof Column) {
                            Column cann = (Column)ann;
                            fname = cann.name();
                            flength = cann.length() + "";
                            dcolumn = cann;
                        }
                        if (ann instanceof Lob) {
                            isLob = true;
                        }
                        if (ann instanceof Temporal) {
                            Temporal tann = (Temporal)ann;
                            tempType = tann.value();
                        }
                        if (fname.length() == 0) {
                            fname = f;
                        }
                    }
                } else {
                    if (f.equals("id")) {
                        isPrikey = true;
                    }
                    isLob = false;
                    fname = f;
                    flength = "255";
                    tempType = TemporalType.TIMESTAMP;
                }
                sql.t(1).a(fname).a(" ");
                if (isPrikey) {
                    prikey = fname;
                    if (ftype.equals(Long.class) 
                            || ftype.toString().equals("long")) {
                        sql.a(" BIGSERIAL NOT NULL");
                    } else if (ftype.equals(Integer.class)
                            || ftype.toString().equals("int")) {
                        sql.a(" SERIAL NOT NULL");
                    } else {
                        sql.a(" BIGSERIAL NOT NULL");
                    }
                } else {
                    if (ftype.equals(Long.class) 
                            || ftype.toString().equals("long")) {
                        sql.a(" BIGINT DEFAULT 0");
                    } else if (ftype.equals(Integer.class) 
                            || ftype.toString().equals("int")) {
                        sql.a(" INTEGER DEFAULT 0");
                    } else if (ftype.equals(Boolean.class) 
                            || ftype.toString().equals("boolean")) {
                        sql.a(" ﻿BOOLEAN");
                    } else if (ftype.equals(Double.class) 
                            || ftype.toString().equals("double")) {
                        sql.a(" numeric(");
                        if (dcolumn == null) {
                            sql.a("20,20");
                        } else {
                            sql.a(dcolumn.precision()).a(",");
                            sql.a(dcolumn.scale());
                        }
                        sql.a(") default 0");
                    } else if (ftype.equals(String.class)) {
                        if (isLob) {
                            sql.a(" TEXT DEFAULT ''");
                        } else {
                            sql.a(" VARCHAR(").a(flength).a(") DEFAULT ''");
                        }
                    } else if (ftype.equals(Date.class)) {
                        if (tempType == null) {
                            sql.a(" TIMESTAMP(6) WITH TIME ZONE");
                        } else if (tempType.equals(TemporalType.TIMESTAMP)) {
                            sql.a(" TIMESTAMP(6) WITH TIME ZONE");
                        } else if (tempType.equals(TemporalType.TIME)) {
                            sql.a(" TIME(6) WITHOUT TIME ZONE");
                        } else if (tempType.equals(TemporalType.DATE)) {
                            sql.a(" DATE");
                        }
                    }
                }
                sql.a(",").r(1);
            }
            sql.t(1).a("PRIMARY KEY(id)").r(1);
            sql.a(") WITHOUT OIDS;");
        }
        sqls.add(sql.toString());
        return sqls;
    }

    @Override
    public List<String> getViewCreateDdl(Class view) 
            throws EntityException, Exception {
        List<String> sqls = new ArrayList<String>();
        return sqls;
    }

    @Override
    public List<String> getViewUpdateDdl(Class view, Connection con) 
            throws EntityException, Exception {
        List<String> sqls = new ArrayList<String>();
        return sqls;
    }

    @Override
    public List<String> getTables(Class entity, Connection con) 
            throws EntityException, Exception {
        String           tbName = ClassUtil.getTableName(entity);
        tbName = tbName.toLowerCase(Locale.ENGLISH);
        List<String> tbs = this.getTablesByJdbc(con, 
                                                null, 
                                                null, 
                                                tbName, 
                                                new String[]{"TABLE"});
        return tbs;
    }

    protected void appendBooleanColumSqls(String       tableName, 
                                          String       colName, 
                                          Column       fCol,
                                          List<String> sqls) {
        String sql = "alter table " + tableName + " add column " + 
                colName + " boolean";
        sqls.add(sql);
    }
    
    protected void appendDateColumSqls(String       tableName, 
                                       String       colName, 
                                       Annotation[] anns,
                                       List<String> sqls) {
        Column       fCol     = null;
        String       colType  = "";
        TemporalType tempType = null;
        String       nullAble = "";
        if (anns != null) {
            for (Annotation ann : anns) {
                if (ann instanceof Column) {
                    fCol = (Column)ann;
                }
                if (ann instanceof Temporal) {
                    Temporal tann = (Temporal)ann;
                    tempType = tann.value();
                }
            }
        }
        if (tempType == null) {
            colType = " TIMESTAMP WITH TIME ZONE";
        } else if (tempType.equals(TemporalType.TIMESTAMP)) {
            colType = " TIMESTAMP WITH TIME ZONE";
        } else if (tempType.equals(TemporalType.TIME)) {
            colType = " TIME WITHOUT TIME ZONE";
        } else if (tempType.equals(TemporalType.DATE)) {
            colType = " DATE";
        }
        String sql = "alter table " + tableName + " add column " + 
                colName + " " + colType;
        if (fCol != null) {
            if (!fCol.nullable()) {
                nullAble = " NOT NULL";
            }
        }
        sql += nullAble;
        sqls.add(sql);
    }
    
    protected void appendStringColumSqls(String       tableName, 
                                         String       colName, 
                                         Annotation[] anns,
                                         List<String> sqls) {
        Column fCol     = null;
        String colType  = "varchar";
        Lob    lob      = null;
        String nullAble = "";
        if (anns != null) {
            for (Annotation ann : anns) {
                if (ann instanceof Column) {
                    fCol = (Column)ann;
                }
                if (ann instanceof Lob) {
                    lob = (Lob)ann;
                }
            }
        }
        String sql = "alter table " + tableName + " add column " + 
                colName + " ";
        int length = 255;
        if (fCol != null) {
            length = fCol.length();
            if (!fCol.nullable()) {
                nullAble = " NOT NULL";
            }
        }
        if (length < 1) {
            length = 1;
        }
        if (lob != null && length > 4000) {
            colType = "text";
        } else {
            colType += "(" + length + ")";
        }
        sql += colType + nullAble;
        sqls.add(sql);
    }
    
    protected void appendDoubleColumSqls(String       tableName, 
                                         String       colName, 
                                         Column       fCol,
                                         List<String> sqls) {
        String sql = "alter table " + tableName + " add column " + 
                colName + " ";
        int precision = 22;
        int scale     = 2;
        String nullAble = "";
        if (fCol != null) {
            precision = fCol.precision();
            scale     = fCol.scale();
            if (!fCol.nullable()) {
                nullAble = " not null";
            }
        }
        if (precision < 1) {
            precision = 1;
        }
        if (scale < 0) {
            scale = 0;
        }
        if (precision < scale) {
            precision = scale;
        }
        sql += "numberic(" + precision + "," +
                scale + ") DEFAULT 0" + nullAble;
        sqls.add(sql);
    }
    
    protected void appendFloatColumSqls(String       tableName, 
                                        String       colName, 
                                        Column       fCol,
                                        List<String> sqls) {
        String sql = "alter table " + tableName + " add column " + 
                colName + " ";
        int precision = 13;
        int scale     = 2;
        String nullAble = "";
        if (fCol != null) {
            precision = fCol.precision();
            scale     = fCol.scale();
            if (!fCol.nullable()) {
                nullAble = " not null";
            }
        }
        if (precision < 1) {
            precision = 1;
        }
        if (scale < 0) {
            scale = 0;
        }
        if (precision < scale) {
            precision = scale;
        }
        sql += "numberic(" + precision + "," +
                scale + ") DEFAULT 0" + nullAble;
        sqls.add(sql);
    }
    
    protected void appendLongColumSqls(String       tableName, 
                                       String       colName, 
                                       Column       fCol,
                                       List<String> sqls) {
        String sql = "alter table " + tableName + " add column " + 
                colName + " bigint";
        sqls.add(sql);
    }
    
    protected void appendIntColumSqls(String       tableName, 
                                      String       colName, 
                                      Column       fCol,
                                      List<String> sqls) {
        String sql = "alter table " + tableName + " add column " + 
                colName + " integer";
        sqls.add(sql);
    }

    @Override
    public String getTableName(Class entity) throws EntityException, Exception {
        return ClassUtil.getTableName(entity);
    }

    public List<String> getColumns(Class entity, Connection con) 
            throws EntityException, Exception {
        String tb = this.getTableName(entity);
        if (tb == null) {
            tb = "";
        }
        tb = tb.toLowerCase(Locale.ENGLISH);
        return this.getFieldsByJdbc(con, tb);
    }

    public List<String> getEntityPartitionDdl(Class entity, String extName) throws EntityException, Exception {
        String tbName = ClassUtil.getTableName(entity);
        PostgresqlDdlManager ddlm = new PostgresqlDdlManager(entity, null);
        PartitionParam pparam = ddlm.parsePartitionParam();
        List<String> sqls = new ArrayList<String>();
        if (extName != null && extName.length() > 2 && extName.startsWith("__")) {
            extName = extName.substring(2);
            int count = 1;
            int index = extName.indexOf("_");
            String type  = "";
            String ext   = "";
            String field = pparam.getField();
            if (index != -1) {
                type = extName.substring(0, index);
                ext  = extName.substring(index + 1);
                if (type.length() > 1) {
                    count = Integer.parseInt(type.substring(1));
                }
                if (type.toUpperCase(Locale.ENGLISH).startsWith("T")) {
                    sqls = getTimePartition(tbName, extName, field, count, ext);
                } else if (type.toUpperCase(Locale.ENGLISH).startsWith("N")) {
                    sqls = getNumberPartition(tbName, extName, field, count, ext);
                }
            }
        }
        return sqls;
    }
    
    private List<String> getTimePartition(String tbName, 
                                          String extName, 
                                          String field, 
                                          int    count, 
                                          String ext) {
        ArrayList<String> sqls = new ArrayList<String>();
        String checkStr = field + ">=";
        if (ext.length() == 4) {
            checkStr += "'" + ext + "-01-01 00:00:00' and " + field + "<='" + ext + 
                    "-12-31 23:59:59'";
        } else if (ext.length() == 6) {
            SimpleDateFormat monthF  = new SimpleDateFormat("yyyyMM");
            SimpleDateFormat month2F = new SimpleDateFormat("yyyy-MM");
            Date nowm = new Date();
            try {
                nowm = monthF.parse(ext);
            } catch (Exception e) {
                logger.error("partition ext name format error!", e);
            }
            Calendar cnow = Calendar.getInstance();
            cnow.setTime(nowm);
            cnow.add(Calendar.MONTH, 1);
            checkStr += "'" + ext.substring(0, 4) + "-" + ext.substring(4) + 
                    "-01 00:00:00' and " + field + "<'" 
                    + month2F.format(cnow.getTime()) + "-01 00:00:00'";
        } else {
            checkStr += "'" + ext + " 00:00:00' and " + field + "<='" + ext + 
                    " 23:59:59'";
        }
        sqls.add("CREATE TABLE " + tbName + "__" + extName + " (check (" + checkStr + 
                ")) INHERITS (" + tbName + ");");
        return sqls;
    }
    
    private List<String> getNumberPartition(String tbName, 
                                            String extName, 
                                            String field, 
                                            int    count, 
                                            String ext) {
        ArrayList<String> sqls = new ArrayList<String>();
        String checkStr = field + ">=(" + ext + "*" + (count*1000000) + 
                ") and " + field + "<((" + ext + 
                "+1)*" + (count*1000000) + ")";
        sqls.add("CREATE TABLE " + tbName + "__" + extName + " (check (" + checkStr + 
                ")) INHERITS (" + tbName + ");");
        return sqls;
    }
}
