package com.easyea.edao;

import com.easyea.edao.annotation.TemporalType;
import java.io.Serializable;


/**
 * 用于查询时向DAO函数传递查询参数的数据类型
 * 
 * @author <a href="mailto:louis@easyea.com">louis</a>
 * 
 */
public class QueryParam implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -4314718654124124102L;
    /**
     * 参数在查询中的位置信息或者名称信息。如果是int则为位置参数，如果是String则为名称参数
     */
    private Object position;
    /**
     * 查询参数的值
     */
    private Object value;
    /**
     * 查询参数如果值为Date或者Calendar类型时，设定时间的值
     */
    private TemporalType temporalType;

    public QueryParam() {
        this.position = null;
        this.value = null;
        this.temporalType = null;
    }

    public QueryParam(Object position, Object value) {
        this.position = position;
        this.value = value;
        this.temporalType = null;
    }

    public QueryParam(Object position, Object value, TemporalType temporalType) {
        this.position = position;
        this.value = value;
        this.temporalType = temporalType;
    }

    public void setPosition(Object position) {
        this.position = position;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public void setTemporalType(TemporalType temporalType) {
        this.temporalType = temporalType;
    }

    public Object getPosition() {
        return this.position;
    }

    public Object getValue() {
        return this.value;
    }

    public TemporalType getTemporalType() {
        return this.temporalType;
    }
}
