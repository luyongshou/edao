/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.easyea.edao.managers;

import com.easyea.dynamic.DynamicClassLoader;
import com.easyea.dynamic.DynamicJavaFile;
import com.easyea.dynamic.DynamicJavaFileManager;
import com.easyea.edao.Builder;
import com.easyea.edao.DaoManager;
import com.easyea.edao.DbProductName;
import com.easyea.edao.Ddl;
import com.easyea.edao.DdlManager;
import com.easyea.edao.EntityFactory;
import com.easyea.edao.MapDao;
import com.easyea.edao.MapFactory;
import com.easyea.edao.ViewFactory;
import com.easyea.edao.annotation.Partition;
import com.easyea.edao.annotation.partition.NumberRangePartition;
import com.easyea.edao.annotation.partition.TimeRangePartition;
import com.easyea.edao.codetpls.WebitTemplate;
import com.easyea.edao.ddls.MysqlDdl;
import com.easyea.edao.ddls.MysqlDdlManager;
import com.easyea.edao.ddls.OracleDdl;
import com.easyea.edao.ddls.OracleDdlManager;
import com.easyea.edao.ddls.PostgresqlDdl;
import com.easyea.edao.ddls.PostgresqlDdlManager;
import com.easyea.edao.exception.EntityException;
import com.easyea.edao.exception.ViewException;
import com.easyea.edao.partition.NumberRange;
import com.easyea.edao.partition.PartitionParam;
import com.easyea.edao.partition.TimeRange;
import com.easyea.edao.partition.Type;
import com.easyea.edao.util.ClassUtil;
import com.easyea.edao.util.JavaCode;
import com.easyea.internal.util.ClassUtils;
import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author louis
 */
public class DefaultManager implements DaoManager {
    
    private static final Logger logger = LoggerFactory.getLogger(DefaultManager.class);

    private static       DefaultManager instance = null;
    private static final ReentrantLock  lock     = new ReentrantLock();
    
    private static final ReentrantLock daolock        = new ReentrantLock();
    private static final ReentrantLock pmlock         = new ReentrantLock();
    private static final ReentrantLock vdaolock       = new ReentrantLock();
    private static final ReentrantLock mapdaolock     = new ReentrantLock();
    private static final ReentrantLock daofactlock    = new ReentrantLock();
    private static final ReentrantLock vdaofactlock   = new ReentrantLock();
    private static final ReentrantLock mapdaofactlock = new ReentrantLock();
    
    //private DynamicClassLoader loader;
    private ConcurrentHashMap<String, Class> cache = 
            new ConcurrentHashMap<String, Class>();
    private ConcurrentHashMap<Class, DynamicClassLoader> cacheLoader = 
            new ConcurrentHashMap<Class, DynamicClassLoader>();
    
    private DefaultManager() {
        /*
        final ClassLoader contextLoader = Thread.currentThread().getContextClassLoader();
        this.loader = AccessController.doPrivileged(
                new PrivilegedAction<DynamicClassLoader>() {
                    public DynamicClassLoader run() {
                        return new DynamicClassLoader(contextLoader);
                    }
                }
            );
        */
    }
    
    public static DefaultManager getInstance() {
        if (instance == null) {
            lock.lock();
            try {
                if (instance == null) {
                    instance = new DefaultManager();
                }
            } finally {
                lock.unlock();
            }
        }
        return instance;
    }
    
    @Override
    public Class getPartitionManager(Class entityCls) {
        Class pm = null;
        Annotation[] ans = entityCls.getAnnotations();
        boolean isp = false;
        logger.info("ans.length=[{}]", ans.length);
        if (ans.length > 0) {
            for (Annotation an : ans) {
                if (an instanceof Partition) {
                    isp = true;
                }
            }
        }
        logger.info("isp=[{}]", isp);
        if (isp) {
            List<Field> fields = ClassUtil.getFields(entityCls);
            Map<String, java.lang.reflect.Type> aType = 
                    new HashMap<String, java.lang.reflect.Type>();
            if (fields != null && !fields.isEmpty()) {
                for (Field f : fields) {
                    aType.put(f.getName(), f.getGenericType());
                    logger.info("[{}]=[{}]", f.getName(), f.getGenericType());
                }
            }
            
            for (Annotation an : ans) {
                if (an instanceof NumberRangePartition) {
                    NumberRangePartition numAn = (NumberRangePartition)an;
                    logger.info("numAn.field()=[{}]", numAn.field());
                    if (aType.containsKey(numAn.field())) {
                        NumberRange numRange = new NumberRange();
                        numRange.setCount(numAn.count());
                        numRange.setField(numAn.field());
                        numRange.setFieldType(pm);
                        numRange.setInterval(numAn.interval());
                        numRange.setType(Type.RANGE);
                        pm = this.getPartitionManager(entityCls, numRange);
                    }

                } else if (an instanceof TimeRangePartition) {
                    TimeRangePartition timeAn = (TimeRangePartition)an;
                    logger.info("timeAn.field()=[{}]", timeAn.field());
                    if (aType.containsKey(timeAn.field())) {
                        TimeRange timeRange = new TimeRange();
                        timeRange.setCount(timeAn.count());
                        timeRange.setField(timeAn.field());
                        timeRange.setFieldType(pm);
                        timeRange.setInterval(timeAn.interval());
                        timeRange.setType(Type.RANGE);
                        pm = this.getPartitionManager(entityCls, timeRange);
                    }
                }
            }
        }
        logger.info("pm=[{}]", pm);
        return pm;
    }
    
