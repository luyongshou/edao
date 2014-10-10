/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.easyea.edao;

import com.easyea.edao.managers.DefaultManager;
import com.easyea.logger.Logger;
import com.easyea.logger.LoggerFactory;
import java.lang.reflect.Constructor;
import java.sql.Connection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * @author louis
 */
public class DaoFactory {
    
    private static Logger logger = LoggerFactory.getLogger(DaoFactory.class);
    
    private static final ConcurrentHashMap<String, EntityFactory> entityFacts =
            new ConcurrentHashMap<String, EntityFactory>();
    private static final ConcurrentHashMap<String, ViewFactory> viewFacts =
            new ConcurrentHashMap<String, ViewFactory>();
    private static final ConcurrentHashMap<String, MapFactory> mapFacts =
            new ConcurrentHashMap<String, MapFactory>();
    
    /**
     * EntityFactory缓存的锁，如果缓存中没有该dao实现的工厂类，在获取工厂类的时候加锁防止
     * 多个线程同时进行编译，影响性能。
     */
    private final static ReentrantLock elock = new ReentrantLock();
    
    /**
     * ViewFactory缓存锁，如果缓存中没有视图dao实现的工厂类，在获取视图工厂类的时候加锁， 
     * 防止多个线程同时进行代码生成以及编译，影响性能。
     */
    private final static ReentrantLock vlock = new ReentrantLock();
    /**
     * MapFactory缓存锁，如果缓存中没有MapDao实现的工厂类，在获取MapFactory工厂类的时候枷锁，
     * 防止多个线程同时进行代码生成以及编译，影响性能。
     */
    private final static ReentrantLock mlock = new ReentrantLock();
    
    /**
     * 根据持久化对象的Class来获取一个持久化Dao的实现
     * @param entity 持久Bean的Class对象
     * @return
     * @throws Exception 
     */
    public static EntityDao getDao(Class entity) throws Exception {
        return getDao(entity, DbProductName.Postgresql);
    }
    /**
     * 根据持久化对象的Class以及制定数据类型来获取一个持久化Dao的实现
     * @param entity 持久Bean的Class对象
     * @param db 数据库类型
     * @return
     * @throws Exception 
     */
    public static EntityDao getDao(Class entity, DbProductName db) 
            throws Exception {
        EntityDao dao = null;
        if (entity == null) {
            return null;
        }
        if (db == null) {
            db = DbProductName.Postgresql;
        }
        String factName = db + "_" + entity.getName();
        EntityFactory fact = entityFacts.get(factName);
        if (fact == null) {
            elock.lock();
            try {
                fact = entityFacts.get(factName);
                if (fact == null) {
                    DefaultManager manager = DefaultManager.getInstance();
                    Class bcls = Class.forName("com.easyea.edao.builders." 
                                + db + "Builder");
                    Class[]  ts = new Class[1];
                    ts[0] = DaoManager.class;
                    Object[] os = new Object[1];
                    os[0] = manager;
                    Constructor cons = bcls.getConstructor(ts);
                    //Builder builder = (Builder)bcls.newInstance();
                    Builder builder = (Builder)cons.newInstance(os);
                    fact = manager.getEntityFactory(entity, builder);
                    if (fact != null) {
                        entityFacts.put(factName, fact);
                    }
                    EntityFactory tfact = entityFacts.putIfAbsent(factName, fact);
                    if (tfact != null) {
                        fact = tfact;
                    }
                }
            } catch (Exception e) {
                throw e;
            } finally {
                elock.unlock();
            }
        }
        if (fact != null) {
            dao = fact.getDao();
        }
        return dao;
    }
    
    /**
     * @throws java.lang.Exception
     * @deprecated
     * @param name
     * @param con
     * @return 
     */
    public static EntityDao getDao(String name, Connection con) 
            throws Exception {
        return getEntityDao(name, con);
    }
    
    /**
     * @throws java.lang.Exception
     * @deprecated
     * @param name
     * @param con
     * @return 
     */
    public static EntityDao getEntityDao(String name, Connection con) 
            throws Exception {
        EntityDao dao = getEntityDao(name, "Postgresql");
        if (dao != null) {
            dao.setConnect(con);
        }
        return dao;
    }
    
