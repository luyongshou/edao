/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.easyea.edao.manager;

import com.easyea.dynamic.DynamicClassLoader;
import com.easyea.dynamic.DynamicJavaFile;
import com.easyea.dynamic.DynamicJavaFileManager;
import com.easyea.edao.Builder;
import com.easyea.edao.DaoManager;
import com.easyea.edao.EntityFactory;
import com.easyea.edao.MapFactory;
import com.easyea.edao.ViewFactory;
import com.easyea.edao.exception.EntityException;
import com.easyea.edao.exception.ViewException;
import com.easyea.internal.util.ClassUtils;
import com.easyea.logger.Logger;
import com.easyea.logger.LoggerFactory;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;

/**
 *
 * @author louis
 */
public class DefaultManager implements DaoManager {
    
    static Logger logger = LoggerFactory.getLogger(DefaultManager.class);

    private static DefaultManager instance = null;
    private final static ReentrantLock lock = new ReentrantLock();
    private final static ReentrantLock block = new ReentrantLock();
    private final static ReentrantLock flock = new ReentrantLock();
    private final static ReentrantLock vlock = new ReentrantLock();
    private final static ReentrantLock vflock = new ReentrantLock();
    private DynamicClassLoader loader;
    private ConcurrentHashMap<String, Class> cache = 
            new ConcurrentHashMap<String, Class>();

    private DefaultManager() {
        final ClassLoader contextLoader = Thread.currentThread().getContextClassLoader();
        this.loader = AccessController.doPrivileged(
                new PrivilegedAction<DynamicClassLoader>() {
                    public DynamicClassLoader run() {
                        return new DynamicClassLoader(contextLoader);
                    }
                }
            );
    }
    
    public DynamicClassLoader getLoader() {
        return this.loader;
    }
    