    private Class getPartitionManager(Class entityCls, PartitionParam param) {
        Class pm = null;
        WebitTemplate tpl = new WebitTemplate();
        Map<String, Object> context = new HashMap<String, Object>();
        context.put("entity", entityCls);
        String tmpPath = "";
        if (param instanceof TimeRange) {
            TimeRange trp = (TimeRange)param;
            String dateFormat    = "";
            String calendarField = "";
            String entityPackage = entityCls.getPackage().getName();
            switch (trp.getInterval()) {
                case TIME_YEAR:
                    if (trp.getCount() > 1) {
                        dateFormat = "";
                    } else {
                        dateFormat = "yyyy";
                    }
                    calendarField = "Calendar.YEAR";
                    break;
                case TIME_MONTH:
                    if (trp.getCount() > 1) {
                        dateFormat = "yyyy";
                    } else {
                        dateFormat = "yyyyMM";
                    }
                    calendarField = "Calendar.MONTH";
                    break;
                case TIME_DAY:
                    if (trp.getCount() > 1) {
                        dateFormat = "yyyyMM";
                    } else {
                        dateFormat = "yyyyMMdd";
                    }
                    calendarField = "Calendar.DAY_OF_MONTH";
                    break;
            }
            String method = "get" + trp.getField().substring(0, 1)
                    .toUpperCase(Locale.ENGLISH) + trp.getField().substring(1);
            tmpPath = "/codetpl/partition/TimeRangeManager.etpl";
            context.put("entityPackage", entityPackage);
            context.put("count", trp.getCount());
            context.put("dateFormat", dateFormat);
            context.put("calendarField", calendarField);
            context.put("method", method);
        } else if (param instanceof NumberRange) {
            NumberRange nrp = (NumberRange)param;
            String entityPackage = entityCls.getPackage().getName();
            String method = "get" + nrp.getField().substring(0, 1)
                    .toUpperCase(Locale.ENGLISH) + nrp.getField().substring(1);
            int count = nrp.getCount();
            context.put("entityPackage", entityPackage);
            context.put("count", count);
            context.put("method", method);
            tmpPath = "/codetpl/partition/NumberRangeManager.etpl";
        }
        String code = "";
        try {
            code = tpl.render(tmpPath, context);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        logger.info("code=[{}]", code);
        if (code.length() > 0) {
            JavaCode java = new JavaCode();
            java.setCode(code);
            java.setClassName(entityCls.getSimpleName() + "Manager");
            java.setPackName("partrm." + entityCls.getPackage().getName());
            try {
                DynamicClassLoader loader = this.getEntityLoader(entityCls);
                compilePartManager(java, loader);
                pm = loader.loadClass(java.getPackName() + "." + java.getClassName());
            } catch (Exception e) {
                logger.error("", e);
            }
        }
        return pm;
    }
    
    public static void main(String[] args) {
        System.out.println(DefaultManager.getMapFactoryCode(DbProductName.Oracle));
    }
    
    public DynamicClassLoader getEntityLoader(Class entityCls) {
        if (entityCls == null) {
            return null;
        }
        DynamicClassLoader loader = cacheLoader.get(entityCls);
        if (loader != null) {
            Thread.currentThread().setContextClassLoader(loader.getParent());
            return loader;
        }
        final ClassLoader entityLoader = entityCls.getClassLoader();
        
        loader = AccessController.doPrivileged(
                new PrivilegedAction<DynamicClassLoader>() {
                    public DynamicClassLoader run() {
                        return new DynamicClassLoader(entityLoader);
                    }
                }
            );
        if (loader != null) {
            Thread.currentThread().setContextClassLoader(loader.getParent());
            cacheLoader.put(entityCls, loader);
        }
        return loader;
    }

    @Override
    public Class getDaoClass(Class entityCls, Builder builder) 
            throws EntityException, Exception {
        String daoName = ClassUtil.daoPackPre + entityCls.getName() + "Dao";
        String cacheKey = builder.getDbProductName() + "_" + daoName;
        Class dao = cache.get(cacheKey);
        if (dao != null) {
            return dao;
        }
        DynamicClassLoader loader = this.getEntityLoader(entityCls);
        try {
            dao = loader.loadClass(daoName);
        } catch (ClassNotFoundException ex) {
            daolock.lock();
            try {
                compileDao (entityCls, loader, builder);
                try {
                    dao = loader.loadClass(daoName);
                    cache.put(cacheKey, dao);
                } catch (ClassNotFoundException e) {
                    throw e;
                }
            } finally {
                daolock.unlock();
            }
        }
        return dao;
    }
    
    public void compileDao(Class entityCls, DynamicClassLoader loader, 
            Builder builder) throws EntityException, Exception {
        if (entityCls == null) {
            throw new EntityException("Dao package name is null");
        }
        //Class ecls = getEntityClass(daoName);
        Class ecls = entityCls;
        String daoName = ClassUtil.daoPackPre + entityCls.getName() + "Dao";
        Class partmClass = null;
        try {
            partmClass = getPartManager(ecls, builder.getDbProductName());
        } catch (Exception e) {
            logger.error("获取分区表管理器失败", e);
        }
        String packName  = "";
        String shortName = "";
        int lastDot = daoName.lastIndexOf(".");
        if (lastDot <= 0) {
            throw new EntityException("Dao package name not well rule");
        }
        packName  = daoName.substring(0, lastDot);
        shortName = daoName.substring(lastDot+1);
        String source = builder.getDaoCode(ecls);
        if (logger.isDebugEnabled()) {
            logger.debug("{}'s java source [{}]", daoName, source);
        }
        DynamicJavaFile jfile = new DynamicJavaFile(shortName, source);
                
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null) {
            throw new IllegalStateException("Can not get system java compiler. Please add jdk tools.jar to your classpath.");
        }
        DiagnosticCollector<JavaFileObject> diagnosticCollector = new DiagnosticCollector<JavaFileObject>();
        StandardJavaFileManager manager = compiler.getStandardFileManager(diagnosticCollector, null, null);
        final ClassLoader contextLoader = Thread.currentThread().getContextClassLoader();
        ClassLoader cloader = contextLoader;
        List<File> files = new ArrayList<File>();
        while (cloader instanceof URLClassLoader 
                && (! loader.getClass().getName()
                    .equals("sun.misc.Launcher$AppClassLoader"))) {
            URLClassLoader urlClassLoader = (URLClassLoader) cloader;
            for (URL url : urlClassLoader.getURLs()) {
                files.add(new File(url.getFile()));
            }
            cloader = cloader.getParent();
        }
        if (files.size() > 0) {
            try {
                manager.setLocation(StandardLocation.CLASS_PATH, files);
            } catch (IOException e) {
                throw new IllegalStateException(e.getMessage(), e);
            }
        }

        List<String> options = new ArrayList<String>();
        DynamicJavaFileManager javaFileManager = new DynamicJavaFileManager(
                manager, loader);
        javaFileManager.putFileForInput(StandardLocation.SOURCE_PATH, 
                packName.replace('.', '/'), 
                shortName + ClassUtils.JAVA_EXTENSION, jfile);
        Boolean result = compiler.getTask(null, javaFileManager, 
                diagnosticCollector, options, 
                null, Arrays.asList(new JavaFileObject[]{jfile})).call();
        if (result == null || ! result.booleanValue()) {
            throw new IllegalStateException("Compilation failed. class: " 
                    + ecls.toString() + ", diagnostics: " 
                    + diagnosticCollector.getDiagnostics());
        }
    }
    
