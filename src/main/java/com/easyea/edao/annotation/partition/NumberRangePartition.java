/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.easyea.edao.annotation.partition;

import com.easyea.edao.partition.NumberInterval;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 按整数进行分区的注释
 * @author louis
 */
@Target(ElementType.TYPE) @Retention(RetentionPolicy.RUNTIME)
public @interface NumberRangePartition {
    /**
     * 间隔的类型
     * @return 
     */
    NumberInterval interval() default NumberInterval.NUMBER_MILLION;
    /**
     * 每几个间隔保存到一张表内
     * @return 
     */
    int count() default 10;
    /**
     * 用于分区的属性
     * @return 
     */
    String field();
}
