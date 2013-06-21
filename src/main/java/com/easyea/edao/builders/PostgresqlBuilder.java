/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.easyea.edao.builders;

/**
 * 定义Postgresql的代码拼接的实现类，主要是实现一些特殊SQL的函数，多数的代码实现由
 * AbstractBuilder来实现
 * @author louis
 */
public class PostgresqlBuilder extends AbstractBuilder {

    @Override
    public String getLimitSql(String sql, String start, String count) {
        return sql + ".append(\" limit  \").append(" + count 
                + ").append(\" offset \").append(" + start + ");";
    }
    
    @Override
    public String getNextId(Class cls) {
        StringBuilder sb = new StringBuilder();
        String tbName = this.getTableName(cls);
        sb.append(t(3)).append("String sId = \"SELECT NEXTVAL('").append(tbName)
                .append("_id_seq') AS nid\";").append(r(1));
        sb.append(t(3)).append("pstmt = con.prepareStatement(sId);")
                .append(r(1));
        sb.append(t(3)).append("ResultSet rs = pstmt.executeQuery();").append(r(1));
        sb.append(t(3)).append("if (rs.next()) {").append(r(1));
        sb.append(t(4)).append("nid = rs.getLong(\"nid\");").append(r(1));
        sb.append(t(3)).append("}").append(r(1));
        sb.append(t(3)).append("try {pstmt.close();}catch (Exception e) {}").append(r(1));
        return sb.toString();
    }
    
    public static void main(String[] args) {
        PostgresqlBuilder pb = new PostgresqlBuilder();
        System.out.println(pb.getLimitSql("sql", "start", "count"));
    }

    @Override
    public String getDbTypeName() {
        return "Postgresql";
    }
    
}
