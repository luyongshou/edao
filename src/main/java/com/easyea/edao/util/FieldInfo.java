/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.easyea.edao.util;

import java.lang.annotation.Annotation;

/**
 *
 * @author louis
 */
public class FieldInfo {
    private String       name;
    private Annotation[] annotations;
    private Class<?>     type;
    private String       typeStr;
    
    public FieldInfo(String name, Class<?> type) {
        this.name        = name;
        this.type        = type;
        this.annotations = null;
        this.typeStr     = null;
    }
    
    public FieldInfo(String name, String type) {
        this.name        = name;
        this.type        = null;
        this.annotations = null;
        this.typeStr     = type;
    }
    
    public FieldInfo(String name, Class<?> type, Annotation[] annotations) {
        this.name        = name;
        this.type        = type;
        this.annotations = annotations;
        this.typeStr     = null;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the annotations
     */
    public Annotation[] getAnnotations() {
        return annotations;
    }

    /**
     * @param annotations the annotations to set
     */
    public void setAnnotations(Annotation[] annotations) {
        this.annotations = annotations;
    }

    /**
     * @return the type
     */
    public Class<?> getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(Class<?> type) {
        this.type = type;
    }

    /**
     * @return the typeStr
     */
    public String getTypeStr() {
        return typeStr;
    }

    /**
     * @param typeStr the typeStr to set
     */
    public void setTypeStr(String typeStr) {
        this.typeStr = typeStr;
    }
}
