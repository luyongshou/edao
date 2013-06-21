/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.easyea.edao.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 注释一个字段是否创建数据库的索引
 * @author louis
 */
@Target({METHOD, FIELD}) @Retention(RUNTIME)
public @interface Key {
    /**
     * 定义索引的名称
     * @return 返回索引名称
     */
    String name() default "";
    
    /**
     * 索引类型
     * @return 返回索引类型的字符串 
     */
    String type() default "";
    
    /**
     * 该属性是否有唯一索引不允许有相同的值
     * @return 如果为true则说明该属性在数据库中不允许有重复的值
     */
    boolean unique() default false;
}