    /**
     * 根据制定的数据库类型获取一个制定名称的Dao实现
     * @deprecated 
     * @param name dao的名称
     * @param db 数据库累心
     * @return
     * @throws Exception 
     */
    public static EntityDao getEntityDao(String name, String db) 
            throws Exception {
        EntityDao dao = null;
        int daoStr = name.lastIndexOf(".dao.");
        String entityName = name.substring(daoStr+5);
        String clsName = name.substring(0, daoStr) + ".entity." + 
                entityName.substring(0, entityName.length()-3);
        try {
            Class cls = Class.forName(clsName);
            dao = getDao(cls, DbProductName.valueOf(db));
        } catch (Exception e) {
            logger.debug("dao=[{}]clsName=[{}]", dao, clsName);
            e.printStackTrace();
            clsName = name.substring(0, daoStr) + ".entitybean." + 
                entityName.substring(0, entityName.length()-3);
            try {
                Class cls = Class.forName(clsName);
                dao = getDao(cls, DbProductName.valueOf(db));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        logger.debug("dao=[{}]clsName=[{}]", dao, clsName);
        return dao;
    }
    
    /**
     * 根据JOPO的Class获取ViewDao。
     * @param view 试图或者持久化Bean的Class
     * @return
     * @throws Exception 
     */
    public static ViewDao getViewDao(Class view) throws Exception {
        return getViewDao(view, DbProductName.Postgresql);
    }
    /**
     * 根据JOPO的Class以及数据库类型获取ViewDao。
     * @param view 试图或者持久化Bean的Class
     * @param db 数据库类型
     * @return
     * @throws Exception 
     */
    public static ViewDao getViewDao(Class view, DbProductName db) 
            throws Exception {
        ViewDao dao = null;
        if (db == null) {
            db = DbProductName.Postgresql;
        }
        String factName = db + "_" + view.getName();
        ViewFactory fact = viewFacts.get(factName);
        if (fact == null) {
            vlock.lock();
            try {
                fact = viewFacts.get(factName);
                if (fact == null) {
                    DefaultManager manager = DefaultManager.getInstance();
                    Class bcls = Class.forName("com.easyea.edao.builders." 
                                + db + "Builder");
                    Class[]  ts = new Class[1];
                    ts[0] = DaoManager.class;
                    Object[] os = new Object[1];
                    os[0] = manager;
                    Constructor cons = bcls.getConstructor(ts);
                    //Builder builder = (Builder)bcls.newInstance();
                    Builder builder = (Builder)cons.newInstance(os);
                    fact = manager.getViewDaoFactory(view, builder);
                    ViewFactory tfact = viewFacts.putIfAbsent(factName, fact);
                    if (tfact != null) {
                        fact = tfact;
                    }
                }
            } catch (Exception e) {
                throw e;
            } finally {
                vlock.unlock();
            }
        }
        if (fact != null) {
            dao = fact.getDao();
        }
        return dao;
    }
    
    /**
     * 根据指定的ViewDao的名称以及改ViewDao的数据库类型，生成一个ViewDao的实现对象
     * @deprecated 
     * @param name ViewDao的全名
     * @param con 数据库链接
     * @return
     * @throws Exception 
     */
    public static ViewDao getViewDao(String name, Connection con) throws Exception {
        ViewDao dao = getViewDao(name, "Postgresql");
        dao.setConnect(con);
        return dao;
    }
    
    /**
     * 根据指定的ViewDao的名称以及改ViewDao的数据库类型，生成一个ViewDao的实现对象
     * @deprecated 
     * @param name ViewDao的全名
     * @param db 数据库类型
     * @return
     * @throws Exception 
     */
    public static ViewDao getViewDao(String name, String db) throws Exception {
        ViewDao dao = null;
        int daoStr = name.lastIndexOf(".viewdao.");
        String entityName = name.substring(daoStr+9);
        String clsName = name.substring(0, daoStr) + ".entity." + 
                entityName.substring(0, entityName.length()-3);
        try {
            Class cls = Class.forName(clsName);
            dao = getViewDao(cls, DbProductName.valueOf(db));
        } catch (Exception e) {
            logger.debug("dao=[{}]clsName=[{}]", dao, clsName);
            e.printStackTrace();
            clsName = name.substring(0, daoStr) + ".view." + 
                entityName.substring(0, entityName.length()-3);
            try {
                Class cls = Class.forName(clsName);
                dao = getViewDao(cls, DbProductName.valueOf(db));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        logger.debug("dao=[{}]clsName=[{}]", dao, clsName);
        return dao;
    }
    
    /**
     * 获取Postgreql的MapDao的实现
     * @return 返回Postgresql的MapDao的实现
     * @throws Exception 
     */
    public static MapDao getMapDao() throws Exception {
        MapDao vdao = getMapDao(DbProductName.Postgresql);
        return vdao;
    }
    
    /**
     * 获取一个指定数据库类型名称MapDao的实现
     * @param db 数据库类型的名称
     * @return 返回一个指定数据库类型名称的MapDao的实现
     * @throws Exception 
     */
    public static MapDao getMapDao(DbProductName db) throws Exception {
        MapDao dao = null;
        if (db == null) {
            db = DbProductName.Postgresql;
        }
        String name = "com.easyea.mapdao." + db + "MapDao";
        String factName = db + "_" + name;
        MapFactory fact = mapFacts.get(factName);
        if (fact == null) {
            mlock.lock();
            try {
                fact = mapFacts.get(factName);
                if (fact == null) {
                    DefaultManager manager = DefaultManager.getInstance();
                    Class bcls = Class.forName("com.easyea.edao.builders." 
                                + db + "Builder");
                    Class[]  ts = new Class[1];
                    ts[0] = DaoManager.class;
                    Object[] os = new Object[1];
                    os[0] = manager;
                    Constructor cons = bcls.getConstructor(ts);
                    //Builder builder = (Builder)bcls.newInstance();
                    Builder builder = (Builder)cons.newInstance(os);
                    fact = manager.getMapDaoFactory(builder);
                    MapFactory tfact = mapFacts.putIfAbsent(factName, fact);
                    if (tfact != null) {
                        fact = tfact;
                    }
                }
            } catch (Exception e) {
                throw e;
            } finally {
                mlock.unlock();
            }
        }
        if (fact != null) {
            dao = fact.getDao();
        }
        return dao;
    }
    
    /**
     * 获取一个指定数据库类型名称MapDao的实现
     * @param db 数据库类型的名称
     * @return 返回一个指定数据库类型名称的MapDao的实现
     * @throws Exception 
     */
    public static MapDao getMapDao(String db) throws Exception {
        return getMapDao(DbProductName.valueOf(db));
    }
}
