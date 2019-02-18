/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.easyea.edao.annotation;

import java.lang.annotation.ElementType;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Target;

/**
 * 表和字段的注释
 * @author louis
 */
@Target({METHOD, FIELD, ElementType.TYPE})
@Retention(RUNTIME)
public @interface Comment {
    String value() default "";
}
