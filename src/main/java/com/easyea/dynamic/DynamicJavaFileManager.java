/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.easyea.dynamic;

import com.easyea.internal.util.ClassUtils;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;
import javax.tools.StandardLocation;

/**
 * 定义动态编译的文件管理器，用来在内存中保存class以及源文件
 * @author louis
 */
public class DynamicJavaFileManager extends ForwardingJavaFileManager<JavaFileManager> {

    private final DynamicClassLoader classLoader;
    private final ConcurrentMap<URI, JavaFileObject> fileObjects = 
                                   new ConcurrentHashMap<URI, JavaFileObject>();

    public DynamicJavaFileManager(JavaFileManager fileManager, 
            DynamicClassLoader classLoader) {
        super(fileManager);
        this.classLoader = classLoader;
    }

    @Override
    public FileObject getFileForInput(Location location, String packageName, 
            String relativeName) throws IOException {
        FileObject o = fileObjects.get(uri(location, packageName, relativeName));
        if (o != null) {
            return o;
        }
        return super.getFileForInput(location, packageName, relativeName);
    }

    public void putFileForInput(StandardLocation location, String packageName, 
            String relativeName, JavaFileObject file) {
        fileObjects.put(uri(location, packageName, relativeName), file);
    }

    private URI uri(Location location, String packageName, String relativeName) {
        return ClassUtils.toURI(location.getName() + '/' + packageName 
                + '/' + relativeName);
    }

    @Override
    public JavaFileObject getJavaFileForOutput(Location location, 
            String qualifiedName, Kind kind, FileObject outputFile) 
                throws IOException {
        JavaFileObject file = new DynamicJavaFile(qualifiedName, kind);
        classLoader.add(qualifiedName, file);
        return file;
    }

    @Override
    public ClassLoader getClassLoader(JavaFileManager.Location location) {
        return classLoader;
    }

    @Override
    public String inferBinaryName(Location loc, JavaFileObject file) {
        if (file instanceof DynamicJavaFile) {
            return file.getName();
        }
        return super.inferBinaryName(loc, file);
    }

    @Override
    public Iterable<JavaFileObject> list(Location location, String packageName, 
            Set<Kind> kinds, boolean recurse) throws IOException {
        ArrayList<JavaFileObject> files = new ArrayList<JavaFileObject>();
        if (location == StandardLocation.CLASS_PATH 
                && kinds.contains(JavaFileObject.Kind.CLASS)) {
            for (JavaFileObject file : fileObjects.values()) {
                if (file.getKind() == Kind.CLASS 
                        && file.getName().startsWith(packageName)) {
                    files.add(file);
                }
            }
            files.addAll(classLoader.files());
        } else if (location == StandardLocation.SOURCE_PATH 
                && kinds.contains(JavaFileObject.Kind.SOURCE)) {
            for (JavaFileObject file : fileObjects.values()) {
                if (file.getKind() == Kind.SOURCE 
                        && file.getName().startsWith(packageName)) {
                    files.add(file);
                }
            }
        }
        Iterable<JavaFileObject> result = super.list(location, packageName, 
                kinds, recurse);
        for (JavaFileObject file : result) {
            files.add(file);
        }
        return files;
    }
}
