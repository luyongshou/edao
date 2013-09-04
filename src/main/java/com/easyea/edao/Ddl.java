/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.easyea.edao;

import com.easyea.edao.exception.EntityException;
import com.easyea.edao.util.FieldInfo;
import java.sql.Connection;
import java.util.List;

/**
 * 定义由持久化Bean生成数据库创建语句的接口
 * @author louis
 */
public interface Ddl {
    
    /**
     * 获取一个持久化bean所拥有的所有数据表，如果没有分区则只有一张数据表，如果分区表则有多条记录
     * @param entity 持久化Bean的Class
     * @param con
     * @return
     * @throws EntityException
     * @throws Exception 
     */
    public List<String> getTables(Class entity, Connection con) 
            throws EntityException, Exception;
    
    public List<String> getColumns(Class entity, Connection con) 
            throws EntityException, Exception;
    
    public String getTableName(Class entity) throws EntityException, Exception;
    
    /**
     * 根据持久化Bean的属性获取该数据库添加该属性的SQL语句列表
     * @param tableName 
     * @param field 持久化Bean的字段对象
     * @return 
     */
    public List<String> getAddColumnSqls(String tableName, FieldInfo field);
    
    /**
     * 定义由持久化Bean获取创建对应数据表的SQL语句集合
     * @param entity 持久化Bean的class
     * @return 
     * @throws com.easyea.edao.exception.EntityException 
     */
    public List<String> getEntityCreateDdl(Class entity) 
            throws EntityException, Exception;
    
    /**
     * 根据持久化Bean的class对象以及扩展名来获取创建分区表的sql语句列表
     * @param entity 实体化Bean的class对象
     * @param extName
     * @return
     * @throws EntityException
     * @throws Exception 
     */
    public List<String> getEntityPartitionDdl(Class entity, String extName) 
            throws EntityException, Exception;
    
    /**
     * 定义持久化Bean获取更新数据库结构的SQL语句的集合
     * @param entity
     * @param con
     * @return 
     * @throws com.easyea.edao.exception.EntityException 
     */
    public List<String> getEntityUpdateDdl(Class entity, Connection con) 
            throws EntityException, Exception;
    
    public List<String> getViewCreateDdl(Class view) 
            throws EntityException, Exception;
    
    public List<String> getViewUpdateDdl(Class view, Connection con) 
            throws EntityException, Exception;
}
