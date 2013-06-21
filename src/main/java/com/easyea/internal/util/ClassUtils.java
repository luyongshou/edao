/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.easyea.internal.util;

import java.net.URI;
import java.net.URISyntaxException;

/**
 *
 * @author louis
 */
public class ClassUtils {
    
    public static final String CLASS_EXTENSION = ".class";

	public static final String JAVA_EXTENSION = ".java";
    
    public static URI toURI(String name) {
		try {
			return new URI(name);
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}
}
