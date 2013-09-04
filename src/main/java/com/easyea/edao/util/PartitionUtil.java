/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.easyea.edao.util;

import com.easyea.edao.partition.NumberRange;
import com.easyea.edao.partition.PartitionParam;
import com.easyea.edao.partition.TimeRange;
import com.easyea.internal.CodeBuilder;
import java.util.Locale;

/**
 *
 * @author louis
 */
public class PartitionUtil {
    public static JavaCode getPartitionManager(Class entity, PartitionParam param) {
        if (param == null) {
            return null;
        }
        JavaCode java = new JavaCode();
        String packName  = getPartManagerPackage(entity);
        String className = getPartManagerName(entity);
        java.setPackName(packName);
        java.setClassName(className);
        
        CodeBuilder code = new CodeBuilder();
        code.a("package ").a(packName).a(";").r(2);
        if (param instanceof NumberRange) {
            NumberRange nrp = (NumberRange)param;
            code.a("import com.easyea.edao.PartitionManager;").r(1);
            code.a("import com.easyea.edao.partition.NumberInterval;").r(1);
            code.a("import com.easyea.edao.partition.NumberRange;").r(1);
            code.a("import com.easyea.edao.partition.PartitionParam;").r(1);
            code.a("import ").a(entity.getName()).a(";").r(2);
            
            code.a("public class ").a(className).a(" implements PartitionManager {").r(1);
            code.t(1).a("public <T> String getExtTableString(T entity, PartitionParam param) {").r(1);
            code.t(2).a("String s = \"\";").r(1);
            code.t(2).a("if (param == null) {").r(1);
            code.t(3).a("return s;").r(1);
            code.t(2).a("}").r(1);
            code.t(2).a(entity.getSimpleName()).a(" demo = (").a(entity.getSimpleName()).a(")entity;").r(1);
            code.t(2).a("NumberRange nrp = (NumberRange)param;").r(1);
            code.t(2).a("if (nrp.getInterval() == NumberInterval.NUMBER_MILLION) {").r(1);
            code.t(3).a("s = \"__N\" + nrp.getCount() + \"M_\" + demo.get")
                    .a(nrp.getField().substring(0, 1).toUpperCase(Locale.ENGLISH))
                    .a(nrp.getField().substring(1)).a("()").a("/(nrp.getCount()*1000000);").r(1);
            code.t(2).a("}").r(1);
            code.t(2).a("return s;").r(1);
            code.t(1).a("}").r(1);
            code.a("}");
        } else if (param instanceof TimeRange) {
            TimeRange trp = (TimeRange)param;
            code.a("import com.easyea.edao.PartitionManager;").r(1);
            code.a("import com.easyea.edao.partition.TimeInterval;").r(1);
            code.a("import com.easyea.edao.partition.TimeRange;").r(1);
            code.a("import com.easyea.edao.partition.PartitionParam;").r(1);
            code.a("import ").a(entity.getName()).a(";").r(1);
            code.a("import java.text.SimpleDateFormat;").r(2);
            
            code.a("public class ").a(className).a(" implements PartitionManager {").r(1);
            code.t(1).a("public <T> String getExtTableString(T entity, PartitionParam param) {").r(1);
            code.t(2).a("String s = \"\";").r(1);
            code.t(2).a("if (param == null) {").r(1);
            code.t(3).a("return s;").r(1);
            code.t(2).a("}").r(1);
            code.t(2).a(entity.getSimpleName()).a(" demo = (").a(entity.getSimpleName()).a(")entity;").r(1);
            code.t(2).a("TimeRange trp = (TimeRange)param;").r(1);
            code.t(2).a("SimpleDateFormat tbFormat = null;").r(1);
            code.t(2).a("if (trp.getInterval() == TimeInterval.TIME_DAY) {").r(1);
            code.t(3).a("tbFormat = new SimpleDateFormat(\"yyyyMMdd\");").r(1);
            code.t(2).a("} else if (trp.getInterval() == TimeInterval.TIME_MONTH) {").r(1);
            code.t(3).a("tbFormat = new SimpleDateFormat(\"yyyyMM\");").r(1);
            code.t(2).a("} else if (trp.getInterval() == TimeInterval.TIME_YEAR) {").r(1);
            code.t(3).a("tbFormat = new SimpleDateFormat(\"yyyy\");").r(1);
            code.t(2).a("}").r(1);
            code.t(2).a("if (tbFormat != null) {").r(1);
            code.t(3).a("s = \"__T\" + trp.getCount() + \"_\" +  ")
                    .a("tbFormat.format(demo.get")
                    .a(trp.getField().substring(0, 1).toUpperCase(Locale.ENGLISH))
                    .a(trp.getField().substring(1)).a("());").r(1);
            code.t(2).a("}").r(1);
            code.t(2).a("return s;").r(1);
            code.t(1).a("}").r(1);
            code.a("}");
        } else {
            java = null;
            return java;
        }
        java.setCode(code.toString());
        return java;
    }
    
    public static String getPartManagerPackage(Class entity) {
        String cname = entity.getName();
        int    index = cname.indexOf(".entity.");
        if (index == -1) {
            index = 0;
        }
        String pack = cname.substring(0, index) + ".partitionm";
        return pack;
    }
    
    public static String getPartManagerName(Class entity) {
        String s = entity.getSimpleName() + "Partm";
        return s;
    }
}
