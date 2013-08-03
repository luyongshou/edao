/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.easyea.edao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author louis
 */
public interface MapDao {
    
    /**
     * 设置该接口持有的数据库连接
     * @param con 数据库连接
     */
    public void setConnect(Connection con);

    /**
     * 返回该DAO持有的数据库连接
     * @return 数据库连接
     */
    public Connection getConnect();
    
    /**
     * 获取一个Map的列表，用于一些简单结果集的数据返回
     * @param sql 查询结果集的SQL语句
     * @return 
     * @throws java.sql.SQLException 
     * @throws java.lang.Exception 
     */
    public List<Map<String, Object>> getList(String sql) 
            throws SQLException, Exception;
    
    /**
     * 根据查询的SQL语句以及绑定的参数获取简单的记录集
     * @param sql 查询的SQL语句
     * @param params 绑定的参数
     * @return 
     * @throws java.sql.SQLException 
     * @throws java.lang.Exception 
     */
    public List<Map<String, Object>> getList(String sql, 
            ArrayList<QueryParam> params) throws SQLException, Exception;
    
    /**
     * 根据查询条件从某个开始位置获取制定数量的记录集
     * @param sql 查询的SQL语句
     * @param params 绑定的参数
     * @param start 开始取数据的位置
     * @param counts 去数据的数量
     * @return 
     * @throws java.sql.SQLException 
     * @throws java.lang.Exception 
     */
    public List<Map<String, Object>> getList(String sql, 
            ArrayList<QueryParam> params, long start, int counts)
            throws SQLException, Exception;
    /**
     * 获取一个单条记录的简单的Map字段名为键，字段的值为Map的值
     * @param sql 查询的SQL语句
     * @return 
     * @throws java.sql.SQLException 
     * @throws java.lang.Exception 
     */
    public Map<String, Object> getMap(String sql) throws SQLException, Exception;
    /**
     * 根据查询SQL以及绑定的参数类型获取一个单条数据的Map
     * @param sql 查询的SQL语句
     * @param params 绑定的参数
     * @return 
     * @throws java.sql.SQLException 
     * @throws java.lang.Exception 
     */
    public Map<String, Object> getMap(String sql, ArrayList<QueryParam> params)
            throws SQLException, Exception;
}
