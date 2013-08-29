/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.easyea.edao.partition;

/**
 * 定义按时间端分区时默认的范围间隔，比如每年，每月，每1000万每100万等
 * @author louis
 */
public enum TimeInterval {
    /**
     * 间隔单位为年
     */
    TIME_YEAR,
    /**
     * 间隔单位为月
     */
    TIME_MONTH,
    /**
     * 间隔单位为天
     */
    TIME_DAY,
    
}
