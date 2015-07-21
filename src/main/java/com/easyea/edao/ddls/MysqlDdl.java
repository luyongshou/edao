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
import com.easyea.edao.util.ClassUtil;
import com.easyea.internal.CodeBuilder;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 *
 * @author louis
 */
public class MysqlDdl extends AbstractDdl {

    @Override
    public List<String> getEntityCreateDdl(Class entity) throws EntityException, Exception {
        List<String> l = null;
        try {
            //Class cls = Class.forName(entity);
            //DDLManager ddlM = DDLManagerFactory.getDDLManager(con);
            String tbName = ClassUtil.getTableName(entity).toUpperCase();
            //List tbs = ddlM.getTables();
            
            List<Field> fields = ClassUtil.getFields(entity);
            ArrayList aF = new ArrayList();
            String id = "";
            for (Field fi : fields) {
                String f = fi.getName();
                if (!f.equals("id")) {
                    aF.add(f);
                } else {
                    id = f;
                }
            }
            CodeBuilder sql = new CodeBuilder();
            l = new ArrayList();
            if (fields != null && !fields.isEmpty()) {
                sql.a("CREATE TABLE ").a(tbName).a(" (").r(1);
                Collections.sort(aF);
                aF.add(0, id);

                boolean isPrikey = false;
                String flength   = "255";
                String prikey    = "";
                Column dcolumn   = null;
                for (Field fi : fields) {
                    isPrikey = false;

                    String       f        = fi.getName();
                    Annotation[] aann     = fi.getAnnotations();
                    Class        ftype    = fi.getType();
                    String       fname    = f;
                    boolean      isLob    = false;
                    TemporalType tempType = null;
                    if (aann != null && aann.length > 0) {
                        //System.out.println(f);
                        int slength = 4000;
                        for (Annotation ann : aann) {
                            if (ann instanceof Id) {
                                isPrikey = true;
                            }
                            if (ann instanceof Column) {
                                Column cann = (Column)ann;
                                if (cann.name() != null 
                                        && cann.name().trim().length() > 0) {
                                    fname   = cann.name();
                                }
                                flength = cann.length() + "";
                                slength = cann.length();
                                dcolumn = cann;
                            }
                            if ((ann instanceof Lob && slength > 4000) || isLob) {
                                isLob = true;
                            } else {
                                if (slength > 4000) {
                                    isLob = true;
                                }
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
                    //System.out.println(isLob);
                    sql.t(1).a(fname).a(" ");

                    if (isPrikey) {
                        prikey = fname;
                        if (ftype.equals(Long.class) 
                                || ftype.toString().equals("long")) {
                            sql.a(" BIGINT NOT NULL AUTO_INCREMENT");
                        } else if (ftype.equals(Integer.class)
                                || ftype.toString().equals("int")) {
                            sql.a(" int NOT NULL AUTO_INCREMENT");
                        } else {
                            sql.a(" BIGINT NOT NULL AUTO_INCREMENT");
                        }
                    } else {
                        if (ftype.equals(Long.class) 
                                || ftype.toString().equals("long")) {
                            sql.a(" bigint(20) DEFAULT 0");
                        } else if (ftype.equals(Integer.class) 
                                || ftype.toString().equals("int")) {
                            sql.a(" int DEFAULT 0");
                        } else if (ftype.equals(Boolean.class) 
                                || ftype.toString().equals("boolean")) {
                            sql.a(" smallint default -1");
                        } else if (ftype.equals(Double.class) 
                                || ftype.toString().equals("double")) {
                            sql.a(" decimal(");
                            if (dcolumn == null) {
                                sql.a("20,5");
                            } else {
                                sql.a(dcolumn.precision()).a(",");
                                sql.a(dcolumn.scale());
                            }
                            sql.a(") default 0");
                        } else if (ftype.equals(String.class)) {
                            if (isLob) {
                                sql.a(" text");
                            } else {
                                sql.a(" VARCHAR(").a(flength)
                                        .a(") DEFAULT ''");
                            }
                        } else if (ftype.equals(Date.class)) {
                            if (tempType == null) {
                                sql.a(" datetime");
                            } else if (tempType.equals(TemporalType.TIMESTAMP)) {
                                sql.a(" timestamp");
                            } else if (tempType.equals(TemporalType.TIME)) {
                                sql.a(" time");
                            } else if (tempType.equals(TemporalType.DATE)) {
                                sql.a(" date");
                            }
                        }
                    }
                    sql.a(",").r(1);
                }
                sql.t(1).a("primary key(id)").r(1);
                sql.a(")").r(1);
                l.add(sql.toString());
            }
        } catch (Exception e) {
            throw e;
        }
        return l;
    }

    @Override
    public List<String> getEntityUpdateDdl(Class entity, Connection con) throws EntityException, Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<String> getViewCreateDdl(Class view) throws EntityException, Exception {
        return null;
        //
    }

    @Override
    public List<String> getViewUpdateDdl(Class view, Connection con) 
            throws EntityException, Exception {
        return null;
    }

    @Override
    public List<String> getTables(Class entity, Connection con) throws EntityException, Exception {
        String           tbName = ClassUtil.getTableName(entity);
        tbName = tbName.toLowerCase(Locale.ENGLISH);
        List<String> tbs = this.getTablesByJdbc(con, 
                                                null, 
                                                null, 
                                                tbName, 
                                                new String[]{"TABLE"});
        return tbs;
    }

    @Override
    protected void appendBooleanColumSqls(String       tableName, 
                                          String       colName, 
                                          Column       fCol,
                                          List<String> sqls) {
        String sql = "alter table " + tableName + " add column " + 
                colName + " boolean";
        sqls.add(sql);
    }
    
    @Override
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
            colType = " TIMESTAMP";
        } else if (tempType.equals(TemporalType.TIMESTAMP)) {
            colType = " TIMESTAMP";
        } else if (tempType.equals(TemporalType.TIME)) {
            colType = " TIME";
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
    
    @Override
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
        if (length > 255) {
            colType = "text";
        } else {
            colType += "(" + length + ")";
        }
        sql += colType + nullAble;
        sqls.add(sql);
    }
    
    @Override
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
        sql += "decimal(" + precision + "," +
                scale + ") DEFAULT 0" + nullAble;
        sqls.add(sql);
    }
    
    @Override
    protected void appendFloatColumSqls(String       tableName, 
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
        sql += "float(" + precision + "," +
                scale + ") DEFAULT 0" + nullAble;
        sqls.add(sql);
    }
    
    @Override
    protected void appendLongColumSqls(String       tableName, 
                                       String       colName, 
                                       Column       fCol,
                                       List<String> sqls) {
        int length = 20;
        if (fCol != null) {
            length = fCol.length();
        }
        String sql = "alter table " + tableName + " add column " + 
                colName + " bigint(" + length + ")";
        sqls.add(sql);
    }
    
    @Override
    protected void appendIntColumSqls(String       tableName, 
                                      String       colName, 
                                      Column       fCol,
                                      List<String> sqls) {
        int length = 10;
        if (fCol != null) {
            length = fCol.length();
        }
        String sql = "alter table " + tableName + " add column " + 
                colName + " integer(" + length + ")";
        sqls.add(sql);
    }

    @Override
    public String getTableName(Class entity) throws EntityException, Exception {
        return ClassUtil.getTableName(entity);
    }

    @Override
    public List<String> getColumns(Class entity, Connection con) 
            throws EntityException, Exception {
        return this.getFieldsByJdbc(con, this.getTableName(entity));
    }

    @Override
    public List<String> getEntityPartitionDdl(Class entity, String extName) throws EntityException, Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
