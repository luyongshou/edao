/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.easyea.edao.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Target;

/***
 * The @GeneratedValue annotation.
 */
@Retention(RUNTIME)
@Target({METHOD, FIELD})
public @interface GeneratedValue {
  GenerationType strategy() default GenerationType.AUTO;

  String generator() default "";
}