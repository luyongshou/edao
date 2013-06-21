/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.easyea.dynamic;

import com.easyea.internal.util.ClassUtils;
import com.easyea.internal.util.UnsafeByteArrayInputStream;
import com.easyea.internal.util.UnsafeByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import javax.tools.SimpleJavaFileObject;

/**
 * 定义一个根据反射生成的Java源文件的类，用来保存生成的源代码。
 * @author louis
 */
public class DynamicJavaFile extends SimpleJavaFileObject {
    
    private UnsafeByteArrayOutputStream bytecode;

    private final CharSequence source;

    public DynamicJavaFile(final String baseName, final CharSequence source) {
        super(ClassUtils.toURI(baseName + ClassUtils.JAVA_EXTENSION), Kind.SOURCE);
        this.source = source;
    }

    DynamicJavaFile(final String name, final Kind kind) {
        super(ClassUtils.toURI(name), kind);
        source = null;
    }

    public DynamicJavaFile(URI uri, Kind kind) {
        super(uri, kind);
        source = null;
    }

    @Override
    public CharSequence getCharContent(final boolean ignoreEncodingErrors)
            throws UnsupportedOperationException {
        if (source == null) {
            throw new UnsupportedOperationException("source == null");
        }
        return source;
    }

    @Override
    public InputStream openInputStream() {
        return new UnsafeByteArrayInputStream(getByteCode());
    }

    @Override
    public OutputStream openOutputStream() {
        return bytecode = new UnsafeByteArrayOutputStream();
    }

    public byte[] getByteCode() {
        return bytecode.toByteArray();
    }
}