    public Class getPartManager(Class entity, DbProductName dbType) throws Exception {
        PartitionParam trp  = null;
        DdlManager     ddlm = null;
        if (dbType == DbProductName.Postgresql) {
            Ddl ddl = new PostgresqlDdl();
            ddlm = new PostgresqlDdlManager(entity, ddl);
        } else if (dbType == DbProductName.Mysql) {
            Ddl ddl = new MysqlDdl();
            ddlm = new MysqlDdlManager(entity, ddl);
        } else if (dbType == DbProductName.Oracle) {
            Ddl ddl = new OracleDdl();
            ddlm = new OracleDdlManager(entity, ddl);
        }
        try {
            trp = ddlm.getPartitionParam();
        } catch (Exception e) {
            
        }
        String partmName = "partm." + entity.getName();
        Class partm = cache.get(partmName);
        if (partm != null) {
            return partm;
        }
        try {
            DynamicClassLoader loader = this.getEntityLoader(entity);
            partm = loader.loadClass(partmName);
        } catch (ClassNotFoundException ex) {
                pmlock.lock();
                try {
                    partm = this.getPartitionManager(entity);
                    if (partm != null) {
                        cache.put(partmName, partm);
                    }
                } finally {
                    pmlock.unlock();
                }
        }
        return partm;
    }
    
