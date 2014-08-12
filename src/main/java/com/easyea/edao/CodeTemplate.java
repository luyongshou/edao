/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.easyea.edao;

import java.util.Map;

/**
 *
 * @author louis
 */
public interface CodeTemplate {
    public String render(String template, Map<String, Object> context) 
            throws Exception;
}
