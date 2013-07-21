/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.easyea.edao.builders;

import com.easyea.edao.Builder;
import com.easyea.edao.annotation.Column;
import com.easyea.edao.annotation.GeneratedValue;
import com.easyea.edao.annotation.GenerationType;
import com.easyea.edao.annotation.Id;
import com.easyea.edao.annotation.Temporal;
import com.easyea.edao.annotation.TemporalType;
import com.easyea.edao.exception.EntityException;
import com.easyea.edao.exception.ViewException;
import com.easyea.edao.util.ClassUtil;
import com.easyea.edao.util.FieldInfo;
import com.easyea.edao.util.MethodInfo;
import com.easyea.internal.CodeBuilder;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author louis
 */
public abstract class AbstractBuilder implements Builder {
    
    private ArrayList<String> entityImps;
    
    public GenerationType getGenerationType() {
        return GenerationType.SEQUENCE;
    }
    /**
     * 获取数据库类型的名称
     * @return 
     */
    @Override
    public abstract String getDbTypeName();
    
    public GenerationType getGenerationType(Class cls) {
        List<FieldInfo>  fields  = ClassUtil.getFields(cls);
        GenerationType gt = null;
        if (fields == null || fields.isEmpty()) {
            return null;
        }
        for (FieldInfo finfo : fields) {
            if (isId(finfo)) {
                Annotation[] aan = finfo.getAnnotations();
                if (aan != null) {
                    for (int i=0;i<aan.length;i++) {
                        if (aan[i] instanceof GeneratedValue) {
                            gt = ((GeneratedValue)aan[i]).strategy();
                        }
                    }
                }
            }
        }
        return gt;
    }

    public AbstractBuilder() {
        entityImps = new ArrayList<String>();
        entityImps.add("com.easyea.edao.EntityDao");
        entityImps.add("com.easyea.edao.ViewDao");
        entityImps.add("com.easyea.logger.*");
        entityImps.add("java.sql.Connection");
        entityImps.add("java.sql.SQLException");
        entityImps.add("java.util.List");
        entityImps.add("java.util.ArrayList");
        entityImps.add("com.easyea.edao.QueryParam");
        entityImps.add("java.sql.PreparedStatement");
        entityImps.add("java.sql.Statement");
        entityImps.add("java.sql.ResultSet");
        entityImps.add("com.easyea.edao.annotation.TemporalType");
        entityImps.add("java.util.Date");
        entityImps.add("java.sql.Timestamp");
        entityImps.add("java.util.Calendar");
        entityImps.add("java.sql.Time");
        entityImps.add("java.sql.ResultSetMetaData");
        
    }
    /**
     * 返回n个换行的字符
     * @param count 返回换行的个数
     * @return 
     */
    public static String r(int count) {
        StringBuilder sb = new StringBuilder();
        for (int i=0;i<count;i++) {
            sb.append("\n");
        }
        return sb.toString();
    }
    /**
     * 返回n个索引的字符，默认采用"\t"tab键来实现
     * @param count 缩进的个数
     * @return 
     */
    public static String t(int count) {
        StringBuilder sb = new StringBuilder();
        for (int i=0;i<count;i++) {
            sb.append("    ");
        }
        return sb.toString();
    }
    
    @Override
    public String getMapDaoCode() throws Exception {
        CodeBuilder code = new CodeBuilder();
        code.a("package com.easyea.mapdao;").r(2);
        code.a("import com.easyea.edao.annotation.TemporalType;").r(1);
        code.a("import com.easyea.edao.MapDao;").r(1);
        code.a("import com.easyea.edao.QueryParam;").r(1);
        code.a("import com.easyea.logger.*;").r(1);
        code.a("import java.sql.Connection;").r(1);
        code.a("import java.sql.PreparedStatement;").r(1);
        code.a("import java.sql.ResultSet;").r(1);
        code.a("import java.sql.ResultSetMetaData;").r(1);
        code.a("import java.sql.SQLException;").r(1);
        code.a("import java.sql.Timestamp;").r(1);
        code.a("import java.sql.Time;").r(1);
        code.a("import java.util.ArrayList;").r(1);
        code.a("import java.util.Calendar;").r(1);
        code.a("import java.util.Date;").r(1);
        code.a("import java.util.HashMap;").r(1);
        code.a("import java.util.List;").r(1);
        code.a("import java.util.Map;").r(1);
        code.r(1);
        code.a("public class ").a(this.getDbTypeName()).a("MapDao implements MapDao {").r(2);
        code.t(1).a("Logger logger = LoggerFactory.getLogger(this.getClass());").r(1);
        code.t(1).a("private Connection con;").r(1);
        code.a(this.getSetConnection(null));
        code.a(this.getGetConnection(null));
        code.a(this.getMapList1());
        code.a(this.getMapList2());
        code.a(this.getMapList());
        code.a(this.getMapMap1());
        code.a(this.getMapMap());
        code.a("}");
        return code.toString();
    }
    
    public String getMapMap1() throws Exception {
        CodeBuilder c = new CodeBuilder();
        c.r(1).t(1).a("@Override").r(1);
        c.t(1).a("public Map<String, Object> getMap(String sql) throws SQLException, Exception {").r(1);
        c.t(2).a("return getMap(sql, null);").r(1);
        c.t(1).a("}").r(1);
        return c.toString();
    }
    
    public String getMapMap() throws Exception {
        CodeBuilder c = new CodeBuilder();
        c.r(1).t(1).a("@Override").r(1);
        c.t(1).a("public Map<String, Object> getMap(String sql, ArrayList<QueryParam> params) ").r(1);
        c.t(3).a("throws SQLException, Exception {").r(1);
        c.t(2).a("HashMap<String, Object> map = null;").r(1);
        c.t(2).a("PreparedStatement pstmt = null;").r(1);
        c.t(2).a("try {").r(1);
        c.t(3).a("pstmt = con.prepareStatement(sql.toString());").r(1);
        c.t(3).a("if (params != null && params.size() > 0) {").r(1);
        c.t(4).a("for (QueryParam param : params) {").r(1);
        c.t(5).a("if (param.getValue() instanceof Date) {").r(1);
        c.t(6).a("if (TemporalType.TIMESTAMP.equals(param.getTemporalType())) {").r(1);
        c.t(7).a("pstmt.setTimestamp((Integer)param.getPosition(), new Timestamp(((Date)param.getValue()).getTime()));").r(1);
        c.t(6).a("} else if (TemporalType.TIME.equals(param.getTemporalType())) {").r(1);
        c.t(7).a("pstmt.setTime((Integer)param.getPosition(), new Time(((Date)param.getValue()).getTime()));").r(1);
        c.t(6).a("} else if (TemporalType.DATE.equals(param.getTemporalType())) {").r(1);
        c.t(7).a("pstmt.setDate((Integer)param.getPosition(), new java.sql.Date(((Date)param.getValue()).getTime()));").r(1);
        c.t(6).a("}").r(1);
        c.t(5).a("} else if (param.getValue() instanceof Calendar) {").r(1);
        c.t(6).a("if (TemporalType.TIMESTAMP.equals(param.getTemporalType())) {").r(1);
        c.t(7).a("pstmt.setTimestamp((Integer)param.getPosition(), new Timestamp(((Calendar)param.getValue()).getTimeInMillis()), (Calendar)param.getValue());").r(1);
        c.t(6).a("} else if (TemporalType.TIME.equals(param.getTemporalType())) {").r(1);
        c.t(7).a("pstmt.setTime((Integer)param.getPosition(), new Time(((Calendar)param.getValue()).getTimeInMillis()), (Calendar)param.getValue());").r(1);
        c.t(6).a("} else if (TemporalType.DATE.equals(param.getTemporalType())) {").r(1);
        c.t(7).a("pstmt.setDate((Integer)param.getPosition(), new java.sql.Date(((Calendar)param.getValue()).getTimeInMillis()), (Calendar)param.getValue());").r(1);
        c.t(6).a("}").r(1);
        c.t(5).a("} else if (param.getValue() instanceof String) {").r(1);
        c.t(6).a("pstmt.setString((Integer)param.getPosition(), (String)param.getValue());").r(1);
        c.t(5).a("} else if (param.getValue() instanceof Integer) {").r(1);
        c.t(6).a("pstmt.setInt((Integer)param.getPosition(), (Integer)param.getValue());").r(1);
        c.t(5).a("} else if (param.getValue() instanceof Long) {").r(1);
        c.t(6).a("pstmt.setLong((Integer)param.getPosition(), (Long)param.getValue());").r(1);
        c.t(5).a("} else if (param.getValue() instanceof Boolean) {").r(1);
        c.t(6).a("pstmt.setBoolean((Integer)param.getPosition(), (Boolean)param.getValue());").r(1);
        c.t(5).a("} else if (param.getValue() instanceof Double) {").r(1);
        c.t(6).a("pstmt.setDouble((Integer)param.getPosition(), (Double)param.getValue());").r(1);
        c.t(5).a("} else {").r(1);
        c.t(6).a("pstmt.setObject((Integer)param.getPosition(), param.getValue());").r(1);
        c.t(5).a("}").r(1);
        c.t(4).a("}").r(1);
        c.t(3).a("}").r(1);
        c.t(3).a("ResultSet rs = pstmt.executeQuery();").r(1);
        c.t(3).a("ResultSetMetaData md = rs.getMetaData();").r(1);
        c.t(3).a("int cs = md.getColumnCount();").r(1);
        c.t(3).a("if (rs.next()) {").r(1);
        c.t(4).a("map = new HashMap<String, Object>();").r(1);
        c.t(4).a("for (int i=1;i<=cs;i++) {").r(1);
        c.t(5).a("map.put(md.getColumnLabel(i), rs.getObject(i));").r(1);
        c.t(4).a("}").r(1);
        c.t(3).a("}").r(1);
        c.t(2).a("} catch (SQLException sqle) {").r(1);
        c.t(3).a("throw sqle;").r(1);
        c.t(2).a("} finally {").r(1);
        c.t(3).a("if (pstmt != null) {").r(1);
        c.t(4).a("try {pstmt.close();} catch (Exception e) {}").r(1);
        c.t(3).a("}").r(1);
        c.t(2).a("}").r(1);
        c.t(2).a("return map;").r(1);
        c.t(1).a("}").r(1);
        return c.toString();
    }
    
    public String getMapList1() throws Exception {
        CodeBuilder c = new CodeBuilder();
        c.r(1).t(1).a("@Override").r(1);
        c.t(1).a("public List<Map<String, Object>> getList(String sql) ").r(1);
        c.t(3).a("throws SQLException, Exception {").r(1);
        c.t(2).a("return this.getList(sql, null, 0, -1);").r(1);
        c.t(1).a("}").r(1);
        return c.toString();
    }
    
    public String getMapList2() throws Exception {
        CodeBuilder c = new CodeBuilder();
        c.r(1).t(1).a("@Override").r(1);
        c.t(1).a("public List<Map<String, Object>> getList(String sql, ").r(1);
        c.t(3).a("ArrayList<QueryParam> params) throws SQLException, Exception {").r(1);
        c.t(2).a("return this.getList(sql, params, 0, -1);").r(1);
        c.t(1).a("}").r(1);
        return c.toString();
    }
    
    public String getMapList() throws Exception {
        CodeBuilder c = new CodeBuilder();
        c.r(1);
        c.r(1).t(1).a("@Override").r(1);
        c.t(1).a("public List<Map<String, Object>> getList(String qlString, ").r(1);
        c.t(3).a("ArrayList<QueryParam> params, long start, int counts) ").r(1);
        c.t(3).a("throws SQLException, Exception {").r(1);
        c.t(2).a("List<Map<String, Object>> l = new ArrayList<Map<String, Object>>();").r(1);
        c.t(2).a("PreparedStatement pstmt = null;").r(1);
        c.t(2).a("StringBuilder sql = new StringBuilder(qlString);").r(1);
        c.t(2).a("if (counts > 0 && start >= 0) {").r(1);
        c.t(3).a(this.getLimitSql("sql", "start", "counts")).r(1);
        c.t(2).a("}").r(1);
        c.t(2).a("try {").r(1);
        c.t(3).a("pstmt = con.prepareStatement(sql.toString());").r(1);
        c.t(3).a("if (params != null && params.size() > 0) {").r(1);
        c.t(4).a("for (QueryParam param : params) {").r(1);
        c.t(5).a("if (param.getValue() instanceof Date) {").r(1);
        c.t(6).a("if (TemporalType.TIMESTAMP.equals(param.getTemporalType())) {").r(1);
        c.t(7).a("pstmt.setTimestamp((Integer)param.getPosition(), new Timestamp(((Date)param.getValue()).getTime()));").r(1);
        c.t(6).a("} else if (TemporalType.TIME.equals(param.getTemporalType())) {").r(1);
        c.t(7).a("pstmt.setTime((Integer)param.getPosition(), new Time(((Date)param.getValue()).getTime()));").r(1);
        c.t(6).a("} else if (TemporalType.DATE.equals(param.getTemporalType())) {").r(1);
        c.t(7).a("pstmt.setDate((Integer)param.getPosition(), new java.sql.Date(((Date)param.getValue()).getTime()));").r(1);
        c.t(6).a("}").r(1);
        c.t(5).a("} else if (param.getValue() instanceof Calendar) {").r(1);
        c.t(6).a("if (TemporalType.TIMESTAMP.equals(param.getTemporalType())) {").r(1);
        c.t(7).a("pstmt.setTimestamp((Integer)param.getPosition(), new Timestamp(((Calendar)param.getValue()).getTimeInMillis()), (Calendar)param.getValue());").r(1);
        c.t(6).a("} else if (TemporalType.TIME.equals(param.getTemporalType())) {").r(1);
        c.t(7).a("pstmt.setTime((Integer)param.getPosition(), new Time(((Calendar)param.getValue()).getTimeInMillis()), (Calendar)param.getValue());").r(1);
        c.t(6).a("} else if (TemporalType.DATE.equals(param.getTemporalType())) {").r(1);
        c.t(7).a("pstmt.setDate((Integer)param.getPosition(), new java.sql.Date(((Calendar)param.getValue()).getTimeInMillis()), (Calendar)param.getValue());").r(1);
        c.t(6).a("}").r(1);
        c.t(5).a("} else if (param.getValue() instanceof String) {").r(1);
        c.t(6).a("pstmt.setString((Integer)param.getPosition(), (String)param.getValue());").r(1);
        c.t(5).a("} else if (param.getValue() instanceof Integer) {").r(1);
        c.t(6).a("pstmt.setInt((Integer)param.getPosition(), (Integer)param.getValue());").r(1);
        c.t(5).a("} else if (param.getValue() instanceof Long) {").r(1);
        c.t(6).a("pstmt.setLong((Integer)param.getPosition(), (Long)param.getValue());").r(1);
        c.t(5).a("} else if (param.getValue() instanceof Boolean) {").r(1);
        c.t(6).a("pstmt.setBoolean((Integer)param.getPosition(), (Boolean)param.getValue());").r(1);
        c.t(5).a("} else if (param.getValue() instanceof Double) {").r(1);
        c.t(6).a("pstmt.setDouble((Integer)param.getPosition(), (Double)param.getValue());").r(1);
        c.t(5).a("} else {").r(1);
        c.t(6).a("pstmt.setObject((Integer)param.getPosition(), param.getValue());").r(1);
        c.t(5).a("}").r(1);
        c.t(4).a("}").r(1);
        c.t(3).a("}").r(1);
        c.t(3).a("ResultSet rs = pstmt.executeQuery();").r(1);
        c.t(3).a("ResultSetMetaData md = rs.getMetaData();").r(1);
        c.t(3).a("int cs = md.getColumnCount();").r(1);
        c.t(3).a("while (rs.next()) {").r(1);
        c.t(4).a("HashMap<String, Object> map = new HashMap<String, Object>();").r(1);
        c.t(4).a("for (int i=1;i<=cs;i++) {").r(1);
        c.t(5).a("map.put(md.getColumnLabel(i), rs.getObject(i));").r(1);
        c.t(4).a("}").r(1);
        c.t(4).a("l.add(map);").r(1);
        c.t(3).a("}").r(1);
        c.t(2).a("} catch (SQLException sqle) {").r(1);
        c.t(3).a("throw sqle;").r(1);
        c.t(2).a("} finally {").r(1);
        c.t(3).a("if (pstmt != null) {").r(1);
        c.t(4).a("try {pstmt.close();} catch (Exception e) {}").r(1);
        c.t(3).a("}").r(1);
        c.t(2).a("}").r(1);
        c.t(2).a("return l;").r(1);
        c.t(1).a("}").r(1);
        return c.toString();
    }

