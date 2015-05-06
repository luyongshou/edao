/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.easyea.edao.codetpls;

import com.easyea.edao.CodeTemplate;
import java.io.StringWriter;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;
import webit.script.Engine;
import webit.script.Template;

/**
 * 用Webit-script模板实现的代码模板
 * @author louis
 */
public class WebitTemplate implements CodeTemplate {
    
    private static Engine engine = null;
    private final static ReentrantLock lock = new ReentrantLock();
    
    @Override
    public String render(String tplPath, Map<String, Object> context) 
            throws Exception {
        if (engine == null) {
            lock.lock();
            try {
                engine = Engine.create("/default.wim");
            } finally {
                lock.unlock();
            }
        }
        Template template = engine.getTemplate(tplPath);
        StringWriter writer = new StringWriter();
        template.merge(context, writer);
        String code = writer.toString();
        return code;
    }
}
