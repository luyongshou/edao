package com.easyea.edao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 操作持久化对象的DAO接口
 * 
 * @author <a href="mailto:louis@easyea.com">louis</a>
 * 
 */
public interface EntityDao {
    /**
     * 获取持久化Dao的Ddl管理器
     * @return 
     */
    public DdlManager getDdlManager();
    /**
     * 设置持久化Dao的Ddl管理器
     * @param ddlManager 
     */
    public void setDdlManager(DdlManager ddlManager);

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
     * 把一个角色的对象保存到容器或者数据库中去
     *
     * @param entity
     *            持久化对象
     * @return 如果保存成功返回持久化对象如果失败返回null
     */
    public <T> T persist(T entity) throws SQLException, Exception;
    
    /**
     * 批量保存数据
     * @param entities 需要保存的持久化Bean列表
     * @throws SQLException 执行SQL语句如果有错则返回错误
     * @throws Exception 如果有其他的错误则返回相应的错误
     */
    public void persist(List<?> entities) throws SQLException, Exception;

    /**
     * 根据主键获取一个持久化的对象
     *
     * @param primaryKey
     *            角色的主键
     * @return 如果成功返回一个角色对象如果失败返回null
     */
    public <T> T getEntityById(Object primaryKey) throws SQLException, Exception;

    /**
     * 获取所有该类持久化对象列表
     *
     * @return 持久化对象列表
     */
    public List getList() throws SQLException, Exception;

    /**
     * 根据hq sql 语句获取所有符合qlString的持久化对象
     *
     * @param qlString
     *            查询语句
     * @return 持久化对象列表
     */
    public List getList(String qlString) throws SQLException, Exception;

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
     */
    public List getList(String qlString, long start, int counts) 
            throws SQLException, Exception;

    /**
     * 根据设置位置和名称参数的qlString条件的持久化对象
     *
     * @param qlString
     *            qlString 查询语句
     * @param params
     *            参数列表
     * @return 持久化对象列表
     */
    public List getList(String qlString, ArrayList<QueryParam> params) 
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
     */
    public List getList(String qlString, ArrayList<QueryParam> params,
            long start, int counts) throws SQLException, Exception;

    /**
     * 更新一个持久化对象到持久化容器或者数据库中
     *
     * @param <T> 需要更新的持久化Bean的类型
     * @param entity 需要持久化的持久化对象
     * @return 返回修改成功的持久化bean
     */
    public <T> T merge(T entity) throws SQLException, Exception;

    /**
     * 获取该类所有持久化对象的个数
     *
     * @return 总个数
     */
    public long getTotal() throws SQLException, Exception;

    /**
     * 获取该类所有持久化对象的个数
     *
     * @return 符合条件总个数
     */
    public long getTotal(String qlString) throws SQLException, Exception;

    /**
     * 根据指定的条件查询所有符合条件记录总数
     * @param qlString
     * @param params
     * @return 返回服务器条件的记录总数
     */
    public long getTotal(String qlString, ArrayList<QueryParam> params) 
            throws SQLException, Exception;

    /**
     * 批量更新持久化对象
     *
     * @param qlString
     * @return 更新影响的个数，如果错误返回-1
     */
    public int update(String qlString) throws SQLException, Exception;

    /**
     * 批量更新持久化对象
     *
     * @param qlString
     * @param params
     * @return 更新影响的个数，如果错误返回-1
     */
    public int update(String qlString, ArrayList<QueryParam> params) 
            throws SQLException, Exception;

    /**
     * 从容器或者数据库中删除一个持久化的对象
     *
     * @param entity
     *            持久化对象
     * @return 是否删除成功
     */
    public boolean remove(Object entity) throws SQLException, Exception;

    /**
     * 批量删除持久化对象
     *
     * @param qlString
     * @return 返回删除对象的个数失败返回-1
     */
    public int remove(String qlString) throws SQLException, Exception;

    /**
     * 批量删除持久化对象
     *
     * @param qlString
     * @param params
     * @return 返回删除对象的个数失败返回-1
     */
    public int remove(String qlString, ArrayList<QueryParam> params) 
            throws SQLException, Exception;
}
