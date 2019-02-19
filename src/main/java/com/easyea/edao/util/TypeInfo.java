/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.easyea.edao.util;

import com.easyea.edao.util.ClassUtil.JdbcMethod;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 *
 * @author louis
 */
public class TypeInfo {
    private JdbcMethod  setMethod;
    private String  method;
    private Class   type;
    private String  temporal;
    private boolean isId;
    private boolean isObject;
    private boolean isJdbcObject;
    private String objClassName;
    
    public TypeInfo(JdbcMethod setMethod, String method, Class type, boolean isId) {
        this.setMethod = setMethod;
        this.method    = method;
        this.type      = type;
        this.temporal  = "";
        this.isId      = isId;
        this.isObject  = type.toString().startsWith("class ");
        this.isJdbcObject = isJdbcObject(type);
        if (isJdbcObject) {
            this.objClassName = type.getName() + ".class";
        }
    }
    
    public TypeInfo(JdbcMethod setMethod, String method, Class type, String temporal, 
            boolean isId) {
        this.setMethod = setMethod;
        this.method    = method;
        this.type      = type;
        this.temporal  = temporal;
        this.isId      = isId;
        this.isObject  = type.toString().startsWith("class ");
        this.isJdbcObject = isJdbcObject(type);
        if (isJdbcObject) {
            this.objClassName = type.getName() + ".class";
        }
    }
    
    private boolean isJdbcObject(Class type) {
        if (type.getName().equals(LocalDateTime.class.getName()) 
                || type.getName().equals(LocalDate.class.getName())
                || type.getName().equals(LocalTime.class.getName())) {
            return true;
        }
        return false;
    }

    /**
     * @return the method
     */
    public String getMethod() {
        return method;
    }

    /**
     * @param method the fieldName to set
     */
    public void setMethod(String method) {
        this.method = method;
    }

    /**
     * @return the type
     */
    public Class getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(Class type) {
        this.type = type;
    }

    /**
     * @return the temporal
     */
    public String getTemporal() {
        return temporal;
    }

    /**
     * @param temporal the temporal to set
     */
    public void setTemporal(String temporal) {
        this.temporal = temporal;
    }

    /**
     * @return the setMethod
     */
    public JdbcMethod getSetMethod() {
        return setMethod;
    }

    /**
     * @param setMethod the setMethod to set
     */
    public void setSetMethod(JdbcMethod setMethod) {
        this.setMethod = setMethod;
    }

    /**
     * @return the isId
     */
    public boolean getIsId() {
        return isId;
    }

    /**
     * @param isId the isId to set
     */
    public void setIsId(boolean isId) {
        this.isId = isId;
    }

    /**
     * @return the isObject
     */
    public boolean getIsObject() {
        return isObject;
    }

    /**
     * @param isObject the isObject to set
     */
    public void setIsObject(boolean isObject) {
        this.isObject = isObject;
    }

    /**
     * @return the isJdbcObject
     */
    public boolean isIsJdbcObject() {
        return isJdbcObject;
    }

    /**
     * @return the objClassName
     */
    public String getObjClassName() {
        return objClassName;
    }
}
