/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.easyea.edao.builders;

import com.easyea.edao.Builder;
import com.easyea.edao.DaoManager;
import com.easyea.edao.DbProductName;
import com.easyea.edao.annotation.GeneratedValue;
import com.easyea.edao.annotation.GenerationType;
import com.easyea.edao.annotation.Id;
import com.easyea.edao.annotation.IdGeneration;
import com.easyea.edao.annotation.SequenceGenerator;
import com.easyea.edao.annotation.Temporal;
import com.easyea.edao.codetpls.WebitTemplate;
import com.easyea.edao.exception.EntityException;
import com.easyea.edao.exception.ViewException;
import com.easyea.edao.util.ClassUtil;
import com.easyea.edao.util.TypeInfo;
import com.easyea.logger.Logger;
import com.easyea.logger.LoggerFactory;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 *
 * @author louis
 */
public abstract class AbstractBuilder implements Builder {
    
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    private final DaoManager manager;
    protected     Class      entityCls;
    protected List<String>        imps;
    protected Map<String, Field>  fields;
    protected Map<String, Method> methods;
    
    public AbstractBuilder(DaoManager manager) {
        this.manager   = manager;
        this.imps      = new ArrayList<String>();
        this.fields    = new HashMap<String, Field>();
        this.methods   = new HashMap<String, Method>();
    }
    
    @Override
    public DaoManager getManager() {
        return manager;
    }
    
    @Override
    public String getDaoCode(Class entityCls) 
            throws EntityException {
        this.entityCls = entityCls;
        Class partitionManager = manager.getPartitionManager(entityCls);
        logger.info("partitionManager=[{}]", partitionManager);
        String tableName = ClassUtil.getTableName(entityCls);
        String seqName = tableName + "_id_seq";
        initFields();
        Field idField = idField();
        GenerationType genType = GenerationType.SEQUENCE;
        Annotation[] ans = idField.getAnnotations();
        String gseqName = "";
        if (ans.length > 0) {
            for (Annotation idAn : ans) {
                if (idAn instanceof GeneratedValue) {
                    GeneratedValue gval = ((GeneratedValue)idAn);
                    genType = gval.strategy();
                    gseqName = gval.generator();
                }
            }
        }
        Annotation[] gAns = entityCls.getAnnotations();
        String idGeneration = "";
        switch (genType) {
            case AUTO:
                break;
            case SEQUENCE:
                if (gAns.length > 0) {
                    for (Annotation gAn : gAns) {
                        if (gAn instanceof SequenceGenerator) {
                            SequenceGenerator seqGen = (SequenceGenerator)gAn;
                            if (seqGen.name().equals(gseqName)) {
                                seqName = seqGen.sequenceName();
                            }
                        }
                    }
                }
                break;
            case IDENTITY:
                break;
            case GENERATION:
                if (gAns.length > 0) {
                    for (Annotation gAn : gAns) {
                        if (gAn instanceof IdGeneration) {
                            IdGeneration idan = (IdGeneration)gAn;
                            idGeneration = idan.value();
                        }
                    }
                }
                break;
        }
        WebitTemplate tpl = new WebitTemplate();
        
        List<String> columns = new ArrayList<String>();
        Map<String, TypeInfo> fieldTypes = new HashMap<String, TypeInfo>();
        String idColumeName = "";
        if (fields != null && !fields.isEmpty()) {
            String mName;
            boolean isId;
            for (Map.Entry<String, Field> entry : fields.entrySet()) {
                Field f = entry.getValue();
                String column = ClassUtil.getColumnName(f);
                columns.add(column.toLowerCase(Locale.ENGLISH));
                mName = f.getName().substring(0, 1).toUpperCase(Locale.ENGLISH)
                        + f.getName().substring(1);
                String setMethod = ClassUtil.typeToJdbc(f);
                isId = f.getName().equals(idField.getName());
                if (isId) {
                    idColumeName = column;
                }
                TypeInfo ti = new TypeInfo(setMethod, mName, f.getType(), isId);
                if (f.getType().equals(Date.class)) {
                    Annotation[] atns = f.getAnnotations();
                    for (Annotation atn : atns) {
                        if (atn instanceof Temporal) {
                            ti.setTemporal(((Temporal)atn).value().name());
                        }
                    }
                    if (ti.getTemporal() == null || ti.getTemporal().isEmpty()) {
                        ti.setTemporal("TIMESTAMP");
                    }
                }
                fieldTypes.put(column.toLowerCase(Locale.ENGLISH), ti);
            }
        }
        Collections.sort(columns);
        Map<String, Object> context = new HashMap<String, Object>();
        String tplPath = "/codetpl/method/SequencePersistMethod.etpl";
        if (this.getDbProductName() == DbProductName.Mysql) {
            tplPath = "/codetpl/method/IdentityPersistMethod.etpl";
        }
        context.put("daoPackPre", ClassUtil.daoPackPre);
        context.put("entityPackage", entityCls.getPackage().getName());
        context.put("entity", entityCls);
        context.put("columns", columns);
        context.put("tableName", tableName);
        context.put("nextIdSql", this.getNextIdSql(seqName));
        context.put("generationType", genType);
        context.put("fieldTypes", fieldTypes);
        context.put("idColumnName", ClassUtil.getColumnName(idField));
        context.put("limitFun", getLimitSQLFun());
        if (partitionManager != null) {
            context.put("hasPartition", "1");
        } else {
            context.put("hasPartition", "0");
        }
        
        
        String persistMethod = "";
        try {
            persistMethod = tpl.render(tplPath, context);
        } catch (Exception ex) {
            throw new EntityException(ex);
        }
        context.put("persistMethod", persistMethod);
        
        persistMethod = "";
        tplPath = "/codetpl/method/SequencePersistsMethod.etpl";
        if (this.getDbProductName() == DbProductName.Mysql) {
            tplPath = "/codetpl/method/IdentityPersistsMethod.etpl";
        }
        try {
            persistMethod = tpl.render(tplPath, context);
        } catch (Exception ex) {
            throw new EntityException(ex);
        }
        context.put("persistsMethod", persistMethod);
        
        tplPath = "/codetpl/EntityDao.etpl";
        String code;
        try {
            code = tpl.render(tplPath, context);
        } catch (Exception ex) {
            throw new EntityException(ex);
        }
        System.out.println("code:\n" + code);
        return code;
    }
    
