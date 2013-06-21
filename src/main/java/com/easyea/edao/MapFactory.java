/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.easyea.edao;

/**
 *
 * @author louis
 */
public interface MapFactory {
    /**
     * 获取一个MapDao的实现
     * @return MapDao的实现对象
     * @throws Exception 
     */
    public MapDao getDao() throws Exception;
}
