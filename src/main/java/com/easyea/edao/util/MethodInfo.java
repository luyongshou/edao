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
public class MethodInfo {
    private String       name;
    private Object       type;
    private Object[]     params;
    private Annotation[] annotations;
    
    public MethodInfo(String name, Object type) {
        this.name   = name;
        this.type   = type;
        this.params = null;
        this.annotations = null;
    }
    
    public MethodInfo(String name, Object type, Object[] params) {
        this.name   = name;
        this.type   = type;
        this.params = params;
        this.annotations = null;
    }
    
    public MethodInfo(String name, Object type, Object[] params, Annotation[] annotations) {
        this.name   = name;
        this.type   = type;
        this.params = params;
        this.annotations = annotations;
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
     * @return the type
     */
    public Object getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(Object type) {
        this.type = type;
    }

    /**
     * @return the params
     */
    public Object[] getParams() {
        return params;
    }

    /**
     * @param params the params to set
     */
    public void setParams(Object[] params) {
        this.params = params;
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
}
