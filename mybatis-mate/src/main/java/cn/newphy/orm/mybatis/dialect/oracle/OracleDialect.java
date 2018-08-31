package cn.newphy.orm.mybatis.dialect.oracle;

import cn.newphy.mate.dialect.DbType;
import cn.newphy.orm.mybatis.MybatisConfiguration;
import cn.newphy.orm.mybatis.dialect.MybatisDialect;

/**
 * @author Newphy
 * @createTime 2018/8/3
 */
public class OracleDialect extends MybatisDialect {

    public OracleDialect(MybatisConfiguration configuration) {
        super(configuration);
    }

    @Override public boolean supportBatch() {
        return false;
    }

    @Override protected String getTemplatePath() {
        return "/template/template_oracle.xsl";
    }

    @Override public DbType getDbType() {
        return DbType.ORACLE;
    }
}
