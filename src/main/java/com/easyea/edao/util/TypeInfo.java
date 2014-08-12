/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.easyea.edao.util;

/**
 *
 * @author louis
 */
public class TypeInfo {
    private String setMethod;
    private String method;
    private Class  type;
    private String temporal;
    private boolean isId;
    
    public TypeInfo(String setMethod, String method, Class type, boolean isId) {
        this.setMethod = setMethod;
        this.method   = method;
        this.type     = type;
        this.temporal = "";
        this.isId      = isId;
    }
    
    public TypeInfo(String setMethod, String method, Class type, String temporal, 
            boolean isId) {
        this.setMethod = setMethod;
        this.method = method;
        this.type      = type;
        this.temporal  = temporal;
        this.isId      = isId;
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
    public String getSetMethod() {
        return setMethod;
    }

    /**
     * @param setMethod the setMethod to set
     */
    public void setSetMethod(String setMethod) {
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
}
