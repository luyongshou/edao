/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.easyea.edao;

import com.easyea.edao.exception.EntityException;
import com.easyea.edao.manager.DefaultManager;
import java.sql.Connection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * dao的工厂类
 *
 * @author louis
 */
public class DaoFactory {

    private static ConcurrentHashMap<String, EntityFactory> entityFacts =
            new ConcurrentHashMap<String, EntityFactory>();
    private static ConcurrentHashMap<String, ViewFactory> viewFacts =
            new ConcurrentHashMap<String, ViewFactory>();
    private static ConcurrentHashMap<String, MapFactory> mapFacts =
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
     * 根据dao实现的名称获取一个持久化dao的实现对象，dao实现的名称为包名为dao结尾，类名以Dao结尾。
     * 持久化bean的命名规则包名以entity或者entitybean结尾。
     *
     * @param name dao实现的类名（包含包名的全名）
     * @return dao实现的对象
     * @throws Exception 如果获取dao实现时出现的各类错误，包括类不符合规范，以及编译的错误等等
     */
    public static EntityDao getEntityDao(String name) throws Exception {
        return getEntityDao(name, "Postgresql");
    }
    
    /**
     * 根据dao实现的名称获取一个持久化dao的实现对象，dao实现的名称为包名为dao结尾，类名以Dao结尾。
     * 持久化bean的命名规则包名以entity或者entitybean结尾。
     *
     * @param name dao实现的类名（包含包名的全名）
     * @return dao实现的对象
     * @throws Exception 如果获取dao实现时出现的各类错误，包括类不符合规范，以及编译的错误等等
     */
    public static EntityDao getDao(String name) throws Exception {
        return getEntityDao(name, "Postgresql");
    }
    /**
     * 根据持久化对象的Class来获取一个持久化Dao的实现
     * @param entity 持久Bean的Class对象
     * @return
     * @throws Exception 
     */
    public static EntityDao getDao(Class entity) throws Exception {
        return getDao(entity, "Postgresql");
    }
    /**
     * 根据持久化对象的Class以及制定数据类型来获取一个持久化Dao的实现
     * @param entity 持久Bean的Class对象
     * @param db 数据库类型
     * @return
     * @throws Exception 
     */
    public static EntityDao getDao(Class entity, String db) throws Exception {
        String packName = entity.getPackage().getName();
        if (!packName.endsWith(".entity")) {
            throw new EntityException("package not endwith \"entity\"!");
        }
        int lastDot = packName.lastIndexOf("entity");
        if (lastDot == -1) {
            throw new EntityException("package not endwith \"entity\"!");
        } else {
            packName = packName.substring(0, lastDot) + "dao";
        }
        String name = packName + "." + entity.getSimpleName() + "Dao";
        return getEntityDao(name, db);
    }
    
    /**
     * 根据dao实现的名称获取一个持久化dao的实现对象，dao实现的名称为包名为dao结尾，类名以Dao结尾。
     * 持久化bean的命名规则包名以entity或者entitybean结尾。
     *
     * @param name dao实现的类名（包含包名的全名）
     * @param con 应用于改Dao的数据库连接
     * @return dao实现的对象
     * @throws Exception 如果获取dao实现时出现的各类错误，包括类不符合规范，以及编译的错误等等
     */
    public static EntityDao getDao(String name, Connection con) throws Exception {
        return getEntityDao(name, con);
    }
    
    /**
     * 根据dao实现的名称获取一个持久化dao的实现对象，dao实现的名称为包名为dao结尾，类名以Dao结尾。
     * 持久化bean的命名规则包名以entity或者entitybean结尾。
     * @param name dao实现的类名（包含包名的全名）
     * @param con 应用于改Dao的数据库连接
     * @return dao实现的对象
     * @throws Exception 如果获取dao实现时出现的各类错误，包括类不符合规范，以及编译的错误等等
     */
    public static EntityDao getEntityDao(String name, Connection con) 
            throws Exception {
        EntityDao dao = getEntityDao(name, "Postgresql");
        dao.setConnect(con);
        return dao;
    }
    
