/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.easyea.edao.partition;

/**
 * 定义分区配置的基本数据类型，Dao框架根据该配置文件进行相关的处理，该目录
 * @author louis
 */
public abstract class PartitionParam {
    /**
     * 分区类型，比如按范围（Range）分区，按Hash分区等
     */
    private Type   type;
    /**
     * 用于数据分区的Java对象的属性
     */
    private String field;
    /**
     * 用于分区的Java对象的数据类型
     */
    private Class  fieldType;

    /**
     * @return the type
     */
    public Type getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(Type type) {
        this.type = type;
    }

    /**
     * @return the field
     */
    public String getField() {
        return field;
    }

    /**
     * @param field the field to set
     */
    public void setField(String field) {
        this.field = field;
    }

    /**
     * @return the fieldType
     */
    public Class getFieldType() {
        return fieldType;
    }

    /**
     * @param fieldType the fieldType to set
     */
    public void setFieldType(Class fieldType) {
        this.fieldType = fieldType;
    }
    
}