    @Override
    public String getDaoCode(Class ecls) throws EntityException {
        StringBuilder sb = new StringBuilder();
        checkEntity(ecls);
        //生成包名的代码
        sb.append("package ").append(this.getEntityDaoPackage(ecls))
                .append(";").append(r(2));
        if (entityImps == null) {
            entityImps = new ArrayList<String>();
        }
        entityImps.add(ecls.getName());
        if (entityImps != null && !entityImps.isEmpty()) {
            for (String imp : entityImps) {
                sb.append("import ").append(imp).append(";").append(r(1));
            }
        }
        sb.append("import com.easyea.edao.DdlManager;").append(r(1));
        sb.append(r(1));
        sb.append("public class ").append(ecls.getSimpleName())
                .append("Dao implements EntityDao {").append(r(2));
        
        sb.append(t(1)).append("Logger logger = ")
                .append("LoggerFactory.getLogger(this.getClass());").append(r(1));
        sb.append(t(1)).append("private Connection con;").append(r(1));
        sb.append(t(1)).append("private DdlManager ddlManager = null;").append(r(1));
        
        sb.append(r(1));
        sb.append(this.getSetDdlManager(ecls));
        sb.append(this.getGetDdlManager(ecls));
        sb.append(this.getSetConnection(ecls));
        sb.append(this.getGetConnection(ecls));
        sb.append(this.getPersist(ecls));
        sb.append(this.getPersists(ecls));
        sb.append(this.getGetObjectById(ecls));
        sb.append(this.getList1(ecls));
        sb.append(this.getList2(ecls));
        sb.append(this.getList3(ecls));
        sb.append(this.getList4(ecls));
        sb.append(this.getListCode(ecls));
        sb.append(this.getMerge(ecls));
        sb.append(this.getTotal1(ecls));
        sb.append(this.getTotal2(ecls));
        sb.append(this.getTotal3(ecls));
        sb.append(this.getUpdate1(ecls));
        sb.append(this.getUpdate2(ecls));
        sb.append(this.getRemove1(ecls));
        sb.append(this.getRemove2(ecls));
        sb.append(this.getRemove3(ecls));
        
        sb.append("}");
        return sb.toString();
    }
    
    /**
     * 添加引入的包名，如果Dao的实现需要引入新的包，则用该方法引入
     * @param packageName 
     */
    public void addImport(String packageName) {
        this.entityImps.add(packageName);
    }

    @Override
    public String getViewDaoCode(Class vcls) throws ViewException {
        StringBuilder sb = new StringBuilder();
        sb.append("package ").append(this.getViewDaoPackage(vcls))
                .append(";").append(r(2));
        if (entityImps == null) {
            entityImps = new ArrayList<String>();
        }
        entityImps.add(vcls.getName());
        if (entityImps != null && !entityImps.isEmpty()) {
            for (String imp : entityImps) {
                sb.append("import ").append(imp).append(";").append(r(1));
            }
        }
        sb.append(r(1));
        sb.append("public class ").append(vcls.getSimpleName())
                .append("Dao implements ViewDao {").append(r(2));
        
        sb.append(t(1)).append("Logger logger = ")
                .append("LoggerFactory.getLogger(this.getClass());").append(r(1));
        sb.append(t(1)).append("private Connection con;").append(r(1));
        
        sb.append(r(1));
        sb.append(this.getSetConnection(vcls));
        sb.append(this.getGetConnection(vcls));
        sb.append(this.getList1(vcls));
        sb.append(this.getList2(vcls));
        sb.append(this.getList3(vcls));
        sb.append(this.getList4(vcls));
        sb.append(this.getListCode(vcls));
        sb.append(this.getTotal1(vcls));
        sb.append(this.getTotal2(vcls));
        sb.append(this.getTotal3(vcls));
        sb.append("}");
        return sb.toString();
    }

    /**
     * 根据持久化Bean的class获取Dao的包名，如果class包名不符合规则则直接报错，包名要以entity
     * 或者entitybean结尾。
     * @param cls 持久化bean的class
     * @return 返回EntityDao实现类的包名
     */
    public String getEntityDaoPackage(Class cls) throws EntityException {
        StringBuilder sb = new StringBuilder();
        String pack = cls.getPackage().getName();
        if (pack == null) {
            pack = "";
        }
        String[] apackage = pack.split("\\.");
        if (pack.length() > 0 
                && ("entity".equals(apackage[apackage.length-1]) 
                    || "entitybean".equals(apackage[apackage.length-1]))) {
            if (apackage.length > 1) {
                for (int i = 0; i < apackage.length - 1; i++) {
                    if (sb.length() > 0) {
                        sb.append(".");
                    }
                    sb.append(apackage[i]);
                }
            }
        } else {
            throw new EntityException("package name not well rule!" 
                    + " package must end with \"entity\" or \"entitybean\"");
        }
        sb.append(".dao");
        return sb.toString();
    }
    
    /**
     * 根据视图的Javabean类，判断是否符合包名的规范如果不符合规范则报错。如果符合规范，则返回
     * ViewDao实现类包名
     * @param cls
     * @return
     * @throws ViewException 
     */
    public String getViewDaoPackage(Class cls) throws ViewException {
        StringBuilder sb = new StringBuilder();
        String pack = cls.getPackage().getName();
        if (pack == null) {
            pack = "";
        }
        String[] apackage = pack.split("\\.");
        if (pack.length() > 0 && "view".equals(apackage[apackage.length-1])) {
            if (apackage.length > 1) {
                for (int i = 0; i < apackage.length - 1; i++) {
                    if (sb.length() > 0) {
                        sb.append(".");
                    }
                    sb.append(apackage[i]);
                }
            }
        } else {
            throw new ViewException("package name not well rule!" 
                    + " package must end with \"view\"");
        }
        sb.append(".viewdao");
        return sb.toString();
    }

    /**
     * 生成实现 public Connection getConnect(); 方法的代码
     *
     * @param cls
     * @return
     */
    public String getGetConnection(Class cls) {
        StringBuilder sb = new StringBuilder();
        sb.append(r(1));
        sb.append(t(1)).append("@Override").append(r(1));
        sb.append(t(1)).append("public Connection getConnect() {").append(r(1));
        sb.append(t(2)).append("return this.con;").append(r(1));
        sb.append(t(1)).append("}").append(r(1));
        return sb.toString();
    }
    
    public String getGetDdlManager(Class cls) {
        StringBuilder sb = new StringBuilder();
        sb.append(r(1));
        sb.append(t(1)).append("@Override").append(r(1));
        sb.append(t(1)).append("public DdlManager getDdlManager() {").append(r(1));
        sb.append(t(2)).append("return this.ddlManager;").append(r(1));
        sb.append(t(1)).append("}").append(r(1));
        return sb.toString();
    }

    /**
     * 生成实现 public void setConnect(Connection con); 方法的代码
     *
     * @param cls
     * @return
     */
    public String getSetConnection(Class cls) {
        StringBuilder sb = new StringBuilder();
        sb.append(r(1));
        sb.append(t(1)).append("@Override").append(r(1));
        sb.append(t(1)).append("public void setConnect(Connection con) {").append(r(1));
        sb.append(t(2)).append("this.con = con;").append(r(1));
        sb.append(t(2)).append("logger.debug(\"ddlManager=[{}]\", ddlManager);").append(r(1));
        sb.append(t(2)).append("if (ddlManager != null) {").append(r(1));
        sb.append(t(2)).append("logger.debug(\"ddlManager.isSync()=[{}]\", ddlManager.isSync());").append(r(1));
        sb.append(t(2)).append("}").append(r(1));
        sb.append(t(2)).append("if (ddlManager != null && !ddlManager.isSync()) {").append(r(1));
        sb.append(t(3)).append("try {").append(r(1));
        sb.append(t(4)).append("ddlManager.syncDdl(con);").append(r(1));
        sb.append(t(3)).append("} catch (Exception e) {").append(r(1));
        sb.append(t(4)).append("logger.error(\"{}\", e);").append(r(1));
        sb.append(t(3)).append("}").append(r(1));
        sb.append(t(2)).append("}").append(r(1));
        sb.append(t(1)).append("}").append(r(1));
        return sb.toString();
    }
    
    public String getSetDdlManager(Class cls) {
        StringBuilder sb = new StringBuilder();
        sb.append(r(1));
        sb.append(t(1)).append("@Override").append(r(1));
        sb.append(t(1)).append("public void setDdlManager(DdlManager ddlManager) {").append(r(1));
        sb.append(t(2)).append("this.ddlManager = ddlManager;").append(r(1));
        sb.append(t(1)).append("}").append(r(1));
        return sb.toString();
    }

    /**
     * 生成实现 public <T> T getEntityById(Object primaryKey) throws SQLException,
     * Exception; 方法的代码
     *
     * @param cls
     * @return
     */
    public String getGetObjectById(Class cls) {
        StringBuilder sb = new StringBuilder();
        sb.append(r(1));
        sb.append(t(1)).append("@Override").append(r(1));
        sb.append(t(1)).append("public <T> T getEntityById(Object primaryKey)")
                .append(" throws SQLException, Exception {").append(r(1));
        sb.append(t(2)).append(cls.getSimpleName()).append(" t = null;").append(r(1));
        sb.append(t(2)).append("List l = this.getList(\" where ")
                .append(" id=").append("\"").append(" + (Long)primaryKey")
                .append(");").append(r(1));
        sb.append(t(2)).append("if (l != null && l.size() > 0) {").append(r(1));
        sb.append(t(3)).append("t = (").append(cls.getSimpleName())
                .append(")l.get(0);").append(r(1));
        sb.append(t(2));
        sb.append("}").append(r(1));
        sb.append(t(2)).append("return (T)t;").append(r(1));
        sb.append(t(1)).append("}").append(r(1));
        return sb.toString();
    }

