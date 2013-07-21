/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.easyea.edao.ddls;

import com.easyea.edao.Ddl;
import com.easyea.edao.exception.EntityException;
import com.easyea.logger.Logger;
import com.easyea.logger.LoggerFactory;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 *
 * @author louis
 */
public abstract class AbstractDdl implements Ddl {
    Logger logger = LoggerFactory.getLogger(this.getClass());
    /**
     * 利用JDBC获取一个连接中所有的与持久化bean使用的所有表名
     * @param ds 数据库连接资源
     * @param catalog 
     * @param schema
     * @param tableName 持久化Bean的表名不带分区扩展名的基础名称
     * @param types
     * @return
     * @throws EntityException
     * @throws Exception 
     */
    public List<String> getTablesByJdbc(Connection con,
                                        String catalog,
                                        String schema,
                                        String tableName,
                                        String[] types) 
            throws EntityException, Exception {
        List<String> tbs = null;
        try {
            DatabaseMetaData dbData = con.getMetaData();
            ResultSet rs = dbData.getTables(catalog, 
                                            schema, 
                                            tableName + "%", 
                                            types);
            tableName = tableName.toLowerCase(Locale.ENGLISH);
            while (rs.next()) {
                if (tbs == null) {
                    tbs = new ArrayList<String>();
                }
                String tname = rs.getString("table_name");
                if (tname != null && tname.length() > 0) {
                    tname = tname.toLowerCase(Locale.ENGLISH);
                    if (tableName.equals(tname) 
                            || tname.startsWith(tableName + "__")) {
                        tbs.add(tname);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("{}", e);
        }
        return tbs;
    }
}
