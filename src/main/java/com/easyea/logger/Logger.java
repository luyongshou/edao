/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.easyea.logger;

import org.slf4j.Marker;

/**
 *
 * @author louis
 */
public class Logger implements org.slf4j.Logger {
    
    private org.slf4j.Logger logger;
    
    public Logger() {
        this.logger = null;
    }
    
    public Logger(org.slf4j.Logger logger) {
        this.logger = logger;
    }
    
    public void setLogger(org.slf4j.Logger logger) {
        this.logger = logger;
    }
    
    public org.slf4j.Logger getLogger() {
        return this.logger;
    }
    
    @Override
    public String getName() {
        if (logger != null) {
            return logger.getName();
        }
        return null;
    }
    
    @Override
    public boolean isTraceEnabled() {
        if (logger != null) {
            return logger.isTraceEnabled();
        }
        return true;
    }
    
    @Override
    public void trace(String msg) {
        if (logger != null && logger.isTraceEnabled()) {
            logger.trace(msg);
        }
    }
    
    @Override
    public void trace(String format, Object arg) {
        if (logger != null && logger.isTraceEnabled()) {
            logger.trace(format, arg);
        }
    }
    
    @Override
    public void trace(String format, Object arg1, Object arg2) {
        if (logger != null && logger.isTraceEnabled()) {
            logger.trace(format, arg1, arg2);
        }
    }
    
    @Override
    public void trace(String format, Object... arguments) {
        if (logger != null && logger.isTraceEnabled()) {
            logger.trace(format, arguments);
        }
    }
    
    @Override
    public void trace(String msg, Throwable t) {
        if (logger != null && logger.isTraceEnabled()) {
            logger.trace(msg, t);
        }
    }
    
    @Override
    public boolean isTraceEnabled(Marker marker) {
        if (logger != null) {
            return logger.isTraceEnabled(marker);
        }
        return true;
    }
    
    @Override
    public void trace(Marker marker, String msg) {
        if (logger != null && logger.isTraceEnabled(marker)) {
            logger.trace(marker, msg);
        }
    }
    
    @Override
    public void trace(Marker marker, String format, Object arg) {
        if (logger != null && logger.isTraceEnabled(marker)) {
            logger.trace(marker, format, arg);
        }
    }
    
    @Override
    public void trace(Marker marker, String format, Object arg1, Object arg2) {
        if (logger != null && logger.isTraceEnabled(marker)) {
            logger.trace(marker, format, arg1, arg2);
        }
    }
    
    @Override
    public void trace(Marker marker, String format, Object... argArray) {
        if (logger != null && logger.isTraceEnabled(marker)) {
            logger.trace(marker, format, argArray);
        }
    }
    
    @Override
    public void trace(Marker marker, String msg, Throwable t) {
        if (logger != null && logger.isTraceEnabled(marker)) {
            logger.trace(marker, msg, t);
        }
    }
    
    @Override
    public boolean isDebugEnabled() {
        if (logger != null) {
            return logger.isDebugEnabled();
        }
        return true;
    }
    
    @Override
    public void debug(String msg) {
        if (logger != null && logger.isDebugEnabled()) {
            logger.debug(msg);
        }
    }
    
    @Override
    public void debug(String format, Object arg) {
        if (logger != null && logger.isDebugEnabled()) {
            logger.debug(format, arg);
        }
    }
    
    @Override
    public void debug(String format, Object arg1, Object arg2) {
        if (logger != null && logger.isDebugEnabled()) {
            logger.debug(format, arg1, arg2);
        }
    }
    
    @Override
    public void debug(String format, Object... arguments) {
        if (logger != null && logger.isDebugEnabled()) {
            logger.debug(format, arguments);
        }
    }
    
    @Override
    public void debug(String msg, Throwable t) {
        if (logger != null && logger.isDebugEnabled()) {
            logger.debug(msg, t);
        }
    }
    
    @Override
    public boolean isDebugEnabled(Marker marker) {
        if (logger != null) {
            return logger.isDebugEnabled(marker);
        }
        return true;
    }
    
    @Override
    public void debug(Marker marker, String msg) {
        if (logger != null && logger.isDebugEnabled(marker)) {
            logger.debug(marker, msg);
        }
    }
    
    @Override
    public void debug(Marker marker, String format, Object arg) {
        if (logger != null && logger.isDebugEnabled(marker)) {
            logger.debug(marker, format, arg);
        }
    }
    
    @Override
    public void debug(Marker marker, String format, Object arg1, Object arg2) {
        if (logger != null && logger.isDebugEnabled(marker)) {
            logger.debug(marker, format, arg2, arg1);
        }
    }
    
    @Override
    public void debug(Marker marker, String format, Object... arguments) {
        if (logger != null && logger.isDebugEnabled(marker)) {
            logger.debug(marker, format, arguments);
        }
    }
    
    @Override
    public void debug(Marker marker, String msg, Throwable t) {
        if (logger != null && logger.isDebugEnabled(marker)) {
            logger.debug(marker, msg, t);
        }
    }
    
    @Override
    public boolean isInfoEnabled() {
        if (logger != null) {
            return logger.isInfoEnabled();
        }
        return true;
    }
    
    @Override
    public void info(String msg) {
        if (logger != null && logger.isInfoEnabled()) {
            logger.info(msg);
        }
    }
    
    @Override
    public void info(String format, Object arg) {
        if (logger != null && logger.isInfoEnabled()) {
            logger.info(format, arg);
        }
    }
    
    @Override
    public void info(String format, Object arg1, Object arg2) {
        if (logger != null && logger.isInfoEnabled()) {
            logger.info(format, arg1, arg2);
        }
    }
    