    /**
     * 根据制定的数据库类型获取一个制定名称的Dao实现
     * @param name dao的名称
     * @param db 数据库累心
     * @return
     * @throws Exception 
     */
    public static EntityDao getEntityDao(String name, String db) throws Exception {
        EntityDao dao = null;
        if (db == null) {
            db = "";
        }
        if (db.length() == 0) {
            return null;
        }
        String factName = db + "_" + name;
        EntityFactory fact = entityFacts.get(factName);
        if (fact == null) {
            elock.lock();
            try {
                fact = entityFacts.get(factName);
                if (fact == null) {
                    DefaultManager manager = DefaultManager.getInstance();
                    Class bcls = Class.forName("com.easyea.edao.builders." 
                                + db.substring(0, 1).toUpperCase() 
                                + db.substring(1) + "Builder");
                    Builder builder = (Builder)bcls.newInstance();
                    fact = manager.getEntityFactory(name, builder, db);
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
            dao = fact.getDao(name);
        }
        return dao;
    }
    /**
     * 根据JOPO的Class获取ViewDao。
     * @param view 试图或者持久化Bean的Class
     * @return
     * @throws Exception 
     */
    public static ViewDao getViewDao(Class view) throws Exception {
        return getViewDao(view, "Postgresql");
    }
    /**
     * 根据JOPO的Class以及数据库类型获取ViewDao。
     * @param view 试图或者持久化Bean的Class
     * @param db 数据库类型
     * @return
     * @throws Exception 
     */
    public static ViewDao getViewDao(Class view, String db) throws Exception {
        String packName = view.getPackage().getName();
        if (!packName.endsWith(".view") && packName.endsWith(".entity")) {
            throw new EntityException("package not endwith \"entity\" and \"view\"!");
        }
        int lastDot = packName.lastIndexOf(".");
        if (lastDot == -1) {
            throw new EntityException("package not endwith \"entity\" and \"view\"!");
        } else {
            packName = packName.substring(0, lastDot) + "viewdao";
        }
        String name = packName + "." + view.getSimpleName() + "Dao";
        return getViewDao(name, db);
    }

    /**
     * 根据视图dao实现的名称获取一个视图dao的实现对象，dao实现的名称为包名为viewdao结尾，
     * 类名以Dao结尾。视图bean的命名规则包名以view结尾。
     * 
     * @param name 视图dao实现的类名(包含包名的全名)
     * @return 返回视图dao实现的对象
     * @throws Exception 获取试图dao实现时的错误，包含视图bean不符合规范，以及编译时出现的错误。
     */
    public static ViewDao getViewDao(String name) throws Exception {
        return getViewDao(name, "Postgresql");
    }
    
    /**
     * 根据视图dao实现的名称获取一个视图dao的实现对象，dao实现的名称为包名为viewdao结尾，
     * 类名以Dao结尾。视图bean的命名规则包名以view结尾。
     * 
     * @param name 视图dao实现的类名(包含包名的全名)
     * @Param con 应用于改ViewDao的数据库连接
     * @return 返回视图dao实现的对象
     * @throws Exception 获取试图dao实现时的错误，包含视图bean不符合规范，以及编译时出现的错误。
     */
    public static ViewDao getViewDao(String name, Connection con) throws Exception {
        ViewDao vdao = getViewDao(name, "Postgresql");
        vdao.setConnect(con);
        return vdao;
    }
    /**
     * 获取Postgreql的MapDao的实现
     * @return 返回Postgresql的MapDao的实现
     * @throws Exception 
     */
    public static MapDao getMapDao() throws Exception {
        MapDao vdao = getMapDao("Postgresql");
        return vdao;
    }
    /**
     * 获取一个数据库类型名称以及附带数据库连接的MapDao实现
     * @param db 数据库类型名称
     * @param con 数据库连接
     * @return 附带数据库连接的MapDao的实现
     * @throws Exception 
     */
    public static MapDao getMapDao(String db, Connection con) throws Exception {
        MapDao vdao = getMapDao(db);
        vdao.setConnect(con);
        return vdao;
    }
    /**
     * 获取一个指定数据库类型名称MapDao的实现
     * @param db 数据库类型的名称
     * @return 返回一个指定数据库类型名称的MapDao的实现
     * @throws Exception 
     */
    public static MapDao getMapDao(String db) throws Exception {
        MapDao dao = null;
        if (db == null || db.length() == 0) {
            return null;
        }
        String name = "com.easyea.mapdao." + db.substring(0, 1).toUpperCase() 
                                + db.substring(1) + "MapDao";
        String factName = db + "_" + name;
        MapFactory fact = mapFacts.get(factName);
        if (fact == null) {
            mlock.lock();
            try {
                fact = mapFacts.get(factName);
                if (fact == null) {
                    DefaultManager manager = DefaultManager.getInstance();
                    Class bcls = Class.forName("com.easyea.edao.builders." 
                                + db.substring(0, 1).toUpperCase() 
                                + db.substring(1) + "Builder");
                    Builder builder = (Builder)bcls.newInstance();
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
     * 根据指定的ViewDao的名称以及改ViewDao的数据库类型，生成一个ViewDao的实现对象
     * @param name ViewDao的全名
     * @param db 数据库类型
     * @return
     * @throws Exception 
     */
    public static ViewDao getViewDao(String name, String db) throws Exception {
        ViewDao dao = null;
        if (db == null || db.length() == 0) {
            return null;
        }
        String factName = db + "_" + name;
        ViewFactory fact = viewFacts.get(factName);
        if (fact == null) {
            vlock.lock();
            try {
                fact = viewFacts.get(factName);
                if (fact == null) {
                    DefaultManager manager = DefaultManager.getInstance();
                    Class bcls = Class.forName("com.easyea.edao.builders." 
                                + db.substring(0, 1).toUpperCase() 
                                + db.substring(1) + "Builder");
                    Builder builder = (Builder)bcls.newInstance();
                    fact = manager.getViewDaoFactory(name, builder);
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
            dao = fact.getDao(name);
        }
        return dao;
    }
}
