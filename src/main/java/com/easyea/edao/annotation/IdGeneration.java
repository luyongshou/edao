/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.easyea.edao.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 主键生成器的一个注释，如果主键的类型为主键生成器，则需要增加主键生成器注释，主键生成器需要
 * 实现主键生成器的接口
 * @author louis
 */
@Target(ElementType.TYPE) @Retention(RetentionPolicy.RUNTIME)
public @interface IdGeneration {
    String value();
}