    public void setLoader(DynamicClassLoader loader) {
        this.loader = loader;
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
    public EntityFactory getEntityFactory(String daoName, Builder builder, String db) 
            throws EntityException,Exception {
        Class fcls = null;
        String factName = getEntityFactoryName(daoName);
        try {
            fcls = loader.loadClass(factName);
        } catch (ClassNotFoundException ex) {
            flock.lock();
            try {
                this.getDaoClass(daoName, builder);
                compileDaoFactory(factName, daoName, loader, db);
                fcls = loader.loadClass(factName);
            } catch (Exception e) {
                throw e;
            }finally {
                flock.unlock();
            }
        }
        return (EntityFactory)fcls.newInstance();
    }
    
    @Override
    public MapFactory getMapDaoFactory(Builder builder) throws Exception {
        Class fcls = null;
        String daoName = "com.easyea.mapdao." + builder.getDbTypeName() + "MapDao";
        String factName = getMapFactoryName(daoName);
        try {
            fcls = loader.loadClass(factName);
        } catch (ClassNotFoundException ex) {
            flock.lock();
            try {
                this.getMapDaoClass(builder);
                compileMapDaoFactory(factName, daoName, loader);
                fcls = loader.loadClass(factName);
            } catch (Exception e) {
                throw e;
            }finally {
                flock.unlock();
            }
        }
        return (MapFactory)fcls.newInstance();
    }
    
    @Override
    public ViewFactory getViewDaoFactory(String daoName, Builder builder) 
            throws ViewException, Exception {
        Class fcls = null;
        String factName = getViewFactoryName(daoName);
        try {
            fcls = loader.loadClass(factName);
        } catch (ClassNotFoundException ex) {
            vflock.lock();
            try {
                this.getViewDaoClass(daoName, builder);
                compileViewDaoFactory(factName, daoName, loader);
                fcls = loader.loadClass(factName);
            } catch (Exception e) {
                throw e;
            }finally {
                vflock.unlock();
            }
        }
        return (ViewFactory)fcls.newInstance();
    }

    @Override
    public Class getDaoClass(String daoName, Builder builder) 
            throws EntityException, Exception {
        Class dao = cache.get(daoName);
        if (dao != null) {
            return dao;
        }
        try {
            dao = loader.loadClass(daoName);
        } catch (ClassNotFoundException ex) {
            block.lock();
            try {
                compileDao(daoName, loader, builder);
                try {
                    dao = loader.loadClass(daoName);
                    cache.put(daoName, dao);
                } catch (ClassNotFoundException e) {
                    throw e;
                }
            } finally {
                block.unlock();
            }
        }
        return dao;
    }
    
    @Override
    public Class getMapDaoClass(Builder builder) throws Exception {
        String daoName = "com.easyea.mapdao." + builder.getDbTypeName() + "MapDao";
        Class dao = cache.get(daoName);
        if (dao != null) {
            return dao;
        }
        try {
            dao = loader.loadClass(daoName);
        } catch (ClassNotFoundException ex) {
            block.lock();
            try {
                compileMapDao(daoName, loader, builder);
                try {
                    dao = loader.loadClass(daoName);
                    cache.put(daoName, dao);
                } catch (ClassNotFoundException e) {
                    throw e;
                }
            } finally {
                block.unlock();
            }
        }
        return dao;
    }
    
    public static void main(String[] args) {
        
    }

    @Override
    public Class getViewDaoClass(String daoName, Builder builder) 
            throws ViewException, Exception {
        Class dao = cache.get(daoName);
        if (dao != null) {
            return dao;
        }
        try {
            dao = loader.loadClass(daoName);
        } catch (ClassNotFoundException ex) {
            vlock.lock();
            try {
                compileViewDao(daoName, loader, builder);
                try {
                    dao = loader.loadClass(daoName);
                    cache.put(daoName, dao);
                } catch (ClassNotFoundException e) {
                    throw e;
                }
            } finally {
                vlock.unlock();
            }
        }
        return dao;
    }
    
    public static void compileDaoFactory(String factName, String daoName, 
            DynamicClassLoader loader, String db) throws EntityException, Exception {
        String packName  = "";
        String shortName = "";
        int lastDot = factName.lastIndexOf(".");
        if (lastDot <= 0) {
            throw new EntityException("Dao package name not well rule");
        }
        packName  = factName.substring(0, lastDot);
        shortName = factName.substring(lastDot+1);
        String source = getEntityFactoryCode(daoName, db);
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
    
    public static void compileViewDaoFactory(String factName, String daoName, 
            DynamicClassLoader loader) throws EntityException, Exception {
        String packName  = "";
        String shortName = "";
        int lastDot = factName.lastIndexOf(".");
        if (lastDot <= 0) {
            throw new EntityException("Dao package name not well rule");
        }
        packName  = factName.substring(0, lastDot);
        shortName = factName.substring(lastDot+1);
        String source = getViewFactoryCode(daoName);
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
    
    public static void compileMapDaoFactory(String factName, String daoName, 
            DynamicClassLoader loader) throws EntityException, Exception {
        String packName  = "";
        String shortName = "";
        int lastDot = factName.lastIndexOf(".");
        if (lastDot <= 0) {
            throw new EntityException("Dao package name not well rule");
        }
        packName  = factName.substring(0, lastDot);
        shortName = factName.substring(lastDot+1);
        String source = getMapFactoryCode(daoName);
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
    
    public static void compileDao(String daoName, DynamicClassLoader loader, 
            Builder builder) throws EntityException, Exception {
        if (daoName == null) {
            throw new EntityException("Dao package name is null");
        }
        Class ecls = DefaultManager.getEntityClass(daoName);
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
    
    public static void compileViewDao(String daoName, DynamicClassLoader loader,
            Builder builder) 
            throws ViewException, Exception {
        if (daoName == null) {
            throw new ViewException("Dao package name is null");
        }
        Class ecls = DefaultManager.getViewClass(daoName);
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
    
    public static Class getEntityClass(String daoName) throws EntityException {
        if (daoName == null || daoName.trim().length() == 0 
                || daoName.startsWith(".") || daoName.endsWith(".")) {
            throw new EntityException("Dao package name not well rule");
        }
        Class ecls = null;
        String[] apackage = daoName.split("\\.");
        StringBuilder entity1 = new StringBuilder();
        StringBuilder entity2 = new StringBuilder();
        if (apackage.length > 2 && "dao".equals(apackage[apackage.length-2])) {
            for (int i=0;i<apackage.length-1;i++) {
                if (!"dao".equals(apackage[i])) {
                    entity1.append(apackage[i]).append(".");
                    entity2.append(apackage[i]).append(".");
                } else {
                    entity1.append("entity").append(".");
                    entity2.append("entitybean").append(".");
                }
            }
            String sname = apackage[apackage.length-1];
            if (sname.length() > 3) {
                entity1.append(sname.substring(0, sname.length()-3));
                try {
                    ecls = Class.forName(entity1.toString());
                } catch (ClassNotFoundException e) {
                    entity2.append(sname.substring(0, sname.length()-3));
                    try {
                        ecls = Class.forName(entity2.toString());
                    } catch (ClassNotFoundException e2s) {
                        throw new EntityException("[" + entity1 +  "] not found"
                                + " and [" + entity2 +  "] not found");
                    }
                }
            } else {
                throw new EntityException("Dao must endwith \"Dao\"");
            }
        } else {
            throw new EntityException("Dao package name not well rule");
        }
        return ecls;
    }
    
    public static String getEntityFactoryName(String daoName) {
        int lastDot = daoName.lastIndexOf(".");
        String packName = daoName.substring(0, lastDot);
        String clsName  = daoName.substring(lastDot + 1);
        return packName + "f." + clsName + "Factory";
    }
    
    public static String getMapFactoryName(String daoName) {
        int lastDot = daoName.lastIndexOf(".");
        String packName = daoName.substring(0, lastDot);
        String clsName  = daoName.substring(lastDot + 1);
        return packName + "f." + clsName + "Factory";
    }
    
    public static String getViewFactoryName(String daoName) {
        int lastDot = daoName.lastIndexOf(".");
        String packName = daoName.substring(0, lastDot);
        String clsName  = daoName.substring(lastDot + 1);
        return packName + "f." + clsName + "Factory";
    }
    
    public static Class getViewClass(String daoName) throws ViewException {
        Class vcls = null;
        if (daoName == null || daoName.trim().length() == 0 
                || daoName.startsWith(".") || daoName.endsWith(".")) {
            throw new ViewException("Dao package name not well rule");
        }
        String[] apackage = daoName.split("\\.");
        StringBuilder view = new StringBuilder();
        if (apackage.length > 2
                && "viewdao".equals(apackage[apackage.length-2])) {
            for (int i=0;i<apackage.length-1;i++) {
                if (!"viewdao".equals(apackage[i])) {
                    view.append(apackage[i]).append(".");
                } else {
                    view.append("view").append(".");
                }
            }
            String sname = apackage[apackage.length-1];
            if (sname.length() > 3) {
                view.append(sname.substring(0, sname.length()-3));
                try {
                    vcls = Class.forName(view.toString());
                } catch (ClassNotFoundException e) {
                        throw new ViewException("[" + view +  "] not found!");
                }
            } else {
                throw new ViewException("Dao must endwith \"Dao\"");
            }
        } else {
            throw new ViewException("Dao package name not well rule");
        }
        return vcls;
    }
    
    public static String getEntityFactoryCode(String daoName, String db) {
        StringBuilder code = new StringBuilder();
        int lastDot = daoName.lastIndexOf(".");
        String packName = daoName.substring(0, lastDot);
        String entityPack = packName.substring(0, packName.length()-3) + "entity";
        String clsName  = daoName.substring(lastDot + 1);
        String entityName = clsName.substring(0, clsName.length() - 3);
        code.append("package ").append(packName).append("f;").append(r(2));
        code.append("import ").append(daoName)
                .append(";").append(r(1));
        code.append("import com.easyea.edao.EntityFactory;").append(r(1));
        code.append("import com.easyea.edao.Builder;").append(r(1));
        code.append("import com.easyea.edao.EntityDao;").append(r(1));
        code.append("import com.easyea.edao.ddls.").append(db).append("DdlManager;").append(r(1));
        code.append("import ").append(entityPack).append(".").append(entityName).append(";").append(r(1));
        code.append("import com.easyea.edao.exception.EntityException;").append(r(2));
        code.append("public class ").append(clsName).append("Factory")
                .append(" implements EntityFactory {").append(r(1));
        code.append(t(2)).append("private ").append(db).append("DdlManager ddlm = null;").append(r(1));
        code.append(t(1)).append("public EntityDao getDao(String daoName)").append(r(1))
                .append(t(3)).append("throws EntityException, Exception {").append(r(1));
        code.append(t(2)).append("EntityDao dao = new ").append(clsName).append("();").append(r(1));
        code.append(t(2)).append("if (ddlm == null) {").append(r(1));
        code.append(t(3)).append("this.ddlm = new ").append(db).append("DdlManager(").append(entityName).append(".class);").append(r(1));
        code.append(t(2)).append("}").append(r(1));
        code.append(t(2)).append("dao.setDdlManager(ddlm);").append(r(1));
        code.append(t(2)).append("return dao;").append(r(1));
        code.append(t(1)).append("}").append(r(1));
        code.append("}").append(r(1));
        return code.toString();
    }
    
    public static String getViewFactoryCode(String daoName) {
        StringBuilder code = new StringBuilder();
        int lastDot = daoName.lastIndexOf(".");
        String packName = daoName.substring(0, lastDot);
        String clsName  = daoName.substring(lastDot + 1);
        code.append("package ").append(packName).append("f;").append(r(2));
        code.append("import ").append(daoName)
                .append(";").append(r(1));
        code.append("import com.easyea.edao.ViewFactory;").append(r(1));
        code.append("import com.easyea.edao.Builder;").append(r(1));
        code.append("import com.easyea.edao.ViewDao;").append(r(1));
        code.append("import com.easyea.edao.exception.ViewException;").append(r(2));
        code.append("public class ").append(clsName).append("Factory")
                .append(" implements ViewFactory {").append(r(1));
        code.append(t(1)).append("public ViewDao getDao(String daoName)").append(r(1))
                .append(t(3)).append("throws ViewException, Exception {").append(r(1));
        code.append(t(2)).append("return new ").append(clsName).append("();").append(r(1));
        code.append(t(1)).append("}").append(r(1));
        code.append("}").append(r(1));
        return code.toString();
    }
    
    public static String getMapFactoryCode(String daoName) {
        StringBuilder code = new StringBuilder();
        int lastDot = daoName.lastIndexOf(".");
        String packName = daoName.substring(0, lastDot);
        String clsName  = daoName.substring(lastDot + 1);
        code.append("package ").append(packName).append("f;").append(r(2));
        code.append("import ").append(daoName)
                .append(";").append(r(1));
        code.append("import com.easyea.edao.MapFactory;").append(r(1));
        code.append("import com.easyea.edao.Builder;").append(r(1));
        code.append("import com.easyea.edao.MapDao;").append(r(1));
        code.append("import com.easyea.edao.exception.ViewException;").append(r(2));
        code.append("public class ").append(clsName).append("Factory")
                .append(" implements MapFactory {").append(r(1));
        code.append(t(1)).append("public MapDao getDao()").append(r(1))
                .append(t(3)).append("throws Exception {").append(r(1));
        code.append(t(2)).append("return new ").append(clsName).append("();").append(r(1));
        code.append(t(1)).append("}").append(r(1));
        code.append("}").append(r(1));
        return code.toString();
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

    

    
}
