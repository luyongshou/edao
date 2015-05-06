/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.easyea.edao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author louis
 */
public class SingleStatementSession implements StatementSession {
    
    private       Connection con;
    private       SqlStat    stat;
    private       boolean    enableStat;
    private final List<Statement> statements;
    private final Map<String, PreparedStatement> preparedStatements;
    
    
    public SingleStatementSession() {
        this.con        = null;
        this.stat       = null;
        this.enableStat = false;
        this.statements = new ArrayList<Statement>();
        this.preparedStatements = new HashMap<String, PreparedStatement>();
    }

    @Override
    public boolean enableBatch() {
        return false;
    }

    @Override
    public void setConnection(Connection con) {
        this.con = con;
    }
    
    @Override
    public Connection getConnection() {
        return this.con;
    }
    
    @Override
    public void setAutoCommit(boolean autoCommit) throws SQLException {
        if (con != null) {
            con.setAutoCommit(autoCommit);
        }
    }
    
    @Override
    public boolean getAutoCommit() throws SQLException {
        if (con != null) {
            return con.getAutoCommit();
        } else{
            throw new SQLException("Connection is null!");
        }
    }

    @Override
    public Statement statement() throws SQLException {
        if (this.con != null) {
            Statement stmt = con.createStatement();
            statements.add(stmt);
            return stmt;
        } else {
            throw new SQLException("Connection is null!");
        }
    }

    @Override
    public PreparedStatement prepareStatement(String sql) throws SQLException {
        PreparedStatement pstmt = preparedStatements.get(sql);
        if (pstmt != null) {
            return pstmt;
        } else {
            if (con != null) {
                pstmt = con.prepareStatement(sql);
                preparedStatements.put(sql, pstmt);
                return pstmt;
            } else {
                throw new SQLException("Connection is null!");
            }
        }
    }
    
    @Override
    public void commit() throws SQLException {
        if (con != null) {
            con.commit();
        }
    }

    @Override
    public void closeStatements() {
        if (!statements.isEmpty()) {
            for (Statement stmt : statements) {
                try {stmt.close();} catch (Exception e) {}
            }
            statements.clear();
        }
        if (!preparedStatements.isEmpty()) {
            for (Map.Entry<String, PreparedStatement> entry : preparedStatements
                    .entrySet()) {
                try {entry.getValue().close();} catch (Exception e) {}
            }
            preparedStatements.clear();
        }
    }

    @Override
    public void closeConnection() {
        if (con != null) {
            try {con.close();} catch (Exception e) {}
        }
    }

    @Override
    public PreparedStatement prepareStatement(String sql, 
            int autoGeneratedKeys) throws SQLException {
        PreparedStatement pstmt = preparedStatements.get(sql + "___" 
                + autoGeneratedKeys);
        if (pstmt != null) {
            return pstmt;
        } else {
            if (con != null) {
                pstmt = con.prepareStatement(sql, autoGeneratedKeys);
                preparedStatements.put(sql, pstmt);
                return pstmt;
            } else {
                throw new SQLException("Connection is null!");
            }
        }
    }

    public boolean enableStat() {
        return enableStat;
    }

    public void addSelect(int count) {
        if (enableStat) {
            stat.setSelect(stat.getSelect() + count);
        }
    }

    public void addInsert(int count) {
        if (enableStat) {
            stat.setInsert(stat.getInsert() + 1);
        }
    }

    public void addUpdate(int count) {
        if (enableStat) {
            stat.setUpdate(stat.getUpdate() + count);
        }
    }

    public void clearSqlStat() {
        if (stat != null) {
            stat.setInsert(0);
            stat.setSelect(0);
            stat.setUpdate(0);
        }
    }

    public void setEnableStat(boolean enable) {
        if (enable && stat == null) {
            stat = new SqlStat();
        }
        this.enableStat = enable;
    }

    public SqlStat getSqlStat() {
        return stat;
    }
    
}