    public void compilePartManager(JavaCode           javaCode, 
                                   DynamicClassLoader loader) throws Exception {
        DynamicJavaFile jfile = new DynamicJavaFile(javaCode.getClassName(), 
                javaCode.getCode());
        if (logger.isDebugEnabled()) {
            logger.debug("{} 's source [{}]", javaCode.getClassName(), 
                    javaCode.getCode());
        }
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null) {
            throw new IllegalStateException("Can not get system java compiler. Please add jdk tools.jar to your classpath.");
        }
        DiagnosticCollector<JavaFileObject> diagnosticCollector = new DiagnosticCollector<JavaFileObject>();
        StandardJavaFileManager manager = compiler.getStandardFileManager(diagnosticCollector, null, null);
        final ClassLoader contextLoader = Thread.currentThread().getContextClassLoader();
        ClassLoader cloader = contextLoader;
        List<File> files = new ArrayList<File>();
        while (cloader instanceof URLClassLoader 
                && (! loader.getClass().getName()
                    .equals("sun.misc.Launcher$AppClassLoader"))) {
            URLClassLoader urlClassLoader = (URLClassLoader) cloader;
            for (URL url : urlClassLoader.getURLs()) {
                files.add(new File(url.getFile()));
            }
            cloader = cloader.getParent();
        }
        if (files.size() > 0) {
            try {
                manager.setLocation(StandardLocation.CLASS_PATH, files);
            } catch (IOException e) {
                throw new IllegalStateException(e.getMessage(), e);
            }
        }

