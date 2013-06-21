/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.easyea.internal;

/**
 * 代码字符串的拼接器，利用StringBuilder的方法来进行拼接，把函数名进行适当的缩短在拼写代码时
 * 减少拼接函数的字符数，方便查看拼写的代码。增加t(int count)用来对代码进行缩进，增加了
 * r(int count)方便对代码进行换行。a函数对应StringBuilder的append函数
 * @author louis
 */
public final class CodeBuilder implements java.io.Serializable {
    
    StringBuilder code;
    
    public CodeBuilder() {
        code = new StringBuilder();
    }
    
    public CodeBuilder a(String str) {
        code.append(str);
        return this;
    }
    
    public CodeBuilder t(int i) {
        if (i < 1) {
            i = 1;
        }
        for (int j=0;j<i;j++) {
            code.append("\t");
        }
        return this;
    }
    
    public CodeBuilder a(int i) {
        code.append(i);
        return this;
    }
    
    public CodeBuilder a(long l) {
        code.append(l);
        return this;
    }
    
    public CodeBuilder a(CharSequence seq) {
        code.append(seq);
        return this;
    }
    
    public CodeBuilder a(Object obj) {
        code.append(obj);
        return this;
    }
    
    public CodeBuilder a(StringBuilder sb) {
        code.append(sb);
        return this;
    }
    
    public CodeBuilder a(StringBuffer sb) {
        code.append(sb);
        return this;
    }
    
    public CodeBuilder insert(int offset, String str) {
        code.insert(offset, str);
        return this;
    }
    
    public CodeBuilder r(int rows) {
        if (rows < 1) {
            rows = 1;
        }
        for (int i=0;i<rows;i++) {
            code.append("\n");
        }
        return this;
    }
    
    @Override
    public String toString() {
        return code.toString();
    }
}
