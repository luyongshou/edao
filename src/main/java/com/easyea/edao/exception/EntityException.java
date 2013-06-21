/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.easyea.edao.exception;

/**
 * 持久化Javabean的错误类型，比如包名不符合规则，缺少必要的属性，get，set的方法不全等等
 * @author louis
 */
public class EntityException extends Exception {
    public EntityException(){
        super();
    }

    public EntityException(String message, Throwable cause){
        super(message, cause);
    }

    public EntityException(String message){
        super(message);
    }

    public EntityException(Throwable cause){
        super(cause);
    }
}