        List<String> options = new ArrayList<String>();
        DynamicJavaFileManager javaFileManager = new DynamicJavaFileManager(
                manager, loader);
        javaFileManager.putFileForInput(StandardLocation.SOURCE_PATH, 
                javaCode.getPackName().replace('.', '/'), 
                javaCode.getClassName() + ClassUtils.JAVA_EXTENSION, jfile);
        Boolean result = compiler.getTask(null, javaFileManager, 
                diagnosticCollector, options, 
                null, Arrays.asList(new JavaFileObject[]{jfile})).call();
        if (result == null || ! result.booleanValue()) {
            throw new IllegalStateException("Compilation failed. class: " 
                    + javaCode.getPackName() + "." + javaCode.getClassName() + 
                    ", diagnostics: " 
                    + diagnosticCollector.getDiagnostics());
        }
    }

    @Override
    public Class getViewDaoClass(Class beanCls, Builder builder) 
            throws ViewException, Exception {
        String daoName = ClassUtil.vdaoPackPre + beanCls.getName() + "Dao";
        String cacheKey = builder.getDbProductName() + "_" + daoName;
        Class dao = cache.get(cacheKey);
        if (dao != null) {
            return dao;
        }
        DynamicClassLoader loader = this.getEntityLoader(beanCls);
        try {
            dao = loader.loadClass(daoName);
        } catch (ClassNotFoundException ex) {
            vdaolock.lock();
            try {
                compileViewDao(beanCls, loader, builder);
                try {
                    dao = loader.loadClass(daoName);
                    cache.put(cacheKey, dao);
                } catch (ClassNotFoundException e) {
                    throw e;
                }
            } finally {
                vdaolock.unlock();
            }
        }
        return dao;
    }
    
    public static void compileViewDao(Class ecls, DynamicClassLoader loader,
            Builder builder) 
            throws ViewException, Exception {
        String daoName = ClassUtil.vdaoPackPre + "_" + ecls.getName() + "Dao";
        if (daoName == null) {
            throw new ViewException("Dao package name is null");
        }
        String packName  = "";
        String shortName = "";
        int lastDot = daoName.lastIndexOf(".");
        if (lastDot <= 0) {
            throw new ViewException("Dao package name not well rule");
        }
        packName  = daoName.substring(0, lastDot);
        shortName = daoName.substring(lastDot+1);
        String source = builder.getViewDaoCode(ecls);
        if (logger.isDebugEnabled()) {
            logger.debug("{}'s java source [{}]", daoName, source);
        }
        DynamicJavaFile jfile = new DynamicJavaFile(shortName, source);
                
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null) {
            throw new IllegalStateException("Can not get system java compiler. "
                    + "Please add jdk tools.jar to your classpath.");
        }
        DiagnosticCollector<JavaFileObject> diagnosticCollector = 
                new DiagnosticCollector<JavaFileObject>();
        StandardJavaFileManager manager = compiler.getStandardFileManager(
                diagnosticCollector, null, null);
        final ClassLoader contextLoader = Thread.currentThread()
                .getContextClassLoader();
        ClassLoader cloader = contextLoader;
        List<File> files = new ArrayList<File>();
        while (cloader instanceof URLClassLoader 
                && (! loader.getClass().getName()
                    .equals("sun.misc.Launcher$AppClassLoader"))) {
            URLClassLoader urlClassLoader = (URLClassLoader) cloader;
            for (URL url : urlClassLoader.getURLs()) {
                files.add(new File(url.getFile()));
            }
            cloader = cloader.getParent();
        }
        if (files.size() > 0) {
            try {
                manager.setLocation(StandardLocation.CLASS_PATH, files);
            } catch (IOException e) {
                throw new IllegalStateException(e.getMessage(), e);
            }
        }

        List<String> options = new ArrayList<String>();
        DynamicJavaFileManager javaFileManager = new DynamicJavaFileManager(
                manager, loader);
        javaFileManager.putFileForInput(StandardLocation.SOURCE_PATH, 
                packName.replace('.', '/'), 
                shortName + ClassUtils.JAVA_EXTENSION, jfile);
        Boolean result = compiler.getTask(null, javaFileManager, 
                diagnosticCollector, options, 
                null, Arrays.asList(new JavaFileObject[]{jfile})).call();
        if (result == null || ! result.booleanValue()) {
            throw new IllegalStateException("Compilation failed. class: " 
                    + ecls.toString() + ", diagnostics: " 
                    + diagnosticCollector.getDiagnostics());
        }
    }

    @Override
    public Class getMapDaoClass(Builder builder) throws Exception {
        String daoName = "com.easyea.mapdao." + builder.getDbProductName() + "MapDao";
        Class dao = cache.get(daoName);
        if (dao != null) {
            return dao;
        }
        DynamicClassLoader loader = this.getEntityLoader(MapDao.class);
        try {
            dao = loader.loadClass(daoName);
        } catch (ClassNotFoundException ex) {
            mapdaolock.lock();
            try {
                compileMapDao(daoName, loader, builder);
                try {
                    dao = loader.loadClass(daoName);
                    cache.put(daoName, dao);
                } catch (ClassNotFoundException e) {
                    throw e;
                }
            } finally {
                mapdaolock.unlock();
            }
        }
        return dao;
    }
    
    public static void compileMapDao(String daoName, DynamicClassLoader loader, 
            Builder builder) throws EntityException, Exception {
        if (daoName == null) {
            throw new EntityException("MapDao package name is null");
        }
        String source = builder.getMapDaoCode();
        int lastDot = daoName.lastIndexOf(".");
        String shortName = daoName.substring(lastDot + 1);
        String packName  = daoName.substring(0, lastDot);
        if (logger.isDebugEnabled()) {
            logger.debug("{}'s java source [{}]", daoName, source);
        }
        DynamicJavaFile jfile = new DynamicJavaFile(shortName, source);
                
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null) {
            throw new IllegalStateException("Can not get system java compiler. Please add jdk tools.jar to your classpath.");
        }
        DiagnosticCollector<JavaFileObject> diagnosticCollector = new DiagnosticCollector<JavaFileObject>();
        StandardJavaFileManager manager = compiler.getStandardFileManager(diagnosticCollector, null, null);
        final ClassLoader contextLoader = Thread.currentThread().getContextClassLoader();
        ClassLoader cloader = contextLoader;
        List<File> files = new ArrayList<File>();
        while (cloader instanceof URLClassLoader 
                && (! loader.getClass().getName()
                    .equals("sun.misc.Launcher$AppClassLoader"))) {
            URLClassLoader urlClassLoader = (URLClassLoader) cloader;
            for (URL url : urlClassLoader.getURLs()) {
                files.add(new File(url.getFile()));
            }
            cloader = cloader.getParent();
        }
        if (files.size() > 0) {
            try {
                manager.setLocation(StandardLocation.CLASS_PATH, files);
            } catch (IOException e) {
                throw new IllegalStateException(e.getMessage(), e);
            }
        }

        List<String> options = new ArrayList<String>();
        DynamicJavaFileManager javaFileManager = new DynamicJavaFileManager(
                manager, loader);
        javaFileManager.putFileForInput(StandardLocation.SOURCE_PATH, 
                packName.replace('.', '/'), 
                shortName + ClassUtils.JAVA_EXTENSION, jfile);
        Boolean result = compiler.getTask(null, javaFileManager, 
                diagnosticCollector, options, 
                null, Arrays.asList(new JavaFileObject[]{jfile})).call();
        if (result == null || ! result.booleanValue()) {
            throw new IllegalStateException("Compilation failed. class: " 
                    + daoName + ", diagnostics: " 
                    + diagnosticCollector.getDiagnostics());
        }
    }

    @Override
    public EntityFactory getEntityFactory(Class entityCls, Builder builder) 
            throws EntityException, Exception {
        Class fcls = null;
        String factName = ClassUtil.daoFactPackPre + entityCls.getName() + 
                "DaoFactory";
        String daoName  = ClassUtil.daoPackPre + entityCls.getName() + "Dao";
        DynamicClassLoader loader = this.getEntityLoader(entityCls);
        try {
            fcls = loader.loadClass(factName);
        } catch (ClassNotFoundException ex) {
            daofactlock.lock();
            try {
                this.getDaoClass(entityCls, builder);
                compileDaoFactory(entityCls, daoName, loader, builder.getDbProductName());
                fcls = loader.loadClass(factName);
            } catch (Exception e) {
                throw e;
            }finally {
                daofactlock.unlock();
            }
        }
        return (EntityFactory)fcls.newInstance();
    }
    
    public static void compileDaoFactory(Class entityCls, String daoName, 
            DynamicClassLoader loader, DbProductName db) throws EntityException, Exception {
        String factName = ClassUtil.daoFactPackPre + entityCls.getName() + 
                "DaoFactory";
        String packName  = "";
        String shortName = "";
        int lastDot = factName.lastIndexOf(".");
        if (lastDot <= 0) {
            throw new EntityException("Dao package name not well rule");
        }
        packName  = factName.substring(0, lastDot);
        shortName = factName.substring(lastDot+1);
        String source = getEntityFactoryCode(entityCls, db);
        if (logger.isDebugEnabled()) {
            logger.debug("{} Factory's java source [{}]", daoName, source);
        }
        DynamicJavaFile jfile = new DynamicJavaFile(shortName, source);
                
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null) {
            throw new IllegalStateException("Can not get system java compiler. Please add jdk tools.jar to your classpath.");
        }
        DiagnosticCollector<JavaFileObject> diagnosticCollector = new DiagnosticCollector<JavaFileObject>();
        StandardJavaFileManager manager = compiler.getStandardFileManager(diagnosticCollector, null, null);
        final ClassLoader contextLoader = Thread.currentThread().getContextClassLoader();
        ClassLoader cloader = contextLoader;
        List<File> files = new ArrayList<File>();
        while (cloader instanceof URLClassLoader 
                && (! loader.getClass().getName()
                    .equals("sun.misc.Launcher$AppClassLoader"))) {
            URLClassLoader urlClassLoader = (URLClassLoader) cloader;
            for (URL url : urlClassLoader.getURLs()) {
                files.add(new File(url.getFile()));
            }
            cloader = cloader.getParent();
        }
        if (files.size() > 0) {
            try {
                manager.setLocation(StandardLocation.CLASS_PATH, files);
            } catch (IOException e) {
                throw new IllegalStateException(e.getMessage(), e);
            }
        }

        List<String> options = new ArrayList<String>();
        DynamicJavaFileManager javaFileManager = new DynamicJavaFileManager(
                manager, loader);
        javaFileManager.putFileForInput(StandardLocation.SOURCE_PATH, 
                packName.replace('.', '/'), 
                shortName + ClassUtils.JAVA_EXTENSION, jfile);
        Boolean result = compiler.getTask(null, javaFileManager, 
                diagnosticCollector, options, 
                null, Arrays.asList(new JavaFileObject[]{jfile})).call();
        if (result == null || ! result.booleanValue()) {
            throw new IllegalStateException("Compilation failed. class: " 
                    + factName + ", diagnostics: " 
                    + diagnosticCollector.getDiagnostics());
        }
    }
    
    public static String getEntityFactoryCode(Class entityCls, DbProductName db) {
        String code = "";
        WebitTemplate tpl = new WebitTemplate();
        Map<String, Object> context = new HashMap<String, Object>();
        context.put("daoFactPackPre", ClassUtil.daoFactPackPre);
        context.put("entity", entityCls);
        context.put("entityPackage", entityCls.getPackage().getName());
        context.put("dbProductName", db);
        try {
            code = tpl.render("/codetpl/EntityDaoFactory.etpl", context);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return code;
    }

    @Override
    public ViewFactory getViewDaoFactory(Class beanCls, Builder builder) 
            throws ViewException, Exception {
        Class fcls = null;
        String factName = ClassUtil.vdaoFactPackPre + beanCls.getName() + 
                "DaoFactory";
        /*
        final ClassLoader contextLoader = Thread.currentThread().getContextClassLoader();
        this.loader = AccessController.doPrivileged(
                new PrivilegedAction<DynamicClassLoader>() {
                    public DynamicClassLoader run() {
                        return new DynamicClassLoader(contextLoader);
                    }
                }
            );
        */
        DynamicClassLoader loader = this.getEntityLoader(beanCls);
        try {
            fcls = loader.loadClass(factName);
        } catch (ClassNotFoundException ex) {
            vdaofactlock.lock();
            try {
                this.getViewDaoClass(beanCls, builder);
                compileViewDaoFactory(factName, beanCls, loader);
                fcls = loader.loadClass(factName);
            } catch (Exception e) {
                throw e;
            }finally {
                vdaofactlock.unlock();
            }
        }
        return (ViewFactory)fcls.newInstance();
    }
    
    public static void compileViewDaoFactory(String factName, Class beanCls, 
            DynamicClassLoader loader) throws EntityException, Exception {
        String daoName = ClassUtil.vdaoPackPre + beanCls.getName() + "Dao";
        String packName  = "";
        String shortName = "";
        int lastDot = factName.lastIndexOf(".");
        if (lastDot <= 0) {
            throw new EntityException("Dao package name not well rule");
        }
        packName  = factName.substring(0, lastDot);
        shortName = factName.substring(lastDot+1);
        String source = getViewFactoryCode(beanCls);
        if (logger.isDebugEnabled()) {
            logger.debug("factName's source = [\n{}]", source);
        }
        DynamicJavaFile jfile = new DynamicJavaFile(shortName, source);
                
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null) {
            throw new IllegalStateException("Can not get system java compiler. Please add jdk tools.jar to your classpath.");
        }
        DiagnosticCollector<JavaFileObject> diagnosticCollector = new DiagnosticCollector<JavaFileObject>();
        StandardJavaFileManager manager = compiler.getStandardFileManager(diagnosticCollector, null, null);
        final ClassLoader contextLoader = Thread.currentThread().getContextClassLoader();
        ClassLoader cloader = contextLoader;
        List<File> files = new ArrayList<File>();
        while (cloader instanceof URLClassLoader 
                && (! loader.getClass().getName()
                    .equals("sun.misc.Launcher$AppClassLoader"))) {
            URLClassLoader urlClassLoader = (URLClassLoader) cloader;
            for (URL url : urlClassLoader.getURLs()) {
                files.add(new File(url.getFile()));
            }
            cloader = cloader.getParent();
        }
        if (files.size() > 0) {
            try {
                manager.setLocation(StandardLocation.CLASS_PATH, files);
            } catch (IOException e) {
                throw new IllegalStateException(e.getMessage(), e);
            }
        }

        List<String> options = new ArrayList<String>();
        DynamicJavaFileManager javaFileManager = new DynamicJavaFileManager(
                manager, loader);
        javaFileManager.putFileForInput(StandardLocation.SOURCE_PATH, 
                packName.replace('.', '/'), 
                shortName + ClassUtils.JAVA_EXTENSION, jfile);
        Boolean result = compiler.getTask(null, javaFileManager, 
                diagnosticCollector, options, 
                null, Arrays.asList(new JavaFileObject[]{jfile})).call();
        if (result == null || ! result.booleanValue()) {
            throw new IllegalStateException("Compilation failed. class: " 
                    + factName + ", diagnostics: " 
                    + diagnosticCollector.getDiagnostics());
        }
    }
    
    public static String getViewFactoryCode(Class entityCls) {
        String code = "";
        WebitTemplate tpl = new WebitTemplate();
        Map<String, Object> context = new HashMap<String, Object>();
        context.put("vdaoFactPackPre", ClassUtil.vdaoFactPackPre);
        context.put("entity", entityCls);
        context.put("entityPackage", entityCls.getPackage().getName());
        try {
            code = tpl.render("/codetpl/ViewDaoFactory.etpl", context);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return code;
    }

    @Override
    public MapFactory getMapDaoFactory(Builder builder) throws Exception {
        Class fcls = null;
        String daoName = ClassUtil.MAPDAO_PACKAGE + "f." + 
                    builder.getDbProductName() + "Dao";
        String factName = daoName + "Factory";
        DynamicClassLoader loader = this.getEntityLoader(MapDao.class);
        try {
            fcls = loader.loadClass(factName);
        } catch (ClassNotFoundException ex) {
            mapdaofactlock.lock();
            try {
                this.getMapDaoClass(builder);
                compileMapDaoFactory(builder.getDbProductName(), daoName, loader);
                fcls = loader.loadClass(factName);
            } catch (Exception e) {
                throw e;
            }finally {
                mapdaofactlock.unlock();
            }
        }
        return (MapFactory)fcls.newInstance();
    }
    
    public static void compileMapDaoFactory(DbProductName dbProductName, 
            String daoName, DynamicClassLoader loader) 
            throws EntityException, Exception {
        String factName = "com.easyea.mapdaof." + dbProductName + 
                "DaoFactory";
        String packName  = "";
        String shortName = "";
        int lastDot = factName.lastIndexOf(".");
        if (lastDot <= 0) {
            throw new EntityException("Dao package name not well rule");
        }
        packName  = factName.substring(0, lastDot);
        shortName = factName.substring(lastDot+1);
        String source = getMapFactoryCode(dbProductName);
        if (logger.isDebugEnabled()) {
            logger.debug("MapDaoFactory's source [\n{}]", source);
        }
        DynamicJavaFile jfile = new DynamicJavaFile(shortName, source);
                
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null) {
            throw new IllegalStateException("Can not get system java compiler. Please add jdk tools.jar to your classpath.");
        }
        DiagnosticCollector<JavaFileObject> diagnosticCollector = new DiagnosticCollector<JavaFileObject>();
        StandardJavaFileManager manager = compiler.getStandardFileManager(diagnosticCollector, null, null);
        final ClassLoader contextLoader = Thread.currentThread().getContextClassLoader();
        ClassLoader cloader = contextLoader;
        List<File> files = new ArrayList<File>();
        while (cloader instanceof URLClassLoader 
                && (! loader.getClass().getName()
                    .equals("sun.misc.Launcher$AppClassLoader"))) {
            URLClassLoader urlClassLoader = (URLClassLoader) cloader;
            for (URL url : urlClassLoader.getURLs()) {
                files.add(new File(url.getFile()));
            }
            cloader = cloader.getParent();
        }
        if (files.size() > 0) {
            try {
                manager.setLocation(StandardLocation.CLASS_PATH, files);
            } catch (IOException e) {
                throw new IllegalStateException(e.getMessage(), e);
            }
        }

        List<String> options = new ArrayList<String>();
        DynamicJavaFileManager javaFileManager = new DynamicJavaFileManager(
                manager, loader);
        javaFileManager.putFileForInput(StandardLocation.SOURCE_PATH, 
                packName.replace('.', '/'), 
                shortName + ClassUtils.JAVA_EXTENSION, jfile);
        Boolean result = compiler.getTask(null, javaFileManager, 
                diagnosticCollector, options, 
                null, Arrays.asList(new JavaFileObject[]{jfile})).call();
        if (result == null || ! result.booleanValue()) {
            throw new IllegalStateException("Compilation failed. class: " 
                    + factName + ", diagnostics: " 
                    + diagnosticCollector.getDiagnostics());
        }
    }
    
    public static String getMapFactoryCode(DbProductName dbProductName) {
        String code = "";
        WebitTemplate tpl = new WebitTemplate();
        Map<String, Object> context = new HashMap<String, Object>();
        context.put("mapdaoPackage", ClassUtil.MAPDAO_PACKAGE);
        context.put("dbProductName", dbProductName);
        try {
            code = tpl.render("/codetpl/MapDaoFactory.etpl", context);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return code;
    }
    
}
