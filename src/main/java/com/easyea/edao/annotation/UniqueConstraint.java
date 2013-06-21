/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.easyea.edao.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/***
 * The @UniqueConstraint annotation.
 */
@Target({}) @Retention(RetentionPolicy.RUNTIME)
public @interface UniqueConstraint {
  String []columnNames();
  String name() default "";
}
