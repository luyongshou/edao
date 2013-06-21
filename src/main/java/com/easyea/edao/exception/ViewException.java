/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.easyea.edao.exception;

/**
 * 试图的Javabean错误类型，比如包名不符合规则等等
 * @author louis
 */
public class ViewException extends Exception {
    public ViewException(){
        super();
    }

    public ViewException(String message, Throwable cause){
        super(message, cause);
    }

    public ViewException(String message){
        super(message);
    }

    public ViewException(Throwable cause){
        super(cause);
    }
}
