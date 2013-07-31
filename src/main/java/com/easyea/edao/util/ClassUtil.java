/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.easyea.edao.util;

import com.easyea.edao.annotation.GeneratedValue;
import com.easyea.edao.annotation.GenerationType;
import com.easyea.edao.annotation.Id;
import com.easyea.edao.annotation.SequenceGenerator;
import com.easyea.edao.annotation.Table;
import com.easyea.edao.annotation.View;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
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
     * 获取一个实体类序列的名称
     * @param cls
     * @return 
     */
    public static String getSeqName(Class cls) {
        String       seq  = getTableName(cls) + "_id_seq";
        Annotation[] anns = cls.getAnnotations();
        HashMap<String, String> aSeq = new HashMap<String, String>();
        if (anns != null) {
            for (int i=0;i<anns.length;i++) {
                if (anns[i] instanceof SequenceGenerator) {
                    SequenceGenerator seqg = (SequenceGenerator)anns[i];
                    if (seqg.name() != null && seqg.name().length() > 0 
                            && seqg.sequenceName() != null 
                            && seqg.sequenceName().length() > 0) {
                        aSeq.put(seqg.name(), seqg.sequenceName());
                    }
                }
            }
            if (!aSeq.isEmpty()) {
                List<FieldInfo> fs = getFields(cls);
                FieldInfo idf = null;
                if (fs != null && !fs.isEmpty()) {
                    for (FieldInfo f : fs) {
                        if (f.getName().equals("id") || idf == null) {
                            idf = f;
                        }
                        Annotation[] fanns = f.getAnnotations();
                        if (fanns != null) {
                            for (int i=0;i<fanns.length;i++) {
                                if (fanns[i] instanceof Id) {
                                    idf = f;
                                }
                            }
                        }
                    }
                }
                if (idf != null) {
                    Annotation[] idanns = idf.getAnnotations();
                    if (idanns != null) {
                        for (int i=0;i<idanns.length;i++) {
                            if (idanns[i] instanceof GeneratedValue) {
                                GeneratedValue gv = (GeneratedValue)idanns[i];
                                if (gv.strategy().equals(GenerationType.SEQUENCE)) {
                                    if (aSeq.containsKey(gv.generator())) {
                                        seq = aSeq.get(gv.generator());
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return seq;
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