    @Override
    public void info(String format, Object... arguments) {
        if (logger != null && logger.isInfoEnabled()) {
            logger.info(format, arguments);
        }
    }
    
    @Override
    public void info(String msg, Throwable t) {
        if (logger != null && logger.isInfoEnabled()) {
            logger.info(msg, t);
        }
    }
    
    @Override
    public boolean isInfoEnabled(Marker marker) {
        if (logger != null) {
            return logger.isInfoEnabled(marker);
        }
        return true;
    }
    
    @Override
    public void info(Marker marker, String msg) {
        if (logger != null && logger.isInfoEnabled(marker)) {
            logger.info(marker, msg);
        }
    }
    
    @Override
    public void info(Marker marker, String format, Object arg) {
        if (logger != null && logger.isInfoEnabled(marker)) {
            logger.info(marker, format, arg);
        }
    }
    
    @Override
    public void info(Marker marker, String format, Object arg1, Object arg2) {
        if (logger != null && logger.isInfoEnabled(marker)) {
            logger.info(marker, format, arg1, arg2);
        }
    }
    
    @Override
    public void info(Marker marker, String format, Object... arguments) {
        if (logger != null && logger.isInfoEnabled(marker)) {
            logger.info(marker, format, arguments);
        }
    }
    
    @Override
    public void info(Marker marker, String msg, Throwable t) {
        if (logger != null && logger.isInfoEnabled(marker)) {
            logger.info(marker, msg, t);
        }
    }
    
    @Override
    public boolean isWarnEnabled() {
        if (logger != null) {
            return logger.isWarnEnabled();
        }
        return true;
    }
    
    @Override
    public void warn(String msg) {
        if (logger != null && logger.isWarnEnabled()) {
            logger.warn(msg);
        }
    }
    
    @Override
    public void warn(String format, Object arg) {
        if (logger != null && logger.isWarnEnabled()) {
            logger.warn(format, arg);
        }
    }
    
    @Override
    public void warn(String format, Object... arguments) {
        if (logger != null && logger.isWarnEnabled()) {
            logger.warn(format, arguments);
        }
    }
    
    @Override
    public void warn(String format, Object arg1, Object arg2) {
        if (logger != null && logger.isWarnEnabled()) {
            logger.warn(format, arg1, arg2);
        }
    }
    
    @Override
    public void warn(String msg, Throwable t) {
        if (logger != null && logger.isWarnEnabled()) {
            logger.warn(msg, t);
        }
    }
    
    @Override
    public boolean isWarnEnabled(Marker marker) {
        if (logger != null) {
            return logger.isWarnEnabled(marker);
        }
        return true;
    }
    
    @Override
    public void warn(Marker marker, String msg) {
        if (logger != null && logger.isWarnEnabled(marker)) {
            logger.warn(marker, msg);
        }
    }
    
    @Override
    public void warn(Marker marker, String format, Object arg) {
        if (logger != null && logger.isWarnEnabled(marker)) {
            logger.warn(marker, format, arg);
        }
    }
    
    @Override
    public void warn(Marker marker, String format, Object arg1, Object arg2) {
        if (logger != null && logger.isWarnEnabled(marker)) {
            logger.warn(marker, format, arg1, arg2);
        }
    }
    
    @Override
    public void warn(Marker marker, String format, Object... arguments) {
        if (logger != null && logger.isWarnEnabled(marker)) {
            logger.warn(marker, format, arguments);
        }
    }
    
    @Override
    public void warn(Marker marker, String msg, Throwable t) {
        if (logger != null && logger.isWarnEnabled(marker)) {
            logger.warn(marker, msg, t);
        }
    }
    
    @Override
    public boolean isErrorEnabled() {
        if (logger != null) {
            return logger.isErrorEnabled();
        }
        return true;
    }
    
    @Override
    public void error(String msg) {
        if (logger != null && logger.isErrorEnabled()) {
            logger.error(msg);
        }
    }
    
    @Override
    public void error(String format, Object arg) {
        if (logger != null && logger.isErrorEnabled()) {
            logger.error(format, arg);
        }
    }
    
    @Override
    public void error(String format, Object arg1, Object arg2) {
        if (logger != null && logger.isErrorEnabled()) {
            logger.error(format, arg1, arg2);
        }
    }
    
    @Override
    public void error(String format, Object... arguments) {
        if (logger != null && logger.isErrorEnabled()) {
            logger.error(format, arguments);
        }
    }
    
    @Override
    public void error(String msg, Throwable t) {
        if (logger != null && logger.isErrorEnabled()) {
            logger.error(msg, t);
        }
    }
    
    @Override
    public boolean isErrorEnabled(Marker marker) {
        if (logger != null) {
            return logger.isErrorEnabled(marker);
        }
        return true;
    }
    
    @Override
    public void error(Marker marker, String msg) {
        if (logger != null && logger.isErrorEnabled(marker)) {
            logger.error(marker, msg);
        }
    }
    
    @Override
    public void error(Marker marker, String format, Object arg) {
        if (logger != null && logger.isErrorEnabled(marker)) {
            logger.error(marker, format, arg);
        }
    }
    
    @Override
    public void error(Marker marker, String format, Object arg1, Object arg2) {
        if (logger != null && logger.isErrorEnabled(marker)) {
            logger.error(marker, format, arg1, arg2);
        }
    }
    
    @Override
    public void error(Marker marker, String format, Object... arguments) {
        if (logger != null && logger.isErrorEnabled(marker)) {
            logger.error(marker, format, arguments);
        }
    }
    
    @Override
    public void error(Marker marker, String msg, Throwable t) {
        if (logger != null && logger.isErrorEnabled(marker)) {
            logger.error(marker, msg, t);
        }
    }
}