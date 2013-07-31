/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.easyea.edao.builders;

import com.easyea.edao.util.ClassUtil;


/**
 * Oracle 数据库的基本实现的Builder
 * @author louis
 */
public class OracleBuilder extends AbstractBuilder {

    @Override
    public String getLimitSql(String sql, String start, String count) {
        StringBuilder sb = new StringBuilder();
        sb.append(t(4)).append(sql).append(".insert(0, \"SELECT * FROM (SELECT A.*,rownum rn FROM (\");").append(r(1));
            sb.append(t(5)).append(sql).append(".append(\") A where rownum <=\").append(start + counts).append(\") where rn>\").append(start);").append(r(1));
        return sb.toString();
    }

    @Override
    public String getNextId(Class cls) {
        StringBuilder sb = new StringBuilder();
        String seqName = ClassUtil.getSeqName(cls);
        sb.append(t(3)).append("String sId = \"SELECT ").append(seqName)
                .append(".nextval AS nid FROM DUAL\";").append(r(1));
        sb.append(t(3)).append("pstmt = con.prepareStatement(sId);")
                .append(r(1));
        sb.append(t(3)).append("ResultSet rs = pstmt.executeQuery();").append(r(1));
        sb.append(t(3)).append("if (rs.next()) {").append(r(1));
        sb.append(t(4)).append("nid = rs.getLong(\"nid\");").append(r(1));
        sb.append(t(3)).append("}").append(r(1));
        sb.append(t(3)).append("try {pstmt.close();}catch (Exception e) {}")
                .append(r(1));
        return sb.toString();
    }
    
    public static void main(String[] args) {
        OracleBuilder ob = new OracleBuilder();
        System.out.println(ob.getLimitSql("sql", "start", "count"));
    }

    @Override
    public String getDbTypeName() {
        return "Oracle";
    }
    
}