    /**
     * 实现public List getList(String qlString, ArrayList<QueryParam> params, int
     * start, int counts) throws SQLException, Exception; 的代码
     *
     * @param cls
     * @return
     */
    public String getListCode(Class cls) {
        StringBuilder sb = new StringBuilder();
        String tbName = getTableName(cls);
        sb.append(r(1));
        sb.append(t(1)).append("@Override").append(r(1));
        sb.append(t(1)).append("public List getList(String qlString, ArrayList<QueryParam> params,").append(r(1));
        sb.append(t(3)).append("long start, int counts) throws SQLException, Exception {").append(r(1));
        sb.append(t(2)).append("boolean isAll = false;").append(r(1));
        sb.append(t(2)).append("List<").append(cls.getSimpleName()).append("> l = null;").append(r(1));
   
        //执行数据查询获取对象
        sb.append(t(2)).append("StringBuilder sql = new StringBuilder();").append(r(1));
        sb.append(t(2));
        sb.append("if (qlString.trim().toLowerCase().startsWith(\"where\") || ")
                .append(" qlString.trim().length() == 0) {").append(r(1));
        sb.append(t(3)).append("sql.append(\"SELECT * FROM ")
                .append(tbName).append(" \");").append(r(1));
        sb.append(t(3)).append("isAll = true;").append(r(1));
        sb.append(t(2)).append("}").append(r(1));
        sb.append(t(2)).append("sql.append(qlString);").append(r(1));
        sb.append(t(2)).append("String ssql = \"\";").append(r(1));
        sb.append(t(2)).append("if (counts > 0) {").append(r(1));
        sb.append(t(3)).append(this.getLimitSql("sql", "start", "counts")).append(r(1));
        sb.append(t(2)).append("}").append(r(1));
        sb.append(t(2)).append("ssql = ").append("sql.toString();").append(r(1));
        sb.append(t(2)).append("PreparedStatement pstmt = null;").append(r(1));
        sb.append(t(2)).append("if (logger.isDebugEnabled()) {").append(r(1));
        sb.append(t(3)).append("logger.debug(\"sql=[{}]\", sql.toString());").append(r(1));
        sb.append(t(2)).append("}").append(r(1));
        sb.append(t(2)).append("try {").append(r(1));
        sb.append(t(3)).append("pstmt = con.prepareStatement(sql.toString());").append(r(1));
        sb.append(t(3)).append("if (params != null && params.size() > 0) {").append(r(1));
        sb.append(t(4)).append("for (QueryParam param : params) {").append(r(1));
        sb.append(t(5)).append("if (param.getValue() instanceof Date) {").append(r(1));
        sb.append(t(6)).append("if (TemporalType.TIMESTAMP.equals(param.getTemporalType())) {").append(r(1));
        sb.append(t(7));
        sb.append("pstmt.setTimestamp((Integer)param.getPosition(), new Timestamp(((Date)param.getValue()).getTime()));").append(r(1));
        sb.append(t(6)).append("} else if (TemporalType.TIME.equals(param.getTemporalType())) {").append(r(1));
        sb.append(t(7));
        sb.append("pstmt.setTime((Integer)param.getPosition(), new Time(((Date)param.getValue()).getTime()));").append(r(1));
        sb.append(t(6)).append("} else if (TemporalType.DATE.equals(param.getTemporalType())) {").append(r(1));
        sb.append(t(7)).append("pstmt.setDate((Integer)param.getPosition(), new java.sql.Date(((Date)param.getValue()).getTime()));").append(r(1));
        sb.append(t(6)).append("}").append(r(1));
        sb.append(t(5)).append("} else if (param.getValue() instanceof Calendar) {").append(r(1));
        sb.append(t(6)).append("if (TemporalType.TIMESTAMP.equals(param.getTemporalType())) {").append(r(1));
        sb.append(t(7)).append("pstmt.setTimestamp((Integer)param.getPosition(), new Timestamp(((Calendar)param.getValue()).getTimeInMillis()), (Calendar)param.getValue());").append(r(1));
        sb.append(t(6)).append("} else if (TemporalType.TIME.equals(param.getTemporalType())) {").append(r(1));
        sb.append(t(7)).append("pstmt.setTime((Integer)param.getPosition(), new Time(((Calendar)param.getValue()).getTimeInMillis()), (Calendar)param.getValue());").append(r(1));
        sb.append(t(6)).append("} else if (TemporalType.DATE.equals(param.getTemporalType())) {").append(r(1));
        sb.append(t(7)).append("pstmt.setDate((Integer)param.getPosition(), new java.sql.Date(((Calendar)param.getValue()).getTimeInMillis()), (Calendar)param.getValue());").append(r(1));
        sb.append(t(6)).append("}").append(r(1));
        sb.append(t(5)).append("} else if (param.getValue() instanceof String) {").append(r(1));
        sb.append(t(6)).append("pstmt.setString((Integer)param.getPosition(), (String)param.getValue());").append(r(1));
        sb.append(t(5)).append("} else if (param.getValue() instanceof Integer) {").append(r(1));
        sb.append(t(6)).append("pstmt.setInt((Integer)param.getPosition(), (Integer)param.getValue());").append(r(1));
        sb.append(t(5)).append("} else if (param.getValue() instanceof Long) {").append(r(1));
        sb.append(t(6)).append("pstmt.setLong((Integer)param.getPosition(), (Long)param.getValue());").append(r(1));
        sb.append(t(5)).append("} else if (param.getValue() instanceof Boolean) {").append(r(1));
        sb.append(t(6)).append("pstmt.setBoolean((Integer)param.getPosition(), (Boolean)param.getValue());").append(r(1));
        sb.append(t(5)).append("} else if (param.getValue() instanceof Double) {").append(r(1));
        sb.append(t(6)).append("pstmt.setDouble((Integer)param.getPosition(), (Double)param.getValue());").append(r(1));
        sb.append(t(5)).append("} else {").append(r(1));
        sb.append(t(6)).append("pstmt.setObject((Integer)param.getPosition(), param.getValue());").append(r(1));
        sb.append(t(5)).append("}").append(r(1));
        sb.append(t(4)).append("}").append(r(1));
        sb.append(t(3)).append("}").append(r(1));
        
        sb.append(t(3)).append("ResultSet rs = pstmt.executeQuery();").append(r(1));
        sb.append(t(3)).append("l = new ArrayList<").append(cls.getSimpleName()).append(">();").append(r(1));
        sb.append(t(3)).append("ArrayList<String> aColumn = new ArrayList<String>();").append(r(1));
        sb.append(t(3)).append("if (!isAll) {").append(r(1));
        sb.append(t(4)).append("ResultSetMetaData rsmd = rs.getMetaData();").append(r(1));
        
        sb.append(t(4)).append("int colSize = rsmd.getColumnCount();").append(r(1));
        sb.append(t(4)).append("for (int i=1;i<=colSize;i++) {").append(r(1));
        sb.append(t(5)).append("aColumn.add(rsmd.getColumnLabel(i).toLowerCase());").append(r(1));
        sb.append(t(4)).append("}").append(r(1));
        sb.append(t(3)).append("}").append(r(1));
        sb.append(t(3)).append("while (rs.next()) {").append(r(1));
        sb.append(t(4)).append(cls.getSimpleName()).append(" t = new ")
                .append(cls.getSimpleName()).append("();").append(r(1));
        List<FieldInfo>  fields  = ClassUtil.getFields(cls);
        
        for (FieldInfo finfo : fields) {
            String field = finfo.getName();
            Object otype = finfo.getType();
            sb.append(t(4)).append("if (isAll || aColumn.contains(\"")
                    .append(this.getDataField(finfo).toLowerCase()).append("\")) {").append(r(1));
            sb.append(t(5)).append("t.set").append(field.substring(0,1).toUpperCase())
                    .append(field.substring(1))
                    .append("(");
            if (otype.equals(Long.class) 
                    || otype.toString().equals("long")) {
                sb.append("rs.getLong(\"");
            } else if (otype.equals(Integer.class) 
                    || otype.toString().equals("int")) {
                sb.append("rs.getInt(\"");
            } else if (otype.equals(String.class)) {
                sb.append("rs.getString(\"");
            } else if (otype.equals(Date.class)) {
                Annotation[] anns = finfo.getAnnotations();
                Temporal temporalANN = null;
                if (anns != null) {
                    for  (int j=0;j<anns.length;j++) {
                        Annotation ann = anns[j];
                        if (ann instanceof Temporal) {
                            temporalANN = (Temporal)ann;
                        }
                    }
                }
                if (temporalANN != null) {
                    Temporal datetype = (Temporal)temporalANN;
                    if (datetype.value().equals(TemporalType.TIMESTAMP)) {
                        sb.append("rs.getTimestamp(\"");
                    } else if (datetype.value().equals(TemporalType.DATE)) {
                         sb.append("rs.getDate(\"");
                    } else if (datetype.value().equals(TemporalType.TIME)) {
                         sb.append("rs.getTime(\"");
                    } else {
                        sb.append("rs.getTimestamp(\"");
                    }
                } else {
                    sb.append("rs.getTimestamp(\"");
                }
            } else if (otype.equals(Boolean.class)) {
                sb.append("rs.getBoolean(\"");
            } else if (otype.equals(Float.class) || otype.toString().equals("float")) {
                sb.append("rs.getFloat(\"");
            } else if (otype.equals(Double.class) || otype.toString().equals("double")) {
                sb.append("rs.getDouble(\"");
            } else {
                sb.append("rs.getObject(\"");
            }
            sb.append(this.getDataField(finfo))
                    .append("\"));").append(r(1));
            sb.append(t(4)).append("}").append(r(1));
        }
        sb.append(t(4)).append("l.add(t);").append(r(1));
        sb.append(t(3)).append("}").append(r(1));
        
        sb.append(t(2)).append("} catch (SQLException ex) {").append(r(1));
        sb.append(t(3)).append("logger.error(\"{}\",").append("ex").append(");").append(r(1));
        sb.append(t(3)).append("throw ex;").append(r(1));
        sb.append(t(2)).append("} finally {").append(r(1));
        sb.append(t(3)).append("pstmt.close();").append(r(1));
        sb.append(t(2)).append("}").append(r(1));
        sb.append(t(2)).append("return l;").append(r(1));
        sb.append(t(1)).append("}").append(r(1));
        return sb.toString();
    }

    /**
     * 根据SQL语句返回获取一定数目记录的SQL语句如Postgresql的“limit count offset start”等。
     * 第一个参数为java代码中StingBuilder类型的SQL字符串，生成对该字符串进行各类操作的java代码即可。
     * Postgreql的实现如下
     * <pre>
     * "sql.append(\" limit  \").append(" + count 
                + ").append(\" offset \").append(" + start + ");"
     * </pre>
     * @param sql java代码中表示查询SQL的StringBuider的变量名
     * @param start 开始取数据的位置
     * @param count 取数据的条数
     * @return 返回对SQL进行运算的Java代码的字符创
     */
    public abstract String getLimitSql(String sql, String start, String count);

