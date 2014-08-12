/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.easyea.edao;

/**
 * 定义一个主键生成器的接口，如果主键不采用自增的方式，可以自己定一个主键发生器
 * @author louis
 * @param <T>
 */
public interface IdGeneration<T> {
    public T getId();
}
