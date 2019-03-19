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
 * Oracle数据库根据持久化Bean以及试图Bean反射生成创建一个更新数据表结构的DDL语句的类
 * @author louis
 */
public class OracleDdl extends AbstractDdl {

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
                String flength   = "";
                String prikey    = "";
                Column dcolumn   = null;
                for (Field fi : fields) {
                    isPrikey = false;

                    String       f        = fi.getName();
                    Annotation[] aann     = fi.getAnnotations();
                    Class        ftype    = fi.getType();
                    String       fname    = "";
                    boolean      isLob    = false;
                    TemporalType tempType = null;
                    if (aann != null) {
                        //System.out.println(f);
                        int slength = 4000;
                        for (Annotation ann : aann) {
                            if (ann instanceof Id) {
                                isPrikey = true;
                            }
                            if (ann instanceof Column) {
                                Column cann = (Column)ann;
                                fname = cann.name();
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
                            sql.a(" NUMBER(20,0) NOT NULL");
                        } else if (ftype.equals(Integer.class)
                                || ftype.toString().equals("int")) {
                            sql.a(" NUMBER(10,0) NOT NULL");
                        } else {
                            sql.a(" NUMBER(20,0) NOT NULL");
                        }
                    } else {
                        if (ftype.equals(Long.class) 
                                || ftype.toString().equals("long")) {
                            sql.a(" NUMBER(20,0) DEFAULT 0");
                        } else if (ftype.equals(Integer.class) 
                                || ftype.toString().equals("int")) {
                            sql.a(" NUMBER(10,0) DEFAULT 0");
                        } else if (ftype.equals(Boolean.class) 
                                || ftype.toString().equals("boolean")) {
                            sql.a(" NUMBER(10,0)");
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
                                sql.a(" CLOB DEFAULT ''");
                            } else {
                                sql.a(" VARCHAR2(").a(flength)
                                        .a(") DEFAULT ''");
                            }
                        } else if (ftype.equals(Date.class)) {
                            if (tempType == null) {
                                sql.a(" TIMESTAMP WITH TIME ZONE");
                            } else if (tempType.equals(TemporalType.TIMESTAMP)) {
                                sql.a(" TIMESTAMP WITH TIME ZONE");
                            } else if (tempType.equals(TemporalType.TIME)) {
                                sql.a(" TIME WITHOUT TIME ZONE");
                            } else if (tempType.equals(TemporalType.DATE)) {
                                sql.a(" DATE");
                            }
                        }
                    }
                    sql.a(",").r(1);
                }
                sql.t(1).a("PRIMARY KEY(id)").r(1);
                sql.a(")").r(1);
                l.add(sql.toString());
                
                sql = new CodeBuilder();
                String seqName = ClassUtil.getSeqName(entity);
                sql.a("create sequence ").a(seqName)
                        .a(" minvalue 1 NOMAXVALUE ")
                        .a("increment by 1 start with 1 NOCYCLE").r(1);
                l.add(sql.toString());
                
