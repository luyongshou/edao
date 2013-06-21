/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.easyea.edao;

import com.easyea.edao.exception.EntityException;
import com.easyea.edao.exception.ViewException;

/**
 * 定义Dao类管理的接口
 *
 * @author louis
 */
public interface DaoManager {

    /**
     * 根据制定的Dao名称，以及制定数据库的代码生成器获取一个Dao的实现，如果该实现不存在在根据
     * dao名称以及规则查找相应的持久化bean，如果持久化bean存在则反射持久化bean生成dao的实现
     * 代码，然后编译后由自定义的classloader载入。
     *
     * @param daoName dao实现的名称，持久化bean的包名以entity或者entitybean结尾。
     * @param builder 反射持久化bean生成dao实现代码的代码生成器。
     * @return 返回dao实现的class
     * @throws EntityException 如果持久化bean的包名、属性以及函数不符合规则的错误
     * @throws Exception 编译错误以及其他错误
     */
    public Class getDaoClass(String daoName, Builder builder)
            throws EntityException, Exception;

    /**
     * 根据指定试图dao的名称以及指定
     *
     * @param viewDaoName
     * @param builder
     * @return
     * @throws ViewException
     * @throws Exception
     */
    public Class getViewDaoClass(String viewDaoName, Builder builder)
            throws ViewException, Exception;
    
    /**
     * 根据数据库Builder获取一个MapDao实现的Class
     * @param builder
     * @return
     * @throws Exception 
     */
    public Class getMapDaoClass(Builder builder) throws Exception;

    /**
     * 根据指定的持久化dao的名称获取一个dao实现的工厂类。该工厂类来返回dao的实现。
     *
     * @param daoName 指定dao的名称
     * @param builder 
     * @return 返回dao的工厂类
     * @throws EntityException 持久化bean相关的错误，包名规范，属性以及函数错误。
     * @throws Exception 编译错误以及其他的错误
     */
    public EntityFactory getEntityFactory(String daoName, Builder builder)
            throws EntityException, Exception;
    /**
     * 根据指定的试图dao的名称返回试图dao的工厂类
     * 
     * @param daoName
     * @param builder 
     * @return
     * @throws ViewException
     * @throws Exception 
     */
    public ViewFactory getViewDaoFactory(String daoName, Builder builder)
            throws ViewException, Exception;
    
    /**
     * 根据数据库的Builder获取一个MapDao实现的工厂类
     * @param builder
     * @return
     * @throws Exception 
     */
    public MapFactory getMapDaoFactory(Builder builder) throws Exception;
}