    @Override
    public String getViewDaoCode(Class entityCls) throws ViewException {
        this.entityCls = entityCls;
        String code = "";
        String tableName = ClassUtil.getTableName(entityCls);
        initFields();
        
        List<String> columns = new ArrayList<String>();
        Map<String, TypeInfo> fieldTypes = new HashMap<String, TypeInfo>();
        if (fields != null && !fields.isEmpty()) {
            String mName;
            for (Map.Entry<String, Field> entry : fields.entrySet()) {
                Field  f      = entry.getValue();
                String column = ClassUtil.getColumnName(f);
                columns.add(column.toLowerCase(Locale.ENGLISH));
                mName = f.getName().substring(0, 1).toUpperCase(Locale.ENGLISH)
                        + f.getName().substring(1);
                String setMethod = ClassUtil.typeToJdbc(f);
                TypeInfo ti = new TypeInfo(setMethod, mName, f.getType(), false);
                if (f.getType().equals(Date.class)) {
                    Annotation[] atns = f.getAnnotations();
                    for (Annotation atn : atns) {
                        if (atn instanceof Temporal) {
                            ti.setTemporal(((Temporal)atn).value().name());
                        }
                    }
                    if (ti.getTemporal() == null || ti.getTemporal().isEmpty()) {
                        ti.setTemporal("TIMESTAMP");
                    }
                }
                fieldTypes.put(column.toLowerCase(Locale.ENGLISH), ti);
            }
        }
        Collections.sort(columns);
        
        Map<String, Object> context = new HashMap<String, Object>();
        String tplPath = "/codetpl/ViewDao.etpl";
        context.put("vdaoPackPre", ClassUtil.vdaoPackPre);
        context.put("entityPackage", entityCls.getPackage().getName());
        context.put("entity", entityCls);
        context.put("columns", columns);
        context.put("tableName", tableName);
        context.put("fieldTypes", fieldTypes);
        context.put("limitFun", getLimitSQLFun());
        
        WebitTemplate tpl = new WebitTemplate();
        try {
            code = tpl.render(tplPath, context);
        } catch (Exception ex) {
            throw new ViewException(ex);
        }
        return code;
    }
    
    public String getMapDaoCode(final String dbProductName) throws Exception {
        String code = "";
        String dbName = dbProductName.substring(0, 1)
                .toUpperCase(Locale.ENGLISH) + dbProductName.substring(1);
        Map<String, Object> context = new HashMap<String, Object>();
        String tplPath = "/codetpl/MapDao.etpl";
        context.put("mapdaoPackage", ClassUtil.MAPDAO_PACKAGE);
        context.put("dbProductName", dbName);
        context.put("limitFun", getLimitSQLFun());
        
        WebitTemplate tpl = new WebitTemplate();
        try {
            code = tpl.render(tplPath, context);
        } catch (Exception ex) {
            throw new ViewException(ex);
        }
        return code;
    }
    
    /**
     * 获取持久化Bean的主键的Field信息，如果没有发现有主键的Field则抛出错误
     * @return 主键的Field信息
     * @throws EntityException 
     */
    public Field idField() throws EntityException {
        initFields();
        Field idField = null;
        for (Map.Entry<String, Field> f : fields.entrySet()) {
            if (f.getKey().equals("id")) {
                idField = f.getValue();
            } else {
                Annotation[] ans = f.getValue().getAnnotations();
                if (ans.length > 0) {
                    for (Annotation an : ans) {
                        if (an instanceof Id) {
                            idField = f.getValue();
                        }
                    }
                }
            }
        }
        if (idField == null) {
            throw new EntityException("not found id field!");
        }
        return idField;
    }
    
    protected abstract String getNextIdSql(String seqName);
    
    protected abstract String getLimitSQLFun();
    
    /**
     * 初始化一个bean中定义的所有的Field信息，包含器父类声明的信息
     */
    protected void initFields() {
        if (!fields.isEmpty()) {
            return;
        }
        List<Field> fs = ClassUtil.getFields(entityCls);
            if (fs != null && !fs.isEmpty()) {
            for (Field f : fs) {
                fields.put(f.getName(), f);
            }
        }
    }
    /**
     * 初始化一个Bean所声明的所有的Method包含其所有父类的方法
     */
    protected void initMethods() {
        if (!methods.isEmpty()) {
            return;
        }
        List<Method> ms = ClassUtil.getMethodList(entityCls);
        if (ms != null && !ms.isEmpty()) {
            for (Method m : ms) {
                methods.put(m.getName(), m);
            }
        }
    }
}