                sql = new CodeBuilder();
                sql.a("create or replace trigger ").a(tbName).a("_IDTRI").r(1);
                sql.a("before insert on ").a(tbName).r(1);
                sql.a("for each row").r(1);
                sql.a("begin").r(1);
                sql.a("if :new.ID is NULL then").r(1);
                sql.a("SELECT ").a(seqName).a(".nextval INTO :new.ID from DUAL;").r(1);
                sql.a("end if;").r(1);
                sql.a("end;").r(2);
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
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<String> getViewUpdateDdl(Class view, Connection con) 
            throws EntityException, Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<String> getTables(Class entity, Connection con) 
            throws EntityException, Exception {
        String tbName = ClassUtil.getTableName(entity);
        tbName = tbName.toUpperCase(Locale.ENGLISH);
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
        StringBuilder sql = new StringBuilder();
        sql.append("ALTER TABLE ").append(tableName).append(" ADD (")
                .append(colName).append(" ");
        sql.append("CHAR(1) CHECK (").append(colName).append(" IN (0,1))");
        sql.append(")");
        sqls.add(sql.toString());
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
            colType = " TIMESTAMP WITH TIME ZONE";
        } else if (tempType.equals(TemporalType.TIMESTAMP)) {
            colType = " TIMESTAMP WITH TIME ZONE";
        } else if (tempType.equals(TemporalType.TIME)) {
            colType = " TIME WITHOUT TIME ZONE";
        } else if (tempType.equals(TemporalType.DATE)) {
            colType = " DATE";
        }
        StringBuilder sql = new StringBuilder();
        sql.append("ALTER TABLE ").append(tableName).append(" ADD (")
                .append(colName).append(" ");
        if (fCol != null) {
            if (!fCol.nullable()) {
                nullAble = " NOT NULL";
            }
        }
        sql.append(colType).append("").append(nullAble);
        sql.append(")");
        sqls.add(sql.toString());
    }
    
    @Override
    protected void appendStringColumSqls(String       tableName, 
                                       String       colName, 
                                       Annotation[] anns,
                                       List<String> sqls) {
        Column fCol     = null;
        String colType  = "varchar2";
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
        StringBuilder sql = new StringBuilder();
        sql.append("ALTER TABLE ").append(tableName).append(" ADD (")
                .append(colName).append(" ");
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
            colType = "CLOB";
        } else {
            colType += "(" + length + ")";
        }
        sql.append(colType).append("").append(nullAble);
        sql.append(")");
        sqls.add(sql.toString());
    }
    
    @Override
    protected void appendDoubleColumSqls(String       tableName, 
                                       String       colName, 
                                       Column       fCol,
                                       List<String> sqls) {
        StringBuilder sql = new StringBuilder();
        sql.append("ALTER TABLE ").append(tableName).append(" ADD (")
                .append(colName).append(" ");
        int precision = 15;
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
        sql.append("NUMBER(").append(precision).append(",")
                .append(scale).append(") DEFAULT 0").append(nullAble);
        sql.append(")");
        sqls.add(sql.toString());
    }
    
    @Override
    protected void appendFloatColumSqls(String       tableName, 
                                        String       colName, 
                                        Column       fCol,
                                        List<String> sqls) {
        StringBuilder sql = new StringBuilder();
        sql.append("ALTER TABLE ").append(tableName).append(" ADD (")
                .append(colName).append(" ");
        int precision = 15;
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
        sql.append("NUMBER(").append(precision).append(",")
                .append(scale).append(") DEFAULT 0").append(nullAble);
        sql.append(")");
        sqls.add(sql.toString());
    }
    
    @Override
    protected void appendLongColumSqls(String       tableName, 
                                       String       colName, 
                                       Column       fCol,
                                       List<String> sqls) {
        StringBuilder sql = new StringBuilder();
        sql.append("ALTER TABLE ").append(tableName).append(" ADD (")
                .append(colName).append(" ");
        int precision = 20;
        int scale     = 0;
        String nullAble = "";
        if (fCol != null) {
            precision = fCol.precision();
            scale     = fCol.scale();
            if (!fCol.nullable()) {
                nullAble = " not null";
            }
        }
        if (precision < 11) {
            precision = 11;
        }
        if (scale != 0) {
            scale = 0;
        }
        sql.append("NUMBER(").append(precision).append(",")
                .append(scale).append(") DEFAULT 0").append(nullAble);
        sql.append(")");
        sqls.add(sql.toString());
    }
    
    @Override
    protected void appendIntColumSqls(String tableName, 
                                 String colName, 
                                 Column fCol,
                                 List<String> sqls) {
        StringBuilder sql = new StringBuilder();
        sql.append("ALTER TABLE ").append(tableName).append(" ADD (")
                .append(colName).append(" ");
        int precision = 11;
        int scale     = 0;
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
        if (scale != 0) {
            scale = 0;
        }
        sql.append("NUMBER(").append(precision).append(",")
                .append(scale).append(") DEFAULT 0").append(nullAble);
        sql.append(")");
        sqls.add(sql.toString());
    }

    @Override
    public String getTableName(Class entity) throws EntityException, Exception {
        String tb = ClassUtil.getTableName(entity);
        return tb.toUpperCase(Locale.ENGLISH);
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

    @Override
    protected void appendJsonbColumSqls(String tableName, String colName, Annotation[] anns, List<String> sqls) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void appendJsonColumSqls(String tableName, String colName, Annotation[] anns, List<String> sqls) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
