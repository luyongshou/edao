/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.easyea.edao;

import com.easyea.edao.exception.ViewException;

/**
 * 调用动态编译的视图Dao实现类的工厂类，这样便将原来的daoclass.newInstance()，操作变为
 * "new daoclass()"操作使效率提高2个数量级。每个Dao的实现由一个dao的工厂类进行调用。该工厂类
 * 动态生成编译后由载入Dao实现类的classloader载入，直接返回new操作符对象。
 * @author louis
 */
public interface ViewFactory {
    public ViewDao getDao(String daoName) throws ViewException, Exception;
}
