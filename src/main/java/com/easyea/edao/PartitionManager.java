/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.easyea.edao;

/**
 *
 * @author louis
 * @param <T>
 */
public interface PartitionManager<T> {
    /**
     * 根据实体Bean的对象获取分区表的扩展名称
     * @param entity 实体Bean对象
     * @return 
     */
    public String getExtTableString(T entity);
}