    /**
     * 生成实现 public <T> T merge(T entity) throws SQLException, Exception; 方法的代码
     *
     * @param cls
     * @return
     */
    public String getMerge(Class cls) {
        StringBuilder sb = new StringBuilder();
        String tbName = getTableName(cls);
        sb.append(r(1));
        sb.append(t(1)).append("@Override").append(r(1));
        sb.append(t(1)).append("public <T> T merge(T ent) throws SQLException, Exception {").append(r(1));
        sb.append(t(2)).append(cls.getSimpleName()).append(" entity = (")
                .append(cls.getSimpleName())
                .append(")ent;").append(r(1));
        sb.append(t(2)).append("StringBuilder sql = new StringBuilder();")
                .append(r(1));
        sb.append(t(2)).append("sql.append(\"UPDATE ").append(tbName).append(" SET \");")
                .append(r(1));
        List<FieldInfo> fields = ClassUtil.getFields(cls);
        int i = 1;
        FieldInfo idInfo = null;
        StringBuilder sqlfields = new StringBuilder();
        for (FieldInfo finfo : fields) {
            if (!isId(finfo)) {
                if (sqlfields.length() > 0) {
                    sqlfields.append(",").append(this.getDataField(finfo)).append("=?");
                } else {
                    sqlfields.append(this.getDataField(finfo)).append("=?");
                }
                if (i != 0 && i%4 == 0) {
                    sqlfields.append("\"").append(r(1));
                    sqlfields.append(t(3))
                            .append("+ \"");
                }
                i++;
            } else {
                idInfo = finfo;
            }
        }
        sb.append(t(2)).append("sql.append(\"").append(sqlfields).append("\");").append(r(1));
        sb.append(t(2)).append("sql.append(\" WHERE ").append(this.getDataField(idInfo))
                .append("=?\");").append(r(1));
        sb.append(t(2)).append("PreparedStatement pstmt = null;").append(r(1));
        sb.append(t(2)).append("try {").append(r(1));
        sb.append(t(3)).append("pstmt = con.prepareStatement(sql.toString());").append(r(1));
        i = 1;
        for (FieldInfo finfo : fields) {
            boolean isId = false;
            int position = 1;
            if (!isId(finfo)) {
                isId = false;
                position = i;
            } else {
                position = fields.size();
                isId = true;
            }
            sb.append(t(3));
            Object otype = finfo.getType();
            String fieldn = finfo.getName();
            if (otype.equals(Long.class) 
                    || otype.toString().equals("long")) {
                sb.append("pstmt.setLong(")
                        .append(position).append(", entity.get")
                        .append(fieldn.substring(0, 1).toUpperCase())
                        .append(fieldn.substring(1))
                        .append("());").append(r(1));
            } else if (otype.equals(Integer.class) 
                    || otype.toString().equals("int")) {
                sb.append("pstmt.setInt(")
                        .append(position).append(", entity.get")
                        .append(fieldn.substring(0, 1).toUpperCase())
                        .append(fieldn.substring(1))
                        .append("());").append(r(1));
            } else if (otype.equals(String.class)) {
                sb.append("pstmt.setString(")
                        .append(position).append(", entity.get")
                        .append(fieldn.substring(0, 1).toUpperCase())
                        .append(fieldn.substring(1))
                        .append("());").append(r(1));
            } else if (otype.equals(Date.class)) {
                Annotation[] anns = finfo.getAnnotations();
                Temporal temporalANN = null;
                if (anns != null) {
                    for  (int j=0;j<anns.length;j++) {
                        Annotation ann = anns[j];
                        if (ann instanceof Temporal) {
                            temporalANN = (Temporal)ann;
                        }
                    }
                }
                if (temporalANN != null) {
                    Temporal datetype = (Temporal)temporalANN;
                    if (datetype.value().equals(TemporalType.TIMESTAMP)) {
                        sb.append("pstmt.setTimestamp(")
                                .append(position).append(", new Timestamp(entity.get")
                                .append(fieldn.substring(0, 1).toUpperCase())
                                .append(fieldn.substring(1))
                                .append("().getTime()));").append(r(1));
                    } else if (datetype.value().equals(TemporalType.DATE)) {
                        sb.append("pstmt.setDate(")
                                .append(position).append(", new java.sql.Date(entity.get")
                                .append(fieldn.substring(0, 1).toUpperCase())
                                .append(fieldn.substring(1))
                                .append("().getTime()));").append(r(1));
                    } else if (datetype.value().equals(TemporalType.TIME)) {
                        sb.append("pstmt.setTime(")
                                .append(position).append(", new java.sql.Time(entity.get")
                                .append(fieldn.substring(0, 1).toUpperCase())
                                .append(fieldn.substring(1))
                                .append("().getTime()));").append(r(1));
                    } else {
                        sb.append("pstmt.setTimestamp(")
                                .append(position).append(", new java.sql.Timesamp(entity.get")
                                .append(fieldn.substring(0, 1).toUpperCase())
                                .append(fieldn.substring(1))
                                .append("().getTime()));").append(r(1));
                    }
                } else {
                    sb.append("pstmt.setTimestamp(")
                            .append(position).append(", new java.sql.Timestamp(entity.get")
                            .append(fieldn.substring(0, 1).toUpperCase())
                            .append(fieldn.substring(1))
                            .append("().getTime()));").append(r(1));
                }
            } else if (otype.equals(Boolean.class)) {
                sb.append("pstmt.setBoolean(")
                        .append(position).append(", entity.get")
                        .append(fieldn.substring(0, 1).toUpperCase())
                        .append(fieldn.substring(1))
                        .append("());").append(r(1));
            } else if (otype.equals(Double.class)) {
                sb.append("pstmt.setDouble(")
                        .append(position).append(", entity.get")
                        .append(fieldn.substring(0, 1).toUpperCase())
                        .append(fieldn.substring(1))
                        .append("());").append(r(1));
            } else {
                sb.append("pstmt.setObject(")
                        .append(position).append(", entity.get")
                        .append(fieldn.substring(0, 1).toUpperCase())
                        .append(fieldn.substring(1))
                        .append("());").append(r(1));
            }
            if (!isId) {
                i++;
            }
        }
        sb.append(t(3)).append("pstmt.executeUpdate();").append(r(1));
        sb.append(t(2)).append("} catch (SQLException sqlex) {").append(r(1));
        sb.append(t(3)).append("logger.error(sqlex.getMessage());").append(r(1));
        sb.append(t(3)).append("throw sqlex;").append(r(1));
        sb.append(t(2)).append("} catch (Exception ex) {").append(r(1));
        sb.append(t(3)).append("logger.error(ex.getMessage());").append(r(1));
        sb.append(t(3)).append("throw ex;").append(r(1));
        sb.append(t(2)).append("} finally {").append(r(1));
        sb.append(t(3)).append("pstmt.close();").append(r(1));
        sb.append(t(2)).append("}").append(r(1));
        sb.append(t(2)).append("return (T)entity;").append(r(1));
        sb.append(t(1)).append("}").append(r(1));
        return sb.toString();
    }
    /**
     * 判断一个持久化Bean的属性是否为主键
     * @param finfo
     * @return 
     */
    public boolean isId(FieldInfo finfo) {
        if (finfo.getName().equals("id")) {
            return true;
        } else {
            Annotation[] anns = finfo.getAnnotations();
            if (anns != null) {
                for (int i=0;i<anns.length;i++) {
                    if (anns[i] instanceof Id) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    /**
     * 生成实现 public <T> T persist(T entity) throws SQLException, Exception; 方法的代码
     *
     * @param cls
     * @return
     */
    public String getPersist(Class cls) {
        GenerationType gt = getGenerationType(cls);
        if (gt == null) {
            gt = getGenerationType();
        }
        if (gt == null) {
            gt = GenerationType.SEQUENCE;
        }
        if (gt == GenerationType.IDENTITY) {
            return getIdentityPersist(cls);
        } else {
            return getSequencePersist(cls);
        }
    }
    
    public String getSequencePersist(Class cls) {
        StringBuilder sb = new StringBuilder();
        sb.append(t(1)).append("@Override").append(r(1));
        sb.append(t(1)).append("public <T> T persist(T ent) throws SQLException, Exception {").append(r(1));
        sb.append(t(2)).append(cls.getSimpleName()).append(" entity = (")
                .append(cls.getSimpleName()).append(")ent;").append(r(1));
        List<FieldInfo> fields = ClassUtil.getFields(cls);
        StringBuilder sqlfields = new StringBuilder();
        StringBuilder sqlvals   = new StringBuilder();
        int fieldCount = 0;
        FieldInfo idInfo = null;
        for (FieldInfo finfo : fields) {
            Annotation[] anns = finfo.getAnnotations();
            String fieldName = this.getDataField(finfo);
            if (this.isId(finfo)) {
                idInfo = finfo;
            }
            if (anns != null) {
                for  (int i=0;i<anns.length;i++) {
                    Annotation ann = anns[i];
                    if (ann instanceof Column) {
                        Column col = (Column)ann;
                        if (col.name() != null && col.name().length() > 0) {
                            fieldName = col.name();
                        }
                    }
                }
            }
            if (sqlfields.length() > 0) {
                sqlfields.append(",").append(fieldName);
                sqlvals.append(",?");
            } else {
                sqlfields.append(fieldName);
                sqlvals.append("?");
            }
            if (fieldCount != 0 && fieldCount%4 == 0) {
                sqlfields.append("\"").append(r(1));
                sqlfields.append(t(3)).append("+ \"");
            }
            //System.out.println(field);
            //System.out.println("\t" + (Object)types.get(field));
            fieldCount++;
        }

        String sql = "";
        String tbName = getTableName(cls);
        sb.append(t(2))
                .append("String sql = \"insert into ")
                .append(tbName).append(" (\"").append(r(1))
                .append(t(3))
                .append("+ \"").append(sqlfields.toString()).append("\"").append(r(1))
                .append(t(3))
                .append("+ \") values (").append(sqlvals.toString()).append(")\";")
                .append(r(1));
        sb.append(t(2));
        sb.append("logger.debug(\"insert sql = {{}}\",").append("sql").append(");").append(r(1));
        sb.append(t(2));
        sb.append("PreparedStatement pstmt = null;").append(r(1));
        sb.append(t(2)).append("boolean initAuto = false;").append(r(1));
        sb.append(t(2));
        sb.append("try {").append(r(1));
        
        sb.append(t(3)).append("if (con.getAutoCommit()) {").append(r(1));
        sb.append(t(4)).append("initAuto = true;").append(r(1));
        sb.append(t(4)).append("con.setAutoCommit(false);").append(r(1));
        sb.append(t(3)).append("}").append(r(1));
        sb.append(t(3)).append(((Class)idInfo.getType()).getSimpleName()).append(" nid = null;").append(r(1));
        sb.append(this.getNextId(cls)).append(r(1));
        sb.append(t(3)).append("int row = 0;").append(r(1));
        sb.append(t(3)).append("if (nid != null) {").append(r(1));
        sb.append(t(4)).append("pstmt = con.prepareStatement(sql);").append(r(1));
        int i = 0;

        for (FieldInfo finfo : fields) {
            sb.append(t(4));
            Object otype = finfo.getType();
            String fieldn = finfo.getName();
            StringBuilder valStr = new StringBuilder();
            if (!isId(finfo)) {
                valStr.append("entity.get")
                        .append(fieldn.substring(0, 1).toUpperCase())
                        .append(fieldn.substring(1))
                        .append("()");
            } else {
                valStr.append("nid");
            }
            if (otype.equals(Long.class) 
                    || otype.toString().equals("long")) {
                sb.append("pstmt.setLong(")
                        .append(i+1).append(", ").append(valStr)
                        .append(");").append(r(1));
            } else if (otype.equals(Integer.class) 
                    || otype.toString().equals("int")) {
                sb.append("pstmt.setInt(")
                        .append(i+1).append(", ").append(valStr)
                        .append(");").append(r(1));
            } else if (otype.equals(String.class)) {
                sb.append("pstmt.setString(")
                        .append(i+1).append(", ").append(valStr)
                        .append(");").append(r(1));
            } else if (otype.equals(Date.class)) {
                Annotation[] anns = finfo.getAnnotations();
                Temporal temporalANN = null;
                if (anns != null) {
                    for  (int j=0;j<anns.length;j++) {
                        Annotation ann = anns[j];
                        if (ann instanceof Temporal) {
                            temporalANN = (Temporal)ann;
                        }
                    }
                }
                if (temporalANN != null) {
                    Temporal datetype = (Temporal)temporalANN;
                    if (datetype.value().equals(TemporalType.TIMESTAMP)) {
                        sb.append("Timestamp ts").append(i).append(" = null;").append(r(1));
                        sb.append(t(4));
                        sb.append("if (").append(valStr).append(" != null) {").append(r(1));
                        sb.append(t(5));
                        sb.append("ts").append(i).append(" = new Timestamp(")
                                .append(valStr).append(".getTime());").append(r(1));
                        sb.append(t(4));
                        sb.append("}").append(r(1));
                        sb.append(t(4));
                        sb.append("pstmt.setTimestamp(")
                                .append(i+1).append(", ts").append(i).append(");").append(r(1));
                    } else if (datetype.value().equals(TemporalType.DATE)) {
                        sb.append("java.sql.Date ts").append(i).append(" = null;").append(r(1));
                        sb.append(t(4));
                        sb.append("if (").append(valStr).append(" != null) {").append(r(1));
                        sb.append(t(5));
                        sb.append("ts").append(i).append(" = new java.sql.Date(")
                                .append(valStr).append(".getTime());").append(r(1));
                        sb.append(t(4));
                        sb.append("}").append(r(1));
                        sb.append(t(4));
                        sb.append("pstmt.setDate(")
                                .append(i+1).append(", ts").append(i).append(");").append(r(1));
                    } else if (datetype.value().equals(TemporalType.TIME)) {
                        sb.append("java.sql.Time ts").append(i).append(" = null;").append(r(1));
                        sb.append(t(4));
                        sb.append("if (").append(valStr).append(" != null) {").append(r(1));
                        sb.append(t(5));
                        sb.append("ts").append(i).append(" = new java.sql.Time(")
                                .append(valStr).append(".getTime());").append(r(1));
                        sb.append(t(4));
                        sb.append("}").append(r(1));
                        sb.append(t(4));
                        sb.append("pstmt.setTime(")
                                .append(i+1).append(", ts").append(i).append(");").append(r(1));
                    } else {
                        sb.append("java.sql.Timestamp ts").append(i).append(" = null;").append(r(1));
                        sb.append(t(4));
                        sb.append("if (").append(valStr).append(" != null) {").append(r(1));
                        sb.append(t(5));
                        sb.append("ts").append(i).append(" = new java.sql.Timestamp(")
                                .append(valStr).append(".getTime());").append(r(1));
                        sb.append(t(4));
                        sb.append("}").append(r(1));
                        sb.append(t(4));
                        sb.append("pstmt.setTimestamp(")
                                .append(i+1).append(", ts").append(i).append(");").append(r(1));
                    }
                } else {
                    sb.append("java.sql.Timestamp ts").append(i).append(" = null;").append(r(1));
                    sb.append(t(4));
                    sb.append("if (").append(valStr).append(" != null) {").append(r(1));
                    sb.append(t(5));
                    sb.append("ts").append(i).append(" = new java.sql.Timestamp(")
                            .append(valStr).append(".getTime());").append(r(1));
                    sb.append(t(4));
                    sb.append("}").append(r(1));
                    sb.append(t(4));
                    sb.append("pstmt.setTimestamp(")
                            .append(i+1).append(", ts").append(i).append(");").append(r(1));
                }
            } else if (otype.equals(Boolean.class)) {
                sb.append("pstmt.setBoolean(")
                        .append(i+1).append(", ").append(valStr)
                        .append(");").append(r(1));
            } else if (otype.equals(Double.class)) {
                sb.append("pstmt.setDouble(")
                        .append(i+1).append(", ").append(valStr)
                        .append(");").append(r(1));
            } else {
                sb.append("pstmt.setObject(")
                        .append(i+1).append(", ").append(valStr)
                        .append(");").append(r(1));
            }
            i++;
        }
        sb.append(t(4)).append("row = pstmt.executeUpdate();").append(r(1));
        sb.append(t(4)).append("if (row > 0) {").append(r(1));
        sb.append(t(5)).append("entity.setId(nid);").append(r(1));
        sb.append(t(4)).append("}").append(r(1));
        sb.append(t(3)).append("}").append(r(1));

        sb.append(t(3)).append("if (initAuto) {").append(r(1));
        sb.append(t(4)).append("con.commit();").append(r(1));
        sb.append(t(3)).append("}").append(r(1));
        sb.append(t(2)).append("} catch (SQLException ex) {").append(r(1));
        sb.append(t(3));
        sb.append("logger.error(\"{}\",").append("ex.getMessage()").append(");").append(r(1));
        sb.append(t(3)).append("throw ex;").append(r(1));
        sb.append(t(2)).append("} finally {").append(r(1));
        sb.append(t(3)).append("try {pstmt.close();}catch (Exception e) {}").append(r(1));
        sb.append(t(3)).append("if (initAuto) {").append(r(1));
        sb.append(t(4)).append("con.setAutoCommit(true);").append(r(1));
        sb.append(t(3)).append("}").append(r(1));
        sb.append(t(2)).append("}").append(r(1));

        sb.append(t(2)).append("return ent;").append(r(1));
        sb.append(t(1)).append("}").append(r(1));
        return sb.toString();
    }

    /**
     * 生成实现 public <T> T persist(T entity) throws SQLException, Exception; 方法的代码
     *
     * @param cls
     * @return
     */
    public String getIdentityPersist(Class cls) {
        StringBuilder sb = new StringBuilder();
        sb.append(t(1)).append("@Override").append(r(1));
        sb.append(t(1)).append("public <T> T persist(T ent) throws SQLException, Exception {").append(r(1));
        sb.append(t(2)).append(cls.getSimpleName()).append(" entity = (")
                .append(cls.getSimpleName()).append(")ent;").append(r(1));
        List<FieldInfo> fields = ClassUtil.getFields(cls);
        StringBuilder sqlfields = new StringBuilder();
        StringBuilder sqlvals   = new StringBuilder();
        int fieldCount = 0;
        FieldInfo idInfo = null;
        for (FieldInfo finfo : fields) {
            Annotation[] anns = finfo.getAnnotations();
            String fieldName = this.getDataField(finfo);
            if (this.isId(finfo)) {
                idInfo = finfo;
            }
            if (anns != null) {
                for  (int i=0;i<anns.length;i++) {
                    Annotation ann = anns[i];
                    if (ann instanceof Column) {
                        Column col = (Column)ann;
                        if (col.name() != null && col.name().length() > 0) {
                            fieldName = col.name();
                        }
                    }
                }
            }
            if (!isId(finfo)) {
                if (sqlfields.length() > 0) {
                    sqlfields.append(",").append(fieldName);
                    sqlvals.append(",?");
                } else {
                    sqlfields.append(fieldName);
                    sqlvals.append("?");
                }
                if (fieldCount != 0 && fieldCount%4 == 0) {
                    sqlfields.append("\"").append(r(1));
                    sqlfields.append(t(3)).append("+ \"");
                }
                //System.out.println(field);
                //System.out.println("\t" + (Object)types.get(field));
                fieldCount++;
            }
        }

        String sql = "";
        String tbName = getTableName(cls);
        sb.append(t(2))
                .append("String sql = \"insert into ")
                .append(tbName).append(" (\"").append(r(1))
                .append(t(3))
                .append("+ \"").append(sqlfields.toString()).append("\"").append(r(1))
                .append(t(3))
                .append("+ \") values (").append(sqlvals.toString()).append(")\";")
                .append(r(1));
        sb.append(t(2));
        sb.append("logger.debug(\"insert sql = {{}}\",").append("sql").append(");").append(r(1));
        sb.append(t(2));
        sb.append("PreparedStatement pstmt = null;").append(r(1));
        sb.append(t(2)).append("boolean initAuto = false;").append(r(1));
        sb.append(t(2));
        sb.append("try {").append(r(1));
        
        sb.append(t(3)).append("if (con.getAutoCommit()) {").append(r(1));
        sb.append(t(4)).append("initAuto = true;").append(r(1));
        sb.append(t(4)).append("con.setAutoCommit(false);").append(r(1));
        sb.append(t(3)).append("}").append(r(1));
        sb.append(t(3)).append(((Class)idInfo.getType()).getSimpleName()).append(" nid = null;").append(r(1));
        sb.append(t(3)).append("int row = 0;").append(r(1));
        sb.append(t(4)).append("pstmt = con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);").append(r(1));
        int i = 0;
        for (FieldInfo finfo : fields) {
            if (!isId(finfo)) {
            sb.append(t(4));
            Object otype = finfo.getType();
            String fieldn = finfo.getName();
            StringBuilder valStr = new StringBuilder();
            if (!isId(finfo)) {
                valStr.append("entity.get")
                        .append(fieldn.substring(0, 1).toUpperCase())
                        .append(fieldn.substring(1))
                        .append("()");
            } else {
                valStr.append("nid");
            }
            if (otype.equals(Long.class) 
                    || otype.toString().equals("long")) {
                sb.append("pstmt.setLong(")
                        .append(i+1).append(", entity.get")
                        .append(fieldn.substring(0, 1).toUpperCase())
                        .append(fieldn.substring(1))
                        .append("());").append(r(1));
            } else if (otype.equals(Integer.class) 
                    || otype.toString().equals("int")) {
                sb.append("pstmt.setInt(")
                        .append(i+1).append(", entity.get")
                        .append(fieldn.substring(0, 1).toUpperCase())
                        .append(fieldn.substring(1))
                        .append("());").append(r(1));
            } else if (otype.equals(String.class)) {
                sb.append("pstmt.setString(")
                        .append(i+1).append(", entity.get")
                        .append(fieldn.substring(0, 1).toUpperCase())
                        .append(fieldn.substring(1))
                        .append("());").append(r(1));
            } else if (otype.equals(Date.class)) {
                Annotation[] anns = finfo.getAnnotations();
                Temporal temporalANN = null;
                if (anns != null) {
                    for  (int j=0;j<anns.length;j++) {
                        Annotation ann = anns[j];
                        if (ann instanceof Temporal) {
                            temporalANN = (Temporal)ann;
                        }
                    }
                }
                if (temporalANN != null) {
                    Temporal datetype = (Temporal)temporalANN;
                    if (datetype.value().equals(TemporalType.TIMESTAMP)) {
                        sb.append("Timestamp ts").append(i).append(" = null;").append(r(1));
                        sb.append(t(4));
                        sb.append("if (").append(valStr).append(" != null) {").append(r(1));
                        sb.append(t(5));
                        sb.append("ts").append(i).append(" = new Timestamp(")
                                .append(valStr).append(".getTime());").append(r(1));
                        sb.append(t(4));
                        sb.append("}").append(r(1));
                        sb.append(t(4));
                        sb.append("pstmt.setTimestamp(")
                                .append(i+1).append(", ts").append(i).append(");").append(r(1));
                    } else if (datetype.value().equals(TemporalType.DATE)) {
                        sb.append("java.sql.Date ts").append(i).append(" = null;").append(r(1));
                        sb.append(t(4));
                        sb.append("if (").append(valStr).append(" != null) {").append(r(1));
                        sb.append(t(5));
                        sb.append("ts").append(i).append(" = new java.sql.Date(")
                                .append(valStr).append(".getTime());").append(r(1));
                        sb.append(t(4));
                        sb.append("}").append(r(1));
                        sb.append(t(4));
                        sb.append("pstmt.setDate(")
                                .append(i+1).append(", ts").append(i).append(");").append(r(1));
                    } else if (datetype.value().equals(TemporalType.TIME)) {
                        sb.append("java.sql.Time ts").append(i).append(" = null;").append(r(1));
                        sb.append(t(4));
                        sb.append("if (").append(valStr).append(" != null) {").append(r(1));
                        sb.append(t(5));
                        sb.append("ts").append(i).append(" = new java.sql.Time(")
                                .append(valStr).append(".getTime());").append(r(1));
                        sb.append(t(4));
                        sb.append("}").append(r(1));
                        sb.append(t(4));
                        sb.append("pstmt.setTime(")
                                .append(i+1).append(", ts").append(i).append(");").append(r(1));
                    } else {
                        sb.append("java.sql.Timestamp ts").append(i).append(" = null;").append(r(1));
                        sb.append(t(4));
                        sb.append("if (").append(valStr).append(" != null) {").append(r(1));
                        sb.append(t(5));
                        sb.append("ts").append(i).append(" = new java.sql.Timestamp(")
                                .append(valStr).append(".getTime());").append(r(1));
                        sb.append(t(4));
                        sb.append("}").append(r(1));
                        sb.append(t(4));
                        sb.append("pstmt.setTimestamp(")
                                .append(i+1).append(", ts").append(i).append(");").append(r(1));
                    }
                } else {
                    sb.append("java.sql.Timestamp ts").append(i).append(" = null;").append(r(1));
                    sb.append(t(4));
                    sb.append("if (").append(valStr).append(" != null) {").append(r(1));
                    sb.append(t(5));
                    sb.append("ts").append(i).append(" = new java.sql.Timestamp(")
                            .append(valStr).append(".getTime());").append(r(1));
                    sb.append(t(4));
                    sb.append("}").append(r(1));
                    sb.append(t(4));
                    sb.append("pstmt.setTimestamp(")
                            .append(i+1).append(", ts").append(i).append(");").append(r(1));
                }
            } else if (otype.equals(Boolean.class)) {
                sb.append("pstmt.setBoolean(")
                        .append(i+1).append(", entity.get")
                        .append(fieldn.substring(0, 1).toUpperCase())
                        .append(fieldn.substring(1))
                        .append("());").append(r(1));
            } else if (otype.equals(Double.class)) {
                sb.append("pstmt.setDouble(")
                        .append(i+1).append(", entity.get")
                        .append(fieldn.substring(0, 1).toUpperCase())
                        .append(fieldn.substring(1))
                        .append("());").append(r(1));
            } else {
                sb.append("pstmt.setObject(")
                        .append(i+1).append(", entity.get")
                        .append(fieldn.substring(0, 1).toUpperCase())
                        .append(fieldn.substring(1))
                        .append("());").append(r(1));
            }
            i++;
            }
        }
        sb.append(t(4)).append("row = pstmt.executeUpdate();").append(r(1));
        
        sb.append(t(4)).append("if (row > 0) {").append(r(1));
        sb.append(t(5)).append("ResultSet prs = pstmt.getGeneratedKeys();").append(r(1));
        sb.append(t(5)).append("if (prs.next()) {").append(r(1));
        sb.append(t(6)).append("nid = prs.getLong(1);").append(r(1));
        sb.append(t(5)).append("}").append(r(1));
        sb.append(t(5)).append("entity.setId(nid);").append(r(1));
        sb.append(t(4)).append("}").append(r(1));

        sb.append(t(3)).append("if (initAuto) {").append(r(1));
        sb.append(t(4)).append("con.commit();").append(r(1));
        sb.append(t(3)).append("}").append(r(1));
        sb.append(t(2)).append("} catch (SQLException ex) {").append(r(1));
        sb.append(t(3));
        sb.append("logger.error(\"{}\",").append("ex.getMessage()").append(");").append(r(1));
        sb.append(t(3)).append("throw ex;").append(r(1));
        sb.append(t(2)).append("} finally {").append(r(1));
        sb.append(t(3)).append("try {pstmt.close();}catch (Exception e) {}").append(r(1));
        sb.append(t(3)).append("if (initAuto) {").append(r(1));
        sb.append(t(4)).append("con.setAutoCommit(true);").append(r(1));
        sb.append(t(3)).append("}").append(r(1));
        sb.append(t(2)).append("}").append(r(1));

        sb.append(t(2)).append("return ent;").append(r(1));
        sb.append(t(1)).append("}").append(r(1));
        return sb.toString();
    }

    /**
     * 生产实现 public void persist(List<?> entities) throws SQLException,
     * Exception; 方法的代码
     *
     * @param cls
     * @return
     */
    public String getPersists(Class cls) {
        GenerationType gt = getGenerationType(cls);
        if (gt == null) {
            gt = getGenerationType();
        }
        if (gt == null) {
            gt = GenerationType.SEQUENCE;
        }
        if (gt == GenerationType.IDENTITY) {
            return getIdentityPersists(cls);
        } else {
            return getSequencePersists(cls);
        }
    }
    
    public String getIdentityPersists(Class cls) {
        StringBuilder sb = new StringBuilder();
        sb.append(t(1)).append("@Override").append(r(1));
        sb.append(t(1)).append("public void persist(List<?> entities) throws SQLException, Exception {").append(r(1));
        sb.append(t(2)).append("if (entities == null) {").append(r(1));
        sb.append(t(3)).append("return;").append(r(1));
        sb.append(t(2)).append("}").append(r(1));
        List<FieldInfo> fields = ClassUtil.getFields(cls);
        StringBuilder sqlfields = new StringBuilder();
        StringBuilder sqlvals   = new StringBuilder();
        int fieldCount = 0;
        FieldInfo idInfo = null;
        for (FieldInfo finfo : fields) {
            Annotation[] anns = finfo.getAnnotations();
            String fieldName = this.getDataField(finfo);
            if (this.isId(finfo)) {
                idInfo = finfo;
            }
            if (anns != null) {
                for  (int i=0;i<anns.length;i++) {
                    Annotation ann = anns[i];
                    if (ann instanceof Column) {
                        Column col = (Column)ann;
                        fieldName = col.name();
                    }
                }
            }
            if (!isId(finfo)) {
                if (sqlfields.length() > 0) {
                    sqlfields.append(",").append(fieldName);
                    sqlvals.append(",?");
                } else {
                    sqlfields.append(fieldName);
                    sqlvals.append("?");
                }
                if (fieldCount != 0 && fieldCount%4 == 0) {
                    sqlfields.append("\"").append(r(1));
                    sqlfields.append(t(3)).append("+ \"");
                }
                //System.out.println(field);
                //System.out.println("\t" + (Object)types.get(field));
                fieldCount++;
            }
        }

        String sql = "";
        String tbName = getTableName(cls);
        sb.append(t(2))
                .append("String sql = \"insert into ")
                .append(tbName).append(" (\"").append(r(1))
                .append(t(3))
                .append("+ \"").append(sqlfields.toString()).append("\"").append(r(1))
                .append(t(3))
                .append("+ \") values (").append(sqlvals.toString()).append(")\";")
                .append(r(1));
        sb.append(t(2));
        sb.append("logger.debug(\"insert sql = {{}}\",").append("sql").append(");").append(r(1));
        sb.append(t(2));
        sb.append("PreparedStatement pstmt = null;").append(r(1));
        sb.append(t(2)).append("boolean initAuto = false;").append(r(1));
        sb.append(t(2));
        sb.append("try {").append(r(1));
        sb.append(t(3)).append("if (con.getAutoCommit()) {").append(r(1));
        sb.append(t(4)).append("initAuto = true;").append(r(1));
        sb.append(t(4)).append("con.setAutoCommit(false);").append(r(1));
        sb.append(t(3)).append("}").append(r(1));
        sb.append(t(3)).append("for (Object")
                .append(" ent : entities) {").append(r(1));
        sb.append(t(4)).append("int row = 0;").append(r(1));
        sb.append(t(4)).append(cls.getSimpleName()).append(" entity = (")
                .append(cls.getSimpleName()).append(")ent;").append(r(1));
        sb.append(t(4)).append(((Class)idInfo.getType()).getSimpleName()).append(" nid = null;").append(r(1));
        sb.append(t(5)).append("pstmt = con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);").append(r(1));
        int i = 0;
        for (FieldInfo finfo : fields) {
            sb.append(t(5));
            Object otype = finfo.getType();
            String fieldn = finfo.getName();
            StringBuilder valStr = new StringBuilder();
            if (!isId(finfo)) {
                valStr.append("entity.get")
                        .append(fieldn.substring(0, 1).toUpperCase())
                        .append(fieldn.substring(1))
                        .append("()");
            } else {
                valStr.append("nid");
            }
            if (otype.equals(Long.class) 
                    || otype.toString().equals("long")) {
                sb.append("pstmt.setLong(")
                        .append(i+1).append(", entity.get")
                        .append(fieldn.substring(0, 1).toUpperCase())
                        .append(fieldn.substring(1))
                        .append("());").append(r(1));
            } else if (otype.equals(Integer.class) 
                    || otype.toString().equals("int")) {
                sb.append("pstmt.setInt(")
                        .append(i+1).append(", entity.get")
                        .append(fieldn.substring(0, 1).toUpperCase())
                        .append(fieldn.substring(1))
                        .append("());").append(r(1));
            } else if (otype.equals(String.class)) {
                sb.append("pstmt.setString(")
                        .append(i+1).append(", entity.get")
                        .append(fieldn.substring(0, 1).toUpperCase())
                        .append(fieldn.substring(1))
                        .append("());").append(r(1));
            } else if (otype.equals(Date.class)) {
                Annotation[] anns = finfo.getAnnotations();
                Temporal temporalANN = null;
                if (anns != null) {
                    for  (int j=0;j<anns.length;j++) {
                        Annotation ann = anns[j];
                        if (ann instanceof Temporal) {
                            temporalANN = (Temporal)ann;
                        }
                    }
                }
                if (temporalANN != null) {
                    Temporal datetype = (Temporal)temporalANN;
                    if (datetype.value().equals(TemporalType.TIMESTAMP)) {
                        sb.append("Timestamp ts").append(i).append(" = null;").append(r(1));
                        sb.append(t(4));
                        sb.append("if (").append(valStr).append(" != null) {").append(r(1));
                        sb.append(t(5));
                        sb.append("ts").append(i).append(" = new Timestamp(")
                                .append(valStr).append(".getTime());").append(r(1));
                        sb.append(t(4));
                        sb.append("}").append(r(1));
                        sb.append("pstmt.setTimestamp(")
                                .append(i+1).append(", ts").append(i).append(");").append(r(1));
                    } else if (datetype.value().equals(TemporalType.DATE)) {
                        sb.append("java.sql.Date ts").append(i).append(" = null;").append(r(1));
                        sb.append(t(4));
                        sb.append("if (").append(valStr).append(" != null) {").append(r(1));
                        sb.append(t(5));
                        sb.append("ts").append(i).append(" = new java.sql.Date(")
                                .append(valStr).append(".getTime());").append(r(1));
                        sb.append(t(4));
                        sb.append("}").append(r(1));
                        sb.append("pstmt.setDate(")
                                .append(i+1).append(", ts").append(i).append(");").append(r(1));
                    } else if (datetype.value().equals(TemporalType.TIME)) {
                        sb.append("java.sql.Time ts").append(i).append(" = null;").append(r(1));
                        sb.append(t(4));
                        sb.append("if (").append(valStr).append(" != null) {").append(r(1));
                        sb.append(t(5));
                        sb.append("ts").append(i).append(" = new java.sql.Time(")
                                .append(valStr).append(".getTime());").append(r(1));
                        sb.append(t(4));
                        sb.append("}").append(r(1));
                        sb.append("pstmt.setTime(")
                                .append(i+1).append(", ts").append(i).append(");").append(r(1));
                    } else {
                        sb.append("java.sql.Timestamp ts").append(i).append(" = null;").append(r(1));
                        sb.append(t(4));
                        sb.append("if (").append(valStr).append(" != null) {").append(r(1));
                        sb.append(t(5));
                        sb.append("ts").append(i).append(" = new java.sql.Timestamp(")
                                .append(valStr).append(".getTime());").append(r(1));
                        sb.append(t(4));
                        sb.append("}").append(r(1));
                        sb.append("pstmt.setTimestamp(")
                                .append(i+1).append(", ts").append(i).append(");").append(r(1));
                    }
                } else {
                    sb.append("java.sql.Timestamp ts").append(i).append(" = null;").append(r(1));
                    sb.append(t(4));
                    sb.append("if (").append(valStr).append(" != null) {").append(r(1));
                    sb.append(t(5));
                    sb.append("ts").append(i).append(" = new java.sql.Timestamp(")
                            .append(valStr).append(".getTime());").append(r(1));
                    sb.append(t(4));
                    sb.append("}").append(r(1));
                    sb.append("pstmt.setTimestamp(")
                            .append(i+1).append(", ts").append(i).append(");").append(r(1));
                }
            } else if (otype.equals(Boolean.class)) {
                sb.append("pstmt.setBoolean(")
                        .append(i+1).append(", entity.get")
                        .append(fieldn.substring(0, 1).toUpperCase())
                        .append(fieldn.substring(1))
                        .append("());").append(r(1));
            } else if (otype.equals(Double.class)) {
                sb.append("pstmt.setDouble(")
                        .append(i+1).append(", entity.get")
                        .append(fieldn.substring(0, 1).toUpperCase())
                        .append(fieldn.substring(1))
                        .append("());").append(r(1));
            } else {
                sb.append("pstmt.setObject(")
                        .append(i+1).append(", entity.get")
                        .append(fieldn.substring(0, 1).toUpperCase())
                        .append(fieldn.substring(1))
                        .append("());").append(r(1));
            }
            i++;
        }
        sb.append(t(5)).append("row = pstmt.executeUpdate();").append(r(1));
        sb.append(t(5)).append("if (row > 0) {").append(r(1));
        sb.append(t(5)).append("ResultSet prs = pstmt.getGeneratedKeys();").append(r(1));
        sb.append(t(5)).append("if (prs.next()) {").append(r(1));
        sb.append(t(6)).append("nid = prs.getLong(1);").append(r(1));
        sb.append(t(5)).append("}").append(r(1));
        sb.append(t(6)).append("entity.setId(nid);").append(r(1));
        sb.append(t(5)).append("}").append(r(1));
        sb.append(t(4)).append("}").append(r(1));
        
        sb.append(t(3)).append("if (initAuto) {").append(r(1));
        sb.append(t(4)).append("con.commit();").append(r(1));
        sb.append(t(3)).append("}").append(r(1));
        sb.append(t(2)).append("} catch (SQLException ex) {").append(r(1));
        sb.append(t(3));
        sb.append("logger.error(\"{}\",").append("ex.getMessage()").append(");").append(r(1));
        sb.append(t(3)).append("throw ex;").append(r(1));
        sb.append(t(2)).append("} finally {").append(r(1));
        sb.append(t(3)).append("try {pstmt.close();}catch (Exception e) {}").append(r(1));
        sb.append(t(3)).append("if (initAuto) {").append(r(1));
        sb.append(t(4)).append("con.setAutoCommit(true);").append(r(1));
        sb.append(t(3)).append("}").append(r(1));
        sb.append(t(2)).append("}").append(r(1));
        sb.append(t(1)).append("}").append(r(1));
        return sb.toString();
    }
    
    public String getSequencePersists(Class cls) {
        StringBuilder sb = new StringBuilder();
        sb.append(t(1)).append("@Override").append(r(1));
        sb.append(t(1)).append("public void persist(List<?> entities) throws SQLException, Exception {").append(r(1));
        sb.append(t(2)).append("if (entities == null) {").append(r(1));
        sb.append(t(3)).append("return;").append(r(1));
        sb.append(t(2)).append("}").append(r(1));
        List<FieldInfo> fields = ClassUtil.getFields(cls);
        StringBuilder sqlfields = new StringBuilder();
        StringBuilder sqlvals   = new StringBuilder();
        int fieldCount = 0;
        FieldInfo idInfo = null;
        for (FieldInfo finfo : fields) {
            Annotation[] anns = finfo.getAnnotations();
            String fieldName = this.getDataField(finfo);
            if (this.isId(finfo)) {
                idInfo = finfo;
            }
            if (anns != null) {
                for  (int i=0;i<anns.length;i++) {
                    Annotation ann = anns[i];
                    if (ann instanceof Column) {
                        Column col = (Column)ann;
                        fieldName = col.name();
                    }
                }
            }
            if (sqlfields.length() > 0) {
                sqlfields.append(",").append(fieldName);
                sqlvals.append(",?");
            } else {
                sqlfields.append(fieldName);
                sqlvals.append("?");
            }
            if (fieldCount != 0 && fieldCount%4 == 0) {
                sqlfields.append("\"").append(r(1));
                sqlfields.append(t(3)).append("+ \"");
            }
            //System.out.println(field);
            //System.out.println("\t" + (Object)types.get(field));
            fieldCount++;
        }

        String sql = "";
        String tbName = getTableName(cls);
        sb.append(t(2))
                .append("String sql = \"insert into ")
                .append(tbName).append(" (\"").append(r(1))
                .append(t(3))
                .append("+ \"").append(sqlfields.toString()).append("\"").append(r(1))
                .append(t(3))
                .append("+ \") values (").append(sqlvals.toString()).append(")\";")
                .append(r(1));
        sb.append(t(2));
        sb.append("logger.debug(\"insert sql = {{}}\",").append("sql").append(");").append(r(1));
        sb.append(t(2));
        sb.append("PreparedStatement pstmt = null;").append(r(1));
        sb.append(t(2)).append("boolean initAuto = false;").append(r(1));
        sb.append(t(2));
        sb.append("try {").append(r(1));
        sb.append(t(3)).append("if (con.getAutoCommit()) {").append(r(1));
        sb.append(t(4)).append("initAuto = true;").append(r(1));
        sb.append(t(4)).append("con.setAutoCommit(false);").append(r(1));
        sb.append(t(3)).append("}").append(r(1));
        sb.append(t(3)).append("for (Object")
                .append(" ent : entities) {").append(r(1));
        sb.append(t(4)).append("int row = 0;").append(r(1));
        sb.append(t(4)).append(cls.getSimpleName()).append(" entity = (")
                .append(cls.getSimpleName()).append(")ent;").append(r(1));
        sb.append(t(4)).append(((Class)idInfo.getType()).getSimpleName()).append(" nid = null;").append(r(1));
        sb.append(this.getNextId(cls)).append(r(1));
        sb.append(t(4)).append("if (nid != null) {").append(r(1));
        sb.append(t(5)).append("pstmt = con.prepareStatement(sql);").append(r(1));
        int i = 0;
        for (FieldInfo finfo : fields) {
            sb.append(t(5));
            Object otype = finfo.getType();
            String fieldn = finfo.getName();
            StringBuilder valStr = new StringBuilder();
            if (!isId(finfo)) {
                valStr.append("entity.get")
                        .append(fieldn.substring(0, 1).toUpperCase())
                        .append(fieldn.substring(1))
                        .append("()");
            } else {
                valStr.append("nid");
            }
            if (otype.equals(Long.class) 
                    || otype.toString().equals("long")) {
                sb.append("pstmt.setLong(")
                        .append(i+1).append(", ").append(valStr)
                        .append(");").append(r(1));
            } else if (otype.equals(Integer.class) 
                    || otype.toString().equals("int")) {
                sb.append("pstmt.setInt(")
                        .append(i+1).append(", ").append(valStr)
                        .append(");").append(r(1));
            } else if (otype.equals(String.class)) {
                sb.append("pstmt.setString(")
                        .append(i+1).append(", ").append(valStr)
                        .append(");").append(r(1));
            } else if (otype.equals(Date.class)) {
                Annotation[] anns = finfo.getAnnotations();
                Temporal temporalANN = null;
                if (anns != null) {
                    for  (int j=0;j<anns.length;j++) {
                        Annotation ann = anns[j];
                        if (ann instanceof Temporal) {
                            temporalANN = (Temporal)ann;
                        }
                    }
                }
                if (temporalANN != null) {
                    Temporal datetype = (Temporal)temporalANN;
                    if (datetype.value().equals(TemporalType.TIMESTAMP)) {
                        sb.append("Timestamp ts").append(i).append(" = null;").append(r(1));
                        sb.append(t(4));
                        sb.append("if (").append(valStr).append(" != null) {").append(r(1));
                        sb.append(t(5));
                        sb.append("ts").append(i).append(" = new Timestamp(")
                                .append(valStr).append(".getTime());").append(r(1));
                        sb.append(t(4));
                        sb.append("}").append(r(1));
                        sb.append(t(4));
                        sb.append("pstmt.setTimestamp(")
                                .append(i+1).append(", ts").append(i).append(");").append(r(1));
                    } else if (datetype.value().equals(TemporalType.DATE)) {
                        sb.append("java.sql.Date ts").append(i).append(" = null;").append(r(1));
                        sb.append(t(4));
                        sb.append("if (").append(valStr).append(" != null) {").append(r(1));
                        sb.append(t(5));
                        sb.append("ts").append(i).append(" = new java.sql.Date(")
                                .append(valStr).append(".getTime());").append(r(1));
                        sb.append(t(4));
                        sb.append("}").append(r(1));
                        sb.append(t(4));
                        sb.append("pstmt.setDate(")
                                .append(i+1).append(", ts").append(i).append(");").append(r(1));
                    } else if (datetype.value().equals(TemporalType.TIME)) {
                        sb.append("java.sql.Time ts").append(i).append(" = null;").append(r(1));
                        sb.append(t(4));
                        sb.append("if (").append(valStr).append(" != null) {").append(r(1));
                        sb.append(t(5));
                        sb.append("ts").append(i).append(" = new java.sql.Time(")
                                .append(valStr).append(".getTime());").append(r(1));
                        sb.append(t(4));
                        sb.append("}").append(r(1));
                        sb.append(t(4));
                        sb.append("pstmt.setTime(")
                                .append(i+1).append(", ts").append(i).append(");").append(r(1));
                    } else {
                        sb.append("java.sql.Timestamp ts").append(i).append(" = null;").append(r(1));
                        sb.append(t(4));
                        sb.append("if (").append(valStr).append(" != null) {").append(r(1));
                        sb.append(t(5));
                        sb.append("ts").append(i).append(" = new java.sql.Timestamp(")
                                .append(valStr).append(".getTime());").append(r(1));
                        sb.append(t(4));
                        sb.append("}").append(r(1));
                        sb.append(t(4));
                        sb.append("pstmt.setTimestamp(")
                                .append(i+1).append(", ts").append(i).append(");").append(r(1));
                    }
                } else {
                    sb.append("java.sql.Timestamp ts").append(i).append(" = null;").append(r(1));
                    sb.append(t(4));
                    sb.append("if (").append(valStr).append(" != null) {").append(r(1));
                    sb.append(t(5));
                    sb.append("ts").append(i).append(" = new java.sql.Timestamp(")
                            .append(valStr).append(".getTime());").append(r(1));
                    sb.append(t(4));
                    sb.append("}").append(r(1));
                    sb.append(t(4));
                    sb.append("pstmt.setTimestamp(")
                            .append(i+1).append(", ts").append(i).append(");").append(r(1));
                }
            } else if (otype.equals(Boolean.class)) {
                sb.append("pstmt.setBoolean(")
                        .append(i+1).append(", ").append(valStr)
                        .append(");").append(r(1));
            } else if (otype.equals(Double.class)) {
                sb.append("pstmt.setDouble(")
                        .append(i+1).append(", ").append(valStr)
                        .append(");").append(r(1));
            } else {
                sb.append("pstmt.setObject(")
                        .append(i+1).append(", ").append(valStr)
                        .append(");").append(r(1));
            }
            i++;
        }
        sb.append(t(5)).append("row = pstmt.executeUpdate();").append(r(1));
        sb.append(t(5)).append("if (row > 0) {").append(r(1));
        sb.append(t(6)).append("entity.setId(nid);").append(r(1));
        sb.append(t(5)).append("}").append(r(1));
        sb.append(t(4)).append("}").append(r(1));
        
        
        sb.append(t(3)).append("}").append(r(1));
        sb.append(t(3)).append("if (initAuto) {").append(r(1));
        sb.append(t(4)).append("con.commit();").append(r(1));
        sb.append(t(3)).append("}").append(r(1));
        sb.append(t(2)).append("} catch (SQLException ex) {").append(r(1));
        sb.append(t(3));
        sb.append("logger.error(\"{}\",").append("ex.getMessage()").append(");").append(r(1));
        sb.append(t(3)).append("throw ex;").append(r(1));
        sb.append(t(2)).append("} finally {").append(r(1));
        sb.append(t(3)).append("try {pstmt.close();}catch (Exception e) {}").append(r(1));
        sb.append(t(3)).append("if (initAuto) {").append(r(1));
        sb.append(t(4)).append("con.setAutoCommit(true);").append(r(1));
        sb.append(t(3)).append("}").append(r(1));
        sb.append(t(2)).append("}").append(r(1));
        sb.append(t(1)).append("}").append(r(1));
        return sb.toString();
    }
    
    /**
     * 根据指定的持久化Bean类生成用于生成下一个主键的Java代码。下一个主键的变量名为“nid”，
     * 类型为javabean中添加@Id注释的属性的类型
     * @param cls
     * @return 
     */
    public abstract String getNextId(Class cls);

    /**
     * 生成实现 public boolean remove(Object entity) throws SQLException, Exception;
     * 方法的代码
     *
     * @param cls
     * @return
     */
    public String getRemove1(Class cls) {
        StringBuilder sb = new StringBuilder();
        sb.append(r(1));
        sb.append(t(1)).append("@Override").append(r(1));
        sb.append(t(1)).append("public boolean remove(Object ent) throws SQLException, Exception {").append(r(1));
        sb.append(t(2)).append(cls.getSimpleName()).append(" entity = (")
                .append(cls.getSimpleName()).append(")ent;").append(r(1));
        sb.append(t(2)).append("int row = this.remove(\" where id=\" + ")
                .append("entity.getId());").append(r(1));
        sb.append(t(2)).append("if (row > 0) {").append(r(1));
        sb.append(t(3)).append("return true;").append(r(1));
        sb.append(t(2)).append("} else {").append(r(1));
        sb.append(t(3)).append("return false;").append(r(1));
        sb.append(t(2)).append("}").append(r(1));
        sb.append(t(1)).append("}").append(r(1));
        return sb.toString();
    }

    /**
     * 生成实现 public int remove(String qlString) throws SQLException, Exception;
     * 方法的代码
     *
     * @param cls
     * @return
     */
    public String getRemove2(Class cls) {
        StringBuilder sb = new StringBuilder();
        sb.append(r(1));
        sb.append(t(1)).append("@Override").append(r(1));
        sb.append(t(1)).append("public int remove(String qlString) throws SQLException, Exception {").append(r(1));
        sb.append(t(2)).append("return this.remove(qlString, null);").append(r(1));
        sb.append(t(1)).append("}").append(r(1));
        return sb.toString();
    }

    /**
     * 生成实现 public int remove(String qlString, ArrayList<QueryParam> params)
     * throws SQLException, Exception; 方法的代码
     *
     * @param cls
     * @return
     */
    public String getRemove3(Class cls) {
        StringBuilder sb = new StringBuilder();
        String tbName = getTableName(cls);
        sb.append(r(1));
        sb.append(t(1)).append("@Override").append(r(1));
        sb.append(t(1)).append("public int remove(String qlString, ArrayList<QueryParam> params)").append(r(1));
        sb.append(t(3)).append(" throws SQLException, Exception {").append(r(1));
        sb.append(t(2)).append("int row = -1;").append(r(1));
        sb.append(t(2)).append("StringBuilder sql = new StringBuilder();").append(r(1));
        sb.append(t(2)).append("if (qlString.trim().toLowerCase().startsWith(\"where\")) {").append(r(1));
        sb.append(t(3)).append("sql.append(\"DELETE FROM \").append(\"")
                .append(tbName).append(" \");").append(r(1));
        sb.append(t(2)).append("}").append(r(1));
        sb.append(t(2)).append("sql.append(qlString);").append(r(1));
        sb.append(t(2)).append("PreparedStatement pstmt = null;").append(r(1));
        sb.append(t(2)).append("try {").append(r(1));
        sb.append(t(3)).append("pstmt = con.prepareStatement(sql.toString());").append(r(1));
        sb.append(t(3)).append("if (params != null && params.size() > 0) {").append(r(1));
        sb.append(t(4)).append("for (QueryParam param : params) {").append(r(1));
        sb.append(t(5)).append("if (param.getValue() instanceof Date) {").append(r(1));
        sb.append(t(6)).append("if (TemporalType.TIMESTAMP.equals(param.getTemporalType())) {").append(r(1));
        sb.append(t(7));
        sb.append("pstmt.setTimestamp((Integer)param.getPosition(), new Timestamp(((Date)param.getValue()).getTime()));").append(r(1));
        sb.append(t(6)).append("} else if (TemporalType.TIME.equals(param.getTemporalType())) {").append(r(1));
        sb.append(t(7));
        sb.append("pstmt.setTime((Integer)param.getPosition(), new Time(((Date)param.getValue()).getTime()));").append(r(1));
        sb.append(t(6)).append("} else if (TemporalType.DATE.equals(param.getTemporalType())) {").append(r(1));
        sb.append(t(7)).append("pstmt.setDate((Integer)param.getPosition(), new java.sql.Date(((Date)param.getValue()).getTime()));").append(r(1));
        sb.append(t(6)).append("}").append(r(1));
        sb.append(t(5)).append("} else if (param.getValue() instanceof Calendar) {").append(r(1));
        sb.append(t(6)).append("if (TemporalType.TIMESTAMP.equals(param.getTemporalType())) {").append(r(1));
        sb.append(t(7)).append("pstmt.setTimestamp((Integer)param.getPosition(), new Timestamp(((Calendar)param.getValue()).getTimeInMillis()), (Calendar)param.getValue());").append(r(1));
        sb.append(t(6)).append("} else if (TemporalType.TIME.equals(param.getTemporalType())) {").append(r(1));
        sb.append(t(7)).append("pstmt.setTime((Integer)param.getPosition(), new Time(((Calendar)param.getValue()).getTimeInMillis()), (Calendar)param.getValue());").append(r(1));
        sb.append(t(6)).append("} else if (TemporalType.DATE.equals(param.getTemporalType())) {").append(r(1));
        sb.append(t(7)).append("pstmt.setDate((Integer)param.getPosition(), new java.sql.Date(((Calendar)param.getValue()).getTimeInMillis()), (Calendar)param.getValue());").append(r(1));
        sb.append(t(6)).append("}").append(r(1));
        sb.append(t(5)).append("} else if (param.getValue() instanceof String) {").append(r(1));
        sb.append(t(6)).append("pstmt.setString((Integer)param.getPosition(), (String)param.getValue());").append(r(1));
        sb.append(t(5)).append("} else if (param.getValue() instanceof Integer) {").append(r(1));
        sb.append(t(6)).append("pstmt.setInt((Integer)param.getPosition(), (Integer)param.getValue());").append(r(1));
        sb.append(t(5)).append("} else if (param.getValue() instanceof Long) {").append(r(1));
        sb.append(t(6)).append("pstmt.setLong((Integer)param.getPosition(), (Long)param.getValue());").append(r(1));
        sb.append(t(5)).append("} else if (param.getValue() instanceof Boolean) {").append(r(1));
        sb.append(t(6)).append("pstmt.setBoolean((Integer)param.getPosition(), (Boolean)param.getValue());").append(r(1));
        sb.append(t(5)).append("} else if (param.getValue() instanceof Double) {").append(r(1));
        sb.append(t(6)).append("pstmt.setDouble((Integer)param.getPosition(), (Double)param.getValue());").append(r(1));
        sb.append(t(5)).append("} else {").append(r(1));
        sb.append(t(6)).append("pstmt.setObject((Integer)param.getPosition(), param.getValue());").append(r(1));
        sb.append(t(5)).append("}").append(r(1));
        sb.append(t(4)).append("}").append(r(1));
        sb.append(t(3)).append("}").append(r(1));
        sb.append(t(3)).append("row = pstmt.executeUpdate();").append(r(1));
        sb.append(t(2)).append("} catch (SQLException sqlex) {").append(r(1));
        sb.append(t(3)).append("logger.error(sqlex.getMessage());").append(r(1));
        sb.append(t(3)).append("throw sqlex;").append(r(1));
        sb.append(t(2)).append("} catch (Exception ex) {").append(r(1));
        sb.append(t(3)).append("logger.error(ex.getMessage());").append(r(1));
        sb.append(t(3)).append("throw ex;").append(r(1));
        sb.append(t(2)).append("} finally {").append(r(1));
        sb.append(t(3)).append("pstmt.close();").append(r(1));
        sb.append(t(2)).append("}").append(r(1));
        sb.append(t(2)).append("return row;").append(r(1));
        sb.append(t(1)).append("}").append(r(1));
        return sb.toString();
    }

    /**
     * 生成 public List getList() throws SQLException, Exception; 方法的代码
     *
     * @param cls
     * @return
     */
    public String getList1(Class cls) {
        StringBuilder sb = new StringBuilder();
        sb.append(r(1));
        sb.append(t(1)).append("@Override").append(r(1));
        sb.append(t(1)).append("public List getList() throws SQLException, Exception {").append(r(1));
        sb.append(t(2)).append("return this.getList(\"\", null, 0, -1);").append(r(1));
        sb.append(t(1)).append("}").append(r(1));
        return sb.toString();
    }

    /**
     * 生成 public List getList(String qlString) throws SQLException, Exception;
     * 方法的代码
     *
     * @param cls
     * @return
     */
    public String getList2(Class cls) {
        StringBuilder sb = new StringBuilder();
        sb.append(r(1));
        sb.append(t(1)).append("@Override").append(r(1));
        sb.append(t(1)).append("public List getList(String qlString) throws SQLException, Exception {").append(r(1));
        sb.append(t(2)).append("return this.getList(qlString, null, 0, -1);").append(r(1));
        sb.append(t(1)).append("}").append(r(1));
        return sb.toString();
    }

    /**
     * 生成 public List getList(String qlString, long start, int counts) throws
     * SQLException, Exception; 方法的代码
     *
     * @param cls
     * @return
     */
    public String getList3(Class cls) {
        StringBuilder sb = new StringBuilder();
        sb.append(r(1));
        sb.append(t(1)).append("@Override").append(r(1));
        sb.append(t(1)).append("public List getList(String qlString, long start, int counts)").append(r(1));
        sb.append(t(3)).append("throws SQLException, Exception {").append(r(1));
        sb.append(t(2)).append("return this.getList(qlString, null, start, counts);").append(r(1));
        sb.append(t(1)).append("}").append(r(1));
        return sb.toString();
    }

    /**
     * 生成 public List getList(String qlString, ArrayList<QueryParam> params)
     * throws SQLException, Exception; 方法的代码
     *
     * @param cls
     * @return
     */
    public String getList4(Class cls) {
        StringBuilder sb = new StringBuilder();
        sb.append(r(1));
        sb.append(t(1)).append("@Override").append(r(1));
        sb.append(t(1)).append("public List getList(String qlString, ArrayList<QueryParam> params)").append(r(1));
        sb.append(t(3)).append("throws SQLException, Exception {").append(r(1));
        sb.append(t(2)).append("return this.getList(qlString, params, 0, -1);").append(r(1));
        sb.append(t(1)).append("}").append(r(1));
        return sb.toString();
    }

    /**
     * 生成 public long getTotal() throws SQLException, Exception; 方法的代码
     *
     * @param cls
     * @return
     */
    public String getTotal1(Class cls) {
        StringBuilder sb = new StringBuilder();
        sb.append(r(1));
        sb.append(t(1)).append("@Override").append(r(1));
        sb.append(t(1)).append("public long getTotal() throws SQLException, Exception {").append(r(1));
        sb.append(t(2)).append("return this.getTotal(\"\", null);").append(r(1));
        sb.append(t(1)).append("}").append(r(1));
        return sb.toString();
    }

    /**
     * 生成 public long getTotal(String qlString) throws SQLException, Exception;
     * 方法的代码
     *
     * @param cls
     * @return
     */
    public String getTotal2(Class cls) {
        StringBuilder sb = new StringBuilder();
        sb.append(r(1));
        sb.append(t(1)).append("@Override").append(r(1));
        sb.append(t(1)).append("public long getTotal(String qlString) throws SQLException, Exception {").append(r(1));
        sb.append(t(2)).append("return this.getTotal(qlString, null);").append(r(1));
        sb.append(t(1)).append("}").append(r(1));
        return sb.toString();
    }

    /**
     * 生成 public long getTotal(String qlString, ArrayList<QueryParam> params)
     * throws SQLException, Exception; 方法的代码
     *
     * @param cls
     * @return
     */
    public String getTotal3(Class cls) {
        StringBuilder sb = new StringBuilder();
        sb.append(r(1));
        sb.append(t(1)).append("@Override").append(r(1));
        String tbName = getTableName(cls);
        sb.append(t(1)).append("public long getTotal(String qlString, ArrayList<QueryParam> params)").append(r(1));
        sb.append(t(3)).append("throws SQLException, Exception {").append(r(1));
        sb.append(t(2)).append("long total = -1;").append(r(1));
        
        sb.append(t(2)).append("StringBuilder sql = new StringBuilder();").append(r(1));
        sb.append(t(2)).append("if (qlString.trim().length() == 0) {").append(r(1));
        sb.append(t(3)).append("sql.append(\"SELECT count(*) as total FROM ").append(tbName).append(" \");")
                .append(r(1));
        sb.append(t(2)).append("} else {").append(r(1));
        sb.append(t(3)).append("if (qlString.trim().toLowerCase().startsWith(\"where\")) {")
                .append(r(1));
        sb.append(t(4)).append("sql.append(\"SELECT count(*) as total FROM ").append(tbName).append(" \");")
                .append(r(1));
        sb.append(t(4)).append("sql.append(qlString);").append(r(1));
        sb.append(t(3)).append("} else {").append(r(1));
        sb.append(t(4)).append("sql.append(qlString);").append(r(1));
        sb.append(t(3)).append("}").append(r(1));
        sb.append(t(2)).append("}").append(r(1));
        sb.append(t(2)).append("if (params != null && params.size() > 0) {").append(r(1));
        sb.append(t(3)).append("PreparedStatement pstmt = null;").append(r(1));
        sb.append(t(3)).append("try {").append(r(1));
        sb.append(t(4)).append("pstmt = con.prepareStatement(sql.toString());").append(r(1));
        sb.append(t(4)).append("for (QueryParam param : params) {").append(r(1));
        sb.append(t(5)).append("if (param.getValue() instanceof Date) {").append(r(1));
        sb.append(t(6)).append("if (TemporalType.TIMESTAMP.equals(param.getTemporalType())) {").append(r(1));
        sb.append(t(7));
        sb.append("pstmt.setTimestamp((Integer)param.getPosition(), new Timestamp(((Date)param.getValue()).getTime()));").append(r(1));
        sb.append(t(6));
        sb.append("} else if (TemporalType.TIME.equals(param.getTemporalType())) {").append(r(1));
        sb.append(t(7));
        sb.append("pstmt.setTime((Integer)param.getPosition(), new Time(((Date)param.getValue()).getTime()));").append(r(1));
        sb.append(t(6));
        sb.append("} else if (TemporalType.DATE.equals(param.getTemporalType())) {").append(r(1));
        sb.append(t(7));
        sb.append("pstmt.setDate((Integer)param.getPosition(), new java.sql.Date(((Date)param.getValue()).getTime()));").append(r(1));
        sb.append(t(6));
        sb.append("}").append(r(1));
        sb.append(t(5));
        sb.append("} else if (param.getValue() instanceof Calendar) {").append(r(1));
        sb.append(t(6));
        sb.append("if (TemporalType.TIMESTAMP.equals(param.getTemporalType())) {").append(r(1));
        sb.append(t(7));
        sb.append("pstmt.setTimestamp((Integer)param.getPosition(), new Timestamp(((Calendar)param.getValue()).getTimeInMillis()), (Calendar)param.getValue());").append(r(1));
        sb.append(t(6));
        sb.append("} else if (TemporalType.TIME.equals(param.getTemporalType())) {").append(r(1));
        sb.append(t(7));
        sb.append("pstmt.setTime((Integer)param.getPosition(), new Time(((Calendar)param.getValue()).getTimeInMillis()), (Calendar)param.getValue());").append(r(1));
        sb.append(t(6));
        sb.append("} else if (TemporalType.DATE.equals(param.getTemporalType())) {").append(r(1));
        sb.append(t(7));
        sb.append("pstmt.setDate((Integer)param.getPosition(), new java.sql.Date(((Calendar)param.getValue()).getTimeInMillis()), (Calendar)param.getValue());").append(r(1));
        sb.append(t(6));
        sb.append("}").append(r(1));
        sb.append(t(5));
        sb.append("} else if (param.getValue() instanceof String) {").append(r(1));
        sb.append(t(6)).append("pstmt.setString((Integer)param.getPosition(), (String)param.getValue());").append(r(1));
        sb.append(t(5)).append("} else if (param.getValue() instanceof Integer) {").append(r(1));
        sb.append(t(6)).append("pstmt.setInt((Integer)param.getPosition(), (Integer)param.getValue());").append(r(1));
        sb.append(t(5)).append("} else if (param.getValue() instanceof Long) {").append(r(1));
        sb.append(t(6)).append("pstmt.setLong((Integer)param.getPosition(), (Long)param.getValue());").append(r(1));
        sb.append(t(5)).append("} else if (param.getValue() instanceof Boolean) {").append(r(1));
        sb.append(t(6)).append("pstmt.setBoolean((Integer)param.getPosition(), (Boolean)param.getValue());").append(r(1));
        sb.append(t(5)).append("} else if (param.getValue() instanceof Double) {").append(r(1));
        sb.append(t(6)).append("pstmt.setDouble((Integer)param.getPosition(), (Double)param.getValue());").append(r(1));
        sb.append(t(5)).append("} else {").append(r(1));
        sb.append(t(6)).append("pstmt.setObject((Integer)param.getPosition(), param.getValue());").append(r(1));
        sb.append(t(5)).append("}").append(r(1));
        sb.append(t(4)).append("}").append(r(1));
        sb.append(t(4)).append("ResultSet rs = pstmt.executeQuery();").append(r(1));
        sb.append(t(4)).append("if (rs.next()) {").append(r(1));
        sb.append(t(5)).append(" total = rs.getLong(\"total\");").append(r(1));
        sb.append(t(4)).append("}").append(r(1));
        sb.append(t(3)).append("} catch (SQLException sqlex) {").append(r(1));
        sb.append(t(4)).append("logger.error(sqlex.getMessage());").append(r(1));
        sb.append(t(4)).append("throw sqlex;").append(r(1));
        sb.append(t(3)).append("} catch (Exception ex) {").append(r(1));
        sb.append(t(4)).append("logger.error(ex.getMessage());").append(r(1));
        sb.append(t(4)).append("throw ex;").append(r(1));
        sb.append(t(3)).append("} finally {").append(r(1));
        sb.append(t(4)).append("pstmt.close();").append(r(1));
        sb.append(t(3)).append("}").append(r(1));
        sb.append(t(3)).append("return total;").append(r(1));
        sb.append(t(2)).append("}").append(r(1));
        sb.append(t(2)).append("Statement stmt = null;").append(r(1));
        sb.append(t(2)).append("try {").append(r(1));
        sb.append(t(3)).append("stmt = con.createStatement();").append(r(1));
        sb.append(t(3)).append("ResultSet rs = stmt.executeQuery(sql.toString());").append(r(1));
        sb.append(t(3)).append("if (rs.next()) {").append(r(1));
        sb.append(t(4)).append("total = rs.getLong(\"total\");").append(r(1));
        sb.append(t(3)).append("}").append(r(1));
        sb.append(t(2)).append("} catch (SQLException sqlex) {").append(r(1));
        sb.append(t(3)).append("logger.error(sqlex.getMessage());").append(r(1));
        sb.append(t(3)).append("throw sqlex;").append(r(1));
        sb.append(t(2)).append("} catch (Exception ex) {").append(r(1));
        sb.append(t(3)).append("logger.error(ex.getMessage());").append(r(1));
        sb.append(t(3)).append("throw ex;").append(r(1));
        sb.append(t(2)).append("} finally {").append(r(1));
        sb.append(t(3)).append("stmt.close();").append(r(1));
        sb.append(t(2)).append("}").append(r(1));
        sb.append(t(2)).append("return total;").append(r(1));
        sb.append(t(1)).append("}").append(r(1));
        return sb.toString();
    }
    
    /**
     * 获取一个Javabean映射的数据表的名称，如果有@Table或者@View注释，并且name长度大于0则为
     * 注释的长度，否则为类的名称。
     * @param cls 持久化Bean或者视图Bean的class
     * @return 返回数据表的名称
     */
    public String getTableName(Class cls) {
        return ClassUtil.getTableName(cls);
    }

    /**
     * 生成 
     * public int update(String qlString) throws SQLException, Exception;
     * 方法的代码
     *
     * @param cls
     * @return
     */
    public String getUpdate1(Class cls) {
        StringBuilder sb = new StringBuilder();
        sb.append(r(1));
        sb.append(t(1)).append("@Override").append(r(1));
        sb.append(t(1)).append("public int update(String qlString) throws SQLException, Exception {").append(r(1));
        sb.append(t(2)).append("return this.update(qlString, null);").append(r(1));
        sb.append(t(1)).append("}").append(r(1));
        return sb.toString();
    }

    /**
     * 生成 
     * public int update(String qlString, ArrayList<QueryParam> params)
     * throws SQLException, Exception; 
     * 方法的代码
     * @param cls
     * @return
     */
    public String getUpdate2(Class cls) {
        StringBuilder sb = new StringBuilder();
        sb.append(r(1));
        sb.append(t(1)).append("@Override").append(r(1));
        sb.append(t(1)).append("public int update(String qlString, ArrayList<QueryParam> params)").append(r(1));
        sb.append(t(3)).append("throws SQLException, Exception {").append(r(1));
        sb.append(t(2)).append("int row = -1;").append(r(1));
        sb.append(t(2)).append("if (!qlString.trim().toLowerCase().startsWith(\"update\")) {").append(r(1));
        sb.append(t(3)).append("return row;").append(r(1));
        sb.append(t(2)).append("}").append(r(1));
        sb.append(t(2)).append("PreparedStatement pstmt = null;").append(r(1));
        sb.append(t(2)).append("try {").append(r(1));
        sb.append(t(3)).append("pstmt = con.prepareStatement(qlString);").append(r(1));
        
        sb.append(t(3)).append("if (params != null && params.size() > 0) {").append(r(1));
        sb.append(t(4)).append("for (QueryParam param : params) {").append(r(1));
        sb.append(t(5)).append("if (param.getValue() instanceof Date) {").append(r(1));
        sb.append(t(6)).append("if (TemporalType.TIMESTAMP.equals(param.getTemporalType())) {").append(r(1));
        sb.append(t(7));
        sb.append("pstmt.setTimestamp((Integer)param.getPosition(), new Timestamp(((Date)param.getValue()).getTime()));").append(r(1));
        sb.append(t(6)).append("} else if (TemporalType.TIME.equals(param.getTemporalType())) {").append(r(1));
        sb.append(t(7));
        sb.append("pstmt.setTime((Integer)param.getPosition(), new Time(((Date)param.getValue()).getTime()));").append(r(1));
        sb.append(t(6)).append("} else if (TemporalType.DATE.equals(param.getTemporalType())) {").append(r(1));
        sb.append(t(7)).append("pstmt.setDate((Integer)param.getPosition(), new java.sql.Date(((Date)param.getValue()).getTime()));").append(r(1));
        sb.append(t(6)).append("}").append(r(1));
        sb.append(t(5)).append("} else if (param.getValue() instanceof Calendar) {").append(r(1));
        sb.append(t(6)).append("if (TemporalType.TIMESTAMP.equals(param.getTemporalType())) {").append(r(1));
        sb.append(t(7)).append("pstmt.setTimestamp((Integer)param.getPosition(), new Timestamp(((Calendar)param.getValue()).getTimeInMillis()), (Calendar)param.getValue());").append(r(1));
        sb.append(t(6)).append("} else if (TemporalType.TIME.equals(param.getTemporalType())) {").append(r(1));
        sb.append(t(7)).append("pstmt.setTime((Integer)param.getPosition(), new Time(((Calendar)param.getValue()).getTimeInMillis()), (Calendar)param.getValue());").append(r(1));
        sb.append(t(6)).append("} else if (TemporalType.DATE.equals(param.getTemporalType())) {").append(r(1));
        sb.append(t(7)).append("pstmt.setDate((Integer)param.getPosition(), new java.sql.Date(((Calendar)param.getValue()).getTimeInMillis()), (Calendar)param.getValue());").append(r(1));
        sb.append(t(6)).append("}").append(r(1));
        sb.append(t(5)).append("} else if (param.getValue() instanceof String) {").append(r(1));
        sb.append(t(6)).append("pstmt.setString((Integer)param.getPosition(), (String)param.getValue());").append(r(1));
        sb.append(t(5)).append("} else if (param.getValue() instanceof Integer) {").append(r(1));
        sb.append(t(6)).append("pstmt.setInt((Integer)param.getPosition(), (Integer)param.getValue());").append(r(1));
        sb.append(t(5)).append("} else if (param.getValue() instanceof Long) {").append(r(1));
        sb.append(t(6)).append("pstmt.setLong((Integer)param.getPosition(), (Long)param.getValue());").append(r(1));
        sb.append(t(5)).append("} else if (param.getValue() instanceof Boolean) {").append(r(1));
        sb.append(t(6)).append("pstmt.setBoolean((Integer)param.getPosition(), (Boolean)param.getValue());").append(r(1));
        sb.append(t(5)).append("} else if (param.getValue() instanceof Double) {").append(r(1));
        sb.append(t(6)).append("pstmt.setDouble((Integer)param.getPosition(), (Double)param.getValue());").append(r(1));
        sb.append(t(5)).append("} else {").append(r(1));
        sb.append(t(6)).append("pstmt.setObject((Integer)param.getPosition(), param.getValue());").append(r(1));
        sb.append(t(5)).append("}").append(r(1));
        sb.append(t(4)).append("}").append(r(1));
        sb.append(t(3)).append("}").append(r(1));
        sb.append(t(3)).append("row = pstmt.executeUpdate();").append(r(1));
        sb.append(t(2)).append("} catch (SQLException sqlex) {").append(r(1));
        sb.append(t(3)).append("logger.error(sqlex.getMessage());").append(r(1));
        sb.append(t(3)).append("throw sqlex;").append(r(1));
        sb.append(t(2)).append("} catch (Exception ex) {").append(r(1));
        sb.append(t(3)).append("logger.error(ex.getMessage());").append(r(1));
        sb.append(t(3)).append("throw ex;").append(r(1));
        sb.append(t(2)).append("} finally {").append(r(1));
        sb.append(t(3)).append("pstmt.close();").append(r(1));
        sb.append(t(2)).append("}").append(r(1));
        sb.append(t(2)).append("return row;").append(r(1));
        sb.append(t(1)).append("}").append(r(1));
        return sb.toString();
    }
    
    /**
     * 检查持久化的Javabean是否符合规则
     * @param cls
     * @throws EntityException 
     */
    public void checkEntity(Class cls) throws EntityException {
        List<FieldInfo>  fields  = ClassUtil.getFields(cls);
        if (fields == null || fields.isEmpty()) {
            throw new EntityException("No field has defined!");
        }
        if (fields.size() <= 1) {
            throw new EntityException("Cann't less 2 fields!");
        }
        List<MethodInfo> methods = ClassUtil.getMethods(cls);
        HashMap<String, MethodInfo> aMethod = new HashMap<String, MethodInfo>();
        ArrayList<String> ms = new ArrayList<String>();
        for (MethodInfo minfo : methods) {
            if (ms.contains(minfo.getName())) {
                throw new EntityException(minfo.getName() + " has define!");
            }
            aMethod.put(minfo.getName(), minfo);
        }
        boolean isPriKey = false;
        for (FieldInfo finfo : fields) {
            Annotation[] anns = finfo.getAnnotations();
            if (anns != null) {
                for (int i=0;i<anns.length;i++) {
                    if (anns[i] instanceof Id) {
                        isPriKey = true;
                    }
                }
            }
            if (finfo.getName().contains("id")) {
                isPriKey = true;
            }
            String getK = "get" + finfo.getName().substring(0,1)
                    .toUpperCase() + finfo.getName().substring(1);
            MethodInfo getM = aMethod.get(getK);
            if (getM == null) {
                throw new EntityException(getK + " method has not defined!");
            } else {
                if (getM.getParams() != null && getM.getParams().length != 0) {
                    throw new EntityException(getK + " method cann't has params!");
                }
            }
            String setK = "set" + finfo.getName().substring(0,1)
                    .toUpperCase() + finfo.getName().substring(1);
            MethodInfo setM = aMethod.get(setK);
            if (setM == null) {
                throw new EntityException(setK + " method has not defined!");
            } else {
                if (setM.getParams() == null && setM.getParams().length != 1) {
                    throw new EntityException(setK + " method params type error!");
                } else {
                    if (!setM.getParams()[0].equals(finfo.getType())) {
                        throw new EntityException(setK + " method params type error!");
                    }
                }
            }
        }
        if (!isPriKey) {
            throw new EntityException("No Id field define!");
        }
    }
    
    /**
     * 根据类属性变量获取该属性映射的数据表的字段名
     * @return 
     */
    public String getDataField(FieldInfo finfo) {
        String name = finfo.getName();
        if (finfo.getAnnotations() != null) {
            Annotation[] anns = finfo.getAnnotations();
            for (int i=0;i<anns.length;i++) {
                if (anns[i] instanceof Column) {
                    if (((Column)anns[i]).name().trim().length() > 0) {
                        name = ((Column)anns[i]).name();
                    }
                }
            }
        }
        return name;
    }
    
}
