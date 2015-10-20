/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.easyea.edao;

import com.easyea.edao.exception.EntityException;

/**
 * 定义dao实现类的工厂类接口，动态编译生成的dao实现的工厂类都继承该接口
 * @author louis
 * @param <T>
 */
public interface EntityFactory<T> {
    /**
     * 根据dao实现的名称获取一个dao实现的对象。该接口的实现应有new操作符来生成而避免由
     * class.newInstance()生成带来的性能损失。
     * @return 返回dao实现的一个对象
     * @throws EntityException dao名称错误或者持久化bean不符合规范的错误
     * @throws Exception 编译以及其他错误
     */
    public EntityDao<T> getDao() throws EntityException, Exception;
}
