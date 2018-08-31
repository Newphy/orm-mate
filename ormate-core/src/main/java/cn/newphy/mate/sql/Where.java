package cn.newphy.mate.sql;

/**
 * @author Newphy
 * @createTime 2018/8/20
 */
public class Where extends And {

    @Override public String toSql(SqlBuilder sqlBuilder) {
        return sqlBuilder.buildWhereSql(this);
    }
}
