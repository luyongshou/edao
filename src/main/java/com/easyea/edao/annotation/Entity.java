/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.easyea.edao.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.Documented;

/***
 * The Entity annotation marks a class as a persistent entity.
 * In many cases, each entity will correspond to a database table.
 */
@Documented
@Target(ElementType.TYPE) @Retention(RetentionPolicy.RUNTIME)
public @interface Entity {
  String name() default "";
}