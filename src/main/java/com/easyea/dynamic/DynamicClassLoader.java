/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.easyea.dynamic;

import com.easyea.logger.Logger;
import com.easyea.logger.LoggerFactory;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import javax.tools.JavaFileObject;

/**
 * 用户载入动态编译后载入动态编译类的一个类加载器。加载器有加载器的生成时间，这样利用加载器的时候
 * 可以根据某些类的最后更新和类加载器的生成时间进行对比，如果类加载器生成时间比类的最后更新时间，
 * 则可以生成一个新的类加载器加载新的类，而吧老的加载器置为null这样老的加载器将由java虚拟机进行
 * 资源回收，实现一些动态部署的功能。
 * @author louis
 */
public class DynamicClassLoader extends ClassLoader {
    
    Logger logger = LoggerFactory.getLogger(DynamicClassLoader.class);

    private final ConcurrentMap<String, JavaFileObject> classes 
            = new ConcurrentHashMap<String, JavaFileObject>();
    private final Date createTime;

    public DynamicClassLoader(final ClassLoader parentClassLoader) {
        super(parentClassLoader);
        this.createTime = new Date();
    }
    
    public DynamicClassLoader(final ClassLoader parentClassLoader, 
            final Date createTime) {
        super(parentClassLoader);
        this.createTime = createTime;
    }

    Collection<JavaFileObject> files() {
        return Collections.unmodifiableCollection(classes.values());
    }

    void add(final String qualifiedClassName, final JavaFileObject javaFile) {
        classes.put(qualifiedClassName, javaFile);
    }
    
    public Date getCreateTime() {
        return this.createTime;
    }

    @Override
    protected Class<?> findClass(final String qualifiedClassName)
            throws ClassNotFoundException {
        try {
            return super.findClass(qualifiedClassName);
        } catch (ClassNotFoundException e) {
            JavaFileObject file = classes.get(qualifiedClassName);
            if (file != null) {
                byte[] bytes = ((DynamicJavaFile) file).getByteCode();
                return defineClass(qualifiedClassName, bytes, 0, bytes.length);
            }
            throw e;
        }
    }
}
