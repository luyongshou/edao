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
import com.easyea.edao.util.FieldInfo;
import com.easyea.internal.CodeBuilder;
import java.lang.annotation.Annotation;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import javax.sql.DataSource;

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
        String id = "";
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
                    for (int i=0;i<aann.length;i++) {
                        Annotation ann = aann[i];
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
    public List<String> getEntityUpdateDdl(Class entity, DataSource ds) 
            throws EntityException, Exception {
        List<String> sqls = new ArrayList<String>();
        return sqls;
    }

    @Override
    public List<String> getViewCreateDdl(Class view) 
            throws EntityException, Exception {
        List<String> sqls = new ArrayList<String>();
        return sqls;
    }

    @Override
    public List<String> getViewUpdateDdl(Class view, DataSource ds) 
            throws EntityException, Exception {
        List<String> sqls = new ArrayList<String>();
        return sqls;
    }

    @Override
    public List<String> getTables(Class entity, DataSource ds) throws EntityException, Exception {
        String           tbName = ClassUtil.getTableName(entity);
        tbName = tbName.toLowerCase(Locale.ENGLISH);
        List<String> tbs = this.getTablesByJdbc(ds, 
                                                null, 
                                                null, 
                                                tbName, 
                                                new String[]{"TABLE"});
        return tbs;
    }
}
