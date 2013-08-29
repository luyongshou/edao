/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.easyea.edao.annotation.partition;

import com.easyea.edao.partition.TimeInterval;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * @author louis
 */
@Target(ElementType.TYPE) @Retention(RetentionPolicy.RUNTIME)
public @interface TimeRangePartition {
    /**
     * 按时间分区时时间的间隔单位，默认为1个月一张表
     * @return 
     */
    TimeInterval interval() default TimeInterval.TIME_MONTH;
    /**
     * 每几个时间单位作为一张表来存储
     * @return 
     */
    int count() default 1;
    
    /**
     * 用于分区的属性
     * @return 
     */
    String field();
}
