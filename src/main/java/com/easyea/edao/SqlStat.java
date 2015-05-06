/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.easyea.edao;

/**
 *
 * @author louis
 */
public class SqlStat {
    private int select;
    private int insert;
    private int update;

    /**
     * @return the select
     */
    public int getSelect() {
        return select;
    }

    /**
     * @param select the select to set
     */
    public void setSelect(int select) {
        this.select = select;
    }

    /**
     * @return the insert
     */
    public int getInsert() {
        return insert;
    }

    /**
     * @param insert the insert to set
     */
    public void setInsert(int insert) {
        this.insert = insert;
    }

    /**
     * @return the update
     */
    public int getUpdate() {
        return update;
    }

    /**
     * @param update the update to set
     */
    public void setUpdate(int update) {
        this.update = update;
    }
    
    
}
