/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.easyea.edao;

import com.easyea.edao.exception.EntityException;
import java.util.List;
import javax.sql.DataSource;

/**
 * 定义由持久化Bean生成数据库创建语句的接口
 * @author louis
 */
public interface Ddl {
    
    /**
     * 获取一个持久化bean所拥有的所有数据表，如果没有分区则只有一张数据表，如果分区表则有多条记录
     * @param entity 持久化Bean的Class
     * @return
     * @throws EntityException
     * @throws Exception 
     */
    public List<String> getTables(Class entity, DataSource ds) 
            throws EntityException, Exception;
    
    /**
     * 定义由持久化Bean获取创建对应数据表的SQL语句集合
     * @param entity 持久化Bean的class
     * @return 
     */
    public List<String> getEntityCreateDdl(Class entity) 
            throws EntityException, Exception;
    
    /**
     * 定义持久化Bean获取更新数据库结构的SQL语句的集合
     * @param entity
     * @return 
     */
    public List<String> getEntityUpdateDdl(Class entity, DataSource ds) 
            throws EntityException, Exception;
    
    public List<String> getViewCreateDdl(Class view) 
            throws EntityException, Exception;
    
    public List<String> getViewUpdateDdl(Class view, DataSource ds) 
            throws EntityException, Exception;
}
