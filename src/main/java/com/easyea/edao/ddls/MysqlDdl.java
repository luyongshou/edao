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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import javax.sql.DataSource;

/**
 *
 * @author louis
 */
public class MysqlDdl extends AbstractDdl {

    public List<String> getEntityCreateDdl(Class entity) throws EntityException, Exception {
        List<String> l = null;
        try {
            //Class cls = Class.forName(entity);
            //DDLManager ddlM = DDLManagerFactory.getDDLManager(con);
            String tbName = ClassUtil.getTableName(entity).toUpperCase();
            //List tbs = ddlM.getTables();
            
            List<FieldInfo> fields = ClassUtil.getFields(entity);
            ArrayList aF = new ArrayList();
            String id = "";
            for (FieldInfo fi : fields) {
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
                for (FieldInfo fi : fields) {
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
                        for (int i=0;i<aann.length;i++) {
                            Annotation ann = aann[i];
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
                        //System.out.println(isLob);
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

    public List<String> getEntityUpdateDdl(Class entity, DataSource ds) throws EntityException, Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public List<String> getViewCreateDdl(Class view) throws EntityException, Exception {
        return null;
        //
    }

    public List<String> getViewUpdateDdl(Class view, DataSource ds) throws EntityException, Exception {
        return null;
    }
    
}
