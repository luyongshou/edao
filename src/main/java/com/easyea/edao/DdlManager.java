/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.easyea.edao;

import java.sql.Connection;
import java.util.Date;

/**
 * Ddl管理器接口，该对象
 * @author louis
 */
public interface DdlManager {
    /**
     * 该Dao的数据表是否已经同步
     * @return 
     */
    public boolean isSync();
    
    /**
     * Dao是否进行数据分区
     * @return 
     */
    public boolean getIsPartition();
    /**
     * Ddl最后同步时间
     * @return 
     */
    public Date getLastSyncTime();
    /**
     * Ddl下一次同步的时间用于数据分区时判断是否应该创建分区表
     * @return 
     */
    public Date getNextSyncTime();
    /**
     * 同步数据表结构，如果为分区表则需要创建所需的分区表
     * @param entity
     * @param con
     * @throws Exception 
     */
    public void syncDdl(Connection con) throws Exception;
}
