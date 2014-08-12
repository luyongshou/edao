/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.easyea.edao.util;

import com.easyea.edao.partition.NumberRange;
import com.easyea.edao.partition.PartitionParam;
import com.easyea.edao.partition.TimeRange;
import java.util.Locale;

/**
 * 定义常用的分区管理相关的API
 * @author louis
 */
public class PartitionUtil {
    /**
     * 根据实体Bean的class对象以及分区的参数获取分区管理器的源代码
     * @param entity 持久化Bean的Class对象
     * @param param 分区的参数
     * @return 返回分区管理器的源代码对象
     */
    public static JavaCode getPartitionManager(Class entity, PartitionParam param) {
        if (param == null) {
            return null;
        }
        JavaCode java = new JavaCode();
        String packName  = getPartManagerPackage(entity);
        String className = getPartManagerName(entity);
        java.setPackName(packName);
        java.setClassName(className);
        
        
        return java;
    }
    /**
     * 根据实体Bean的Class对象获取分区管理器的包名
     * @param entity 持久化Bean的Class对象
     * @return 返回分区管理器的包名
     */
    public static String getPartManagerPackage(Class entity) {
        String cname = entity.getName();
        int    index = cname.indexOf(".entity.");
        if (index == -1) {
            index = 0;
        }
        String pack = cname.substring(0, index) + ".partitionm";
        return pack;
    }
    /**
     * 根据实体Bean的Class对象获取分区管理器的类名
     * @param entity 实体Bean的Class对象
     * @return 分区管理器的类名
     */
    public static String getPartManagerName(Class entity) {
        String s = entity.getSimpleName() + "Partm";
        return s;
    }
}
