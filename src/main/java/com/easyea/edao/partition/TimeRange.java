/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.easyea.edao.partition;

import java.util.Date;
import java.util.List;

/**
 * 按范围分区的分区设置参数
 * @author louis
 */
public class TimeRange extends PartitionParam {
    /**
     * 分区时默认的范围间隔设置
     */
    private TimeInterval interval;
    /**
     * 每张表间隔几个单位
     */
    private int          count;
    /**
     * 自定义时间的范围来进行分表
     */
    private List<Date>   customerRange;
    
    public TimeRange() {
        super.setType(Type.RANGE);
    }

    /**
     * @return the interval
     */
    public TimeInterval getInterval() {
        return interval;
    }

    /**
     * @param interval the interval to set
     */
    public void setInterval(TimeInterval interval) {
        this.interval = interval;
    }

    /**
     * @return the count
     */
    public int getCount() {
        return count;
    }

    /**
     * @param count the count to set
     */
    public void setCount(int count) {
        this.count = count;
    }

    /**
     * @return the customerRange
     */
    public List<Date> getCustomerRange() {
        return customerRange;
    }

    /**
     * @param customerRange the customerRange to set
     */
    public void setCustomerRange(List<Date> customerRange) {
        this.customerRange = customerRange;
    }
}
