/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.easyea.edao.util;

import com.easyea.edao.annotation.Table;
import com.easyea.edao.annotation.View;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author louis
 */
public class ClassUtil {
    
    public static String getTableName(Class cls) {
        Annotation[] clsAnns = cls.getAnnotations();
        String tbName = "";
        for (Annotation ann : clsAnns) {
            if (ann instanceof Table) {
                Table tnn = (Table)ann;
                tbName = tnn.name();
            } else if (ann instanceof View) {
                View view = (View)ann;
                tbName = view.name();
            }
        }
        if (tbName.length() == 0) {
            tbName = cls.getSimpleName();
        }
        return tbName;
    }

    /**
     * 获取一个类属性以及属性中的注释类，获取时只支持一级的集成关系,获取本Class以及父类的属性。
     *
     * @param cls
     * @return
     */
    public static List<FieldInfo> getFields(Class cls) {
        Field[] fields = cls.getDeclaredFields();
        List<FieldInfo> afs = new ArrayList<FieldInfo>();
        for (Field field : fields) {
            FieldInfo finfo = new FieldInfo(field.getName(), field.getType());
            Annotation[] fAnns = field.getAnnotations();
            if (fAnns != null && fAnns.length > 0) {
                finfo.setAnnotations(fAnns);
            }
            afs.add(finfo);
        }

        appendSuperFields(cls, afs);
        return afs;
    }
    
    private static void appendSuperFields(Class cls, List<FieldInfo> aFields) {
        Class sup = cls.getSuperclass();
        if (sup != null) {
            if (!sup.getName().equals("java.lang.Object")) {
                aFields.addAll(ClassUtil.getFields(sup));
                appendSuperFields(sup, aFields);
            }
        }
    }

    /**
     * 获取一个类的所有方法
     * @param cls
     * @return 
     */
    public static List<MethodInfo> getMethods(Class cls) {
        List<MethodInfo> ms = new ArrayList<MethodInfo>();
        Method[] methods = cls.getMethods();
        for (Method method : methods) {
            MethodInfo minfo = new MethodInfo(method.getName(), method.getReturnType());
            minfo.setParams(method.getParameterTypes());
            minfo.setAnnotations(method.getAnnotations());
            ms.add(minfo);
        }
        return ms;
    }
    
    public static List<Method> getMethodList(Class cls) {
        ArrayList<Method> l = new ArrayList<Method>();
        Method[] methods = cls.getMethods();
        if (methods != null) {
            for (int i=0;i<methods.length;i++) {
                l.add(methods[i]);
            }
        }
        getSuperMethods(cls, l);
        return l;
    }
    
    private static void getSuperMethods(Class cls, List<Method> l) {
        Class sup = cls.getSuperclass();
        if (sup != null) {
            if (!sup.getName().equals("java.lang.Object")) {
                Method[] methods = cls.getMethods();
                if (methods != null) {
                    for (int i=0;i<methods.length;i++) {
                        l.add(methods[i]);
                    }
                }
                getSuperMethods(sup, l);
            }
        }
    }
}
