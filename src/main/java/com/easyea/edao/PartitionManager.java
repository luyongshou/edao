/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.easyea.edao;

import com.easyea.edao.partition.PartitionParam;

/**
 *
 * @author louis
 */
public interface PartitionManager {
    /**
     * 根据实体Bean的对象获取分区表的扩展名称
     * @param <T> 实体Bean对象类型
     * @param entity 实体Bean对象
     * @param param 分区的设置参数
     * @return 
     */
    public <T> String getExtTableString(T entity, PartitionParam param);
}
