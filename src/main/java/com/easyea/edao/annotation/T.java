/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.easyea.edao.annotation;

import com.easyea.edao.util.ClassUtil;
import static com.easyea.edao.util.ClassUtil.isUpperCase;

/**
 *
 * @author louis
 */
public class T {
    public static void main(String[] args) {
        String name = "userINFOType";
        
        System.out.println(ClassUtil.toUnderScore(name));
        
    }
}
