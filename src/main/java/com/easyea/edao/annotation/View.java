/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.easyea.edao.annotation;

import java.lang.annotation.Target;
import java.lang.annotation.Retention;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

/**
 * 把一个类注释成为一个类似数据库View类似功能，直接读取属性不能更新属性的值。
 * @author louis
 */
@Target({TYPE}) @Retention(RUNTIME)
public @interface View {
    String name() default "";
}
