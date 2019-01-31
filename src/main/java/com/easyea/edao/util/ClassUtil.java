/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.easyea.edao.util;

import com.easyea.edao.annotation.Column;
import com.easyea.edao.annotation.GeneratedValue;
import com.easyea.edao.annotation.GenerationType;
import com.easyea.edao.annotation.Id;
import com.easyea.edao.annotation.SequenceGenerator;
import com.easyea.edao.annotation.Table;
import com.easyea.edao.annotation.Temporal;
import com.easyea.edao.annotation.TemporalType;
import com.easyea.edao.annotation.View;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author louis
 */
public class ClassUtil {
    
    public final static String daoPackPre  = "edaop."; //EntityDao包名前缀
    public final static String vdaoPackPre = "evdaop."; //ViewDao包名前缀
    public final static String daoFactPackPre  = "edaofp."; //ViewDao包名前缀
    public final static String vdaoFactPackPre = "evdaofp."; //ViewDao包名前缀
    public final static String MAPDAO_PACKAGE = "com.easyea.mapdao";
    
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
            String name = cls.getSimpleName();
            tbName = name.substring(0, 1).toLowerCase() + name.substring(1);
            tbName = toUnderScore(tbName);
        }
        return tbName;
    }
    
    /**
     * 根据java属性的数据类型获取jdbc设置字段值的函数名
     * @param field java对象的属性对象
     * @return jdbc绑定的函数名称
     */
    public static String typeToJdbc(Field field) {
        String set = "";
        Class otype = field.getType();
        if (otype.equals(Long.class) || otype.toString().equals("long")) {
            set = "Long";
        } else if (otype.equals(Integer.class) || otype.toString().equals("int")) {
            set = "Int";
        } else if (otype.equals(String.class)) {
            set = "String";
        } else if (otype.equals(Boolean.class) || otype.toString().equals("boolean")) {
            set = "Boolean";
        } else if (otype.equals(Double.class) || otype.toString().equals("double")) {
            set = "Double";
        } else if (otype.equals(Float.class) || otype.toString().equals("float")) {
            set = "Float";
        } else if (otype.equals(Date.class)) {
            Annotation[] ans = field.getAnnotations();
            Temporal tpo = null;
            for (Annotation an : ans) {
                if (an instanceof Temporal) {
                    tpo = (Temporal)an;
                }
            }
            if (tpo == null) {
                set = "Timestamp";
            } else {
                if (tpo.value().equals(TemporalType.TIMESTAMP)) {
                    set = "Timestamp";
                } else if (tpo.value().equals(TemporalType.DATE)) {
                    set = "Date";
                } else if (tpo.value().equals(TemporalType.TIME)) {
                    set = "Time";
                } else {
                    set = "Timestamp";
                }
            }
        } else {
            set = "Object";
        }
        System.out.println("f.name=" + field.getName() + ",set=" + set);
        return set;
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
                List<Field> fs = getFields(cls);
                Field idf = null;
                if (fs != null && !fs.isEmpty()) {
                    for (Field f : fs) {
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
     * 将驼峰命名的风格字符串转为下划线风格的命名字符串。连续多个大些字母时，如果以连续的大写字母
     * 结束则在第一个大写字母前增加下划线，如果不是以大写字母结束则在第一个大写字母前增加一个下划线
     * 然后再最后一个大写字母前增加一个下划线
     * @param camel
     * @return 
     */
    public static String toUnderScore(String camel) {
        if (camel == null || camel.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        int len = camel.length();
        for (int i=0;i<len;i++) {
            char c = camel.charAt(i);
            if (isUpperCase(c)) { //如果为大写字母则判断后面是否是大写字母
                int upCount = getUpperCaseCount(i, camel, len);
                
                if (upCount + i == len) {
                    sb.append("_").append(toLowerCase(c));
                    for (int j=0;j<upCount-1;j++) {
                        i++;
                        sb.append(toLowerCase(camel.charAt(i)));
                    }
                } else {
                    sb.append("_").append(toLowerCase(c));
                    if (upCount > 1) {
                        for (int j=0;j<upCount-2;j++) {
                            i++;
                            sb.append(toLowerCase(camel.charAt(i)));
                        }
                    }
                }
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }
    
    /**
     * 查询连续大写字母的个数
     * @param start 开始位置
     * @param camel 原字符串
     * @param len 字符传长度
     * @return 
     */
    private static int getUpperCaseCount(int start, String camel, int len) {
        if (start == len -1) {
            return 1;
        }
        for (int i=start + 1;i<len;i++) {
            char c = camel.charAt(i);
            if (!isUpperCase(c)) {
                return i - start;
            }
        }
        return len - start;
    }
    
    public static char toLowerCase(char c) {
        return (char)(c + 32);
    }
    
    /**
     * 判断一个字符是否是大写字母
     * @param c
     * @return 
     */
    public static boolean isUpperCase(char c) {
        return c >= 'A' && c <= 'Z';
    }

    /**
     * 获取一个类属性以及属性中的注释类，获取时只支持一级的集成关系,获取本Class以及父类的属性。
     *
     * @param cls
     * @return
     */
    public static List<Field> getFields(Class cls) {
        Field[] fields = cls.getDeclaredFields();
        List<Field> afs = new ArrayList<Field>();
        for (Field f : fields) {
            if (!"serialVersionUID".equals(f.getName())) {
                afs.add(f);
            }
        }
        appendSuperFields(cls, afs);
        return afs;
    }
    
    private static void appendSuperFields(Class cls, List<Field> aFields) {
        Class sup = cls.getSuperclass();
        if (sup != null) {
            if (!sup.getName().equals("java.lang.Object")) {
                aFields.addAll(ClassUtil.getFields(sup));
                appendSuperFields(sup, aFields);
            }
        }
    }
    
    public static String getColumnName(Field field) {
        String       colName = field.getName();
        Annotation[] fanns   = field.getAnnotations();
        if (fanns != null && fanns.length > 0) {
            for (Annotation fann : fanns) {
                if (fann instanceof Column) {
                    Column cann = (Column) fann;
                    if (cann.name() != null && cann.name().length() > 0) {
                        colName = cann.name();
                    }
                }
            }
        } else {
            colName = toUnderScore(colName);
        }
        return colName;
    }

    /**
     * 获取一个类的所有方法
     * @param cls
     * @return 
     */
    public static List<Method> getMethods(Class cls) {
        List<Method> ms = new ArrayList<Method>();
        Method[] methods = cls.getMethods();
        ms.addAll(Arrays.asList(methods));
        return ms;
    }
    
    public static List<Method> getMethodList(Class cls) {
        ArrayList<Method> l = new ArrayList<Method>();
        Method[] methods = cls.getMethods();
        if (methods != null) {
            l.addAll(Arrays.asList(methods));
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
                    l.addAll(Arrays.asList(methods));
                }
                getSuperMethods(sup, l);
            }
        }
    }
}
