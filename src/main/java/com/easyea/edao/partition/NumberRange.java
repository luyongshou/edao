/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.easyea.edao.partition;

import java.util.List;

/**
 *
 * @author louis
 */
public class NumberRange extends PartitionParam {
    /**
     * 整数间隔的单位
     */
    private NumberInterval interval;
    /**
     * 每个几个间隔单位为一张表
     */
    private int            count;
    /**
     * 自定义整数单位来创建表
     */
    private List<Long>     customerRange;
    
    public NumberRange() {
        super.setType(Type.RANGE);
    }

    /**
     * @return the interval
     */
    public NumberInterval getInterval() {
        return interval;
    }

    /**
     * @param interval the interval to set
     */
    public void setInterval(NumberInterval interval) {
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
    public List<Long> getCustomerRange() {
        return customerRange;
    }

    /**
     * @param customerRange the customerRange to set
     */
    public void setCustomerRange(List<Long> customerRange) {
        this.customerRange = customerRange;
    }
}
