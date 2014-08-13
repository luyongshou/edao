/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.easyea.edao;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * @author louis
 */
public class DdlFactory {
    
    private static final ConcurrentHashMap<String, Ddl> ddlFacts =
            new ConcurrentHashMap<String, Ddl>();
    private final static ReentrantLock lock = new ReentrantLock();
    
    /**
     * 根据数据库类型获取对应数据的Ddl用来操作DDL语句
     * @param dbType
     * @return 
     * @throws java.lang.Exception 
     */
    public static Ddl getDdl(String dbType) throws Exception {
        Ddl ddl = ddlFacts.get(dbType);
        if (ddl == null) {
            lock.lock();
            try {
                if (ddl == null) {
                    Class ddlcls = Class.forName("com.easyea.edao.ddls." + dbType + "Ddl");
                    ddl = (Ddl)ddlcls.newInstance();
                    ddlFacts.put(dbType, ddl);
                }
            } catch (InstantiationException ex) {
                throw ex;
            } catch (IllegalAccessException ex) {
                throw ex;
            } catch (ClassNotFoundException ex) {
                throw ex;
            } finally {
                lock.unlock();
            }
        }
        return ddl;
    }
}
