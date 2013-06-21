/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.easyea.edao;

import com.easyea.edao.exception.EntityException;
import com.easyea.edao.exception.ViewException;

/**
 *  DAO实现生成器的接口
 * @author louis
 */
public interface Builder {
    /**
     * 根据给定的持久化的Bean反射bean并生成实现EntityDao接口实现类的Java代码
     * @param entityCls 持久化的javabean
     * @return 生成的DAO实现的Java代码
     */
    public String getDaoCode(Class entityCls) throws EntityException;

    /**
     * 根据指定的View的javabean生成ViewDao实现类的Java代码
     * @param viewCls View性质的javabean
     * @return 生成的ViewDao实现的Java代码
     */
    public String getViewDaoCode(Class viewCls) throws ViewException;
    
    /**
     * 生成返回一个简单结果集的Dao实现
     * @return
     * @throws Exception 
     */
    public String getMapDaoCode() throws Exception;
    /**
     * 获取数据库类型的名称比如Postgresql,Oracle,Mysql
     * @return 
     */
    public String getDbTypeName();
}
