/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.easyea.edao;

import com.easyea.edao.exception.EntityException;
import com.easyea.edao.partition.PartitionParam;
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
     * 该Dao的数据表是否进行了字段的同步
     * @return 
     */
    public boolean isSyncField();
    
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
     * @param con
     * @throws Exception 
     */
    public void syncDdl(Connection con) throws Exception;
    /**
     * 根据持久化Bean获取一个分区的参数
     * @return 返回持久化Bean分区的参数设置
     * @throws EntityException
     * @throws Exception 
     */
    public PartitionParam getPartitionParam() throws EntityException, Exception;
    
}
