/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.easyea.edao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author louis
 * @param <T>
 */
public interface ViewDao<T> {
    
    /**
     * @deprecated 
     * @param con 
     */
    public void setConnect(Connection con);
    
    public void setStatementSession(StatementSession sessioin);
    
    /**
     * 获取所有该类持久化对象列表
     *
     * @return 持久化对象列表
     * @throws java.sql.SQLException
     * @throws java.lang.Exception
     */
    public List<T> getList() throws SQLException, Exception;

    /**
     * 根据hq sql 语句获取所有符合qlString的持久化对象
     *
     * @param qlString
     *            查询语句
     * @return 持久化对象列表
     * @throws java.sql.SQLException
     * @throws java.lang.Exception
     */
    public List<T> getList(String qlString) throws SQLException, Exception;

    /**
     * 获取一定数量符合条件的持久化对象列表
     *
     * @param qlString
     *            qlString 查询语句
     * @param start
     *            开始获取对象的位置
     * @param counts
     *            获取对象的个数
     * @return 持久化对象列表
     * @throws java.sql.SQLException
     * @throws java.lang.Exception
     */
    public List<T> getList(String qlString, long start, int counts) 
            throws SQLException, Exception;

    /**
     * 根据设置位置和名称参数的qlString条件的持久化对象
     *
     * @param qlString
     *            qlString 查询语句
     * @param params
     *            参数列表
     * @return 持久化对象列表
     * @throws java.sql.SQLException
     * @throws java.lang.Exception
     */
    public List<T> getList(String qlString, ArrayList<QueryParam> params)
             throws SQLException, Exception;

    /**
     * 获取一定数量符合条件的持久化对象列表
     *
     * @param qlString
     *            qlString 查询语句
     * @param params
     *            参数列表
     * @param start
     *            开始获取对象的位置
     * @param counts
     *            获取对象的个数
     * @return 持久化对象列表
     * @throws java.sql.SQLException
     * @throws java.lang.Exception
     */
    public List<T> getList(String qlString, ArrayList<QueryParam> params,
            long start, int counts) throws SQLException, Exception;
    
    /**
     * 获取该类所有持久化对象的个数
     *
     * @return 总个数
     * @throws java.sql.SQLException
     * @throws java.lang.Exception
     */
    public long getTotal() throws SQLException, Exception;

    /**
     * 获取该类所有持久化对象的个数
     *
     * @param qlString
     * @return 符合条件总个数
     * @throws java.sql.SQLException
     * @throws java.lang.Exception
     */
    public long getTotal(String qlString) throws SQLException, Exception;

    /**
     * 根据指定的条件查询所有符合条件记录总数
     * @param qlString
     * @param params
     * @return
     * @throws java.sql.SQLException
     * @throws java.lang.Exception
     */
    public long getTotal(String qlString, ArrayList<QueryParam> params)
             throws SQLException, Exception;

}
