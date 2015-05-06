/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.easyea.edao.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Target;

/**
 * 字段以JSON数据的格式进行保存，数据库支持JSON格式则字段用JSON类型
 * @author louis
 */
@Target({METHOD, FIELD})
@Retention(RUNTIME)
public @interface Json {
    
}
