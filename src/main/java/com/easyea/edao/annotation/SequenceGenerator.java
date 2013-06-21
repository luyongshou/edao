/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.easyea.edao.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Target;

/***
 * The @SequenceGenerator annotation.
 */
@Target({TYPE, METHOD, FIELD}) @Retention(RUNTIME)
public @interface SequenceGenerator {
  String name();
  String sequenceName() default "";
  /***
   * @since JPA 2.0
   */
  String catalog() default "";
  /***
   * @since JPA 2.0
   */
  String schema() default "";
  int initialValue() default 1;
  int allocationSize() default 50;
}
