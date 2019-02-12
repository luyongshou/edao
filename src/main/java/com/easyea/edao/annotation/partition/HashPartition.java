/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.easyea.edao.annotation.partition;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * @author louis
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface HashPartition {
    /**
     * 用于分区的属性
     * @return 
     */
    String field();
    /**
     * 按hash划分为几个分区，默认为4个分区
     * @return 
     */
    int count() default 4;
}