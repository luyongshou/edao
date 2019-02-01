/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.easyea.edao;

import java.sql.Connection;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;

/**
 * 定义数据库分库分表相关的API
 * @author louis
 */
public interface ShardingDSSession {
    
    /**
     * 获取垮库操作的DataSource
     * @param entityCls 持久化对象的POJO
     * @return 返回DataSource
     */
    DataSource getDataSource(Class entityCls);
    
    /**
     * 根据持久化Bean的类以及分区字段的值获取该数据所使用的StatementSession。
     * @param entityCls 持久化Bean的类
     * @param v 分区字段的值
     * @return 返回该对象所在的数据库操作的StatementSession对象
     */
    DataSource getDataSource(Class entityCls, Object v);
    
    /**
     * 根据持久化Bean的类以及分区字段的值列表获取数据列表所使用的StatementSession的Map
     * @param <T>
     * @param entityCls 持久化Bean的类
     * @param vals 分区字段值的列表
     * @return 返回该对象所在的数据库操作的StatementSession对象Map
     */
    <T> Map<DataSource, List<T>> getDataSources(Class entityCls, List<T> vals);
    /**
     * 获取数据库StatementSession
     * @param con
     * @return 
     */
    StatementSession getSingleStmtSession(Connection con);
    /**
     * 获取批量数据库操作的StatementSession
     * @param con
     * @return 
     */
    StatementSession getBatchStmtSession(Connection con);
}
