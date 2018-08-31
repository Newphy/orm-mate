package cn.newphy.orm.mybatis.dialect.mysql;

import cn.newphy.mate.dialect.DbType;
import cn.newphy.orm.mybatis.MybatisConfiguration;
import cn.newphy.orm.mybatis.dialect.MybatisDialect;
import com.mysql.jdbc.JDBC4Connection;
import java.sql.Connection;
import java.sql.SQLException;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;

/**
 * @author Newphy
 * @createTime 2018/8/3
 */
public class MysqlDialect extends MybatisDialect {
    private Log log = LogFactory.getLog(MysqlDialect.class);

    private boolean supportBatch = false;

    public MysqlDialect(MybatisConfiguration configuration, Connection connection) {
        super(configuration);
        try {
            JDBC4Connection mysqlConn = (JDBC4Connection)connection.getMetaData().getConnection();
            this.supportBatch = mysqlConn.getAllowMultiQueries();
        } catch (SQLException e) {
            log.error("获取Mysql属性出错", e);
        }
    }

    @Override public boolean supportBatch() {
        return supportBatch;
    }

    @Override protected String getTemplatePath() {
        return "/cn/newphy/orm/mybatis/dialect/mysql/template_mysql.xsl";
    }

    @Override public DbType getDbType() {
        return DbType.MYSQL;
    }
}
