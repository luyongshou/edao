/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.easyea.logger;

/**
 *
 * @author louis
 */
public class LoggerFactory {

    public static Logger getLogger(String name) {
        Logger logger = new Logger();
        logger.setLogger(org.slf4j.LoggerFactory.getLogger(name));
        return logger;
    }

    public static Logger getLogger(Class clazz) {
        Logger logger = new Logger();
        logger.setLogger(org.slf4j.LoggerFactory.getLogger(clazz));
        return logger;
    }
}
