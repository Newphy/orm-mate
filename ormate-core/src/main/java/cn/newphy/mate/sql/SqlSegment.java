package cn.newphy.mate.sql;

/**
 * sql片段
 * @creater Newphy
 * @createTime 2018/7/25
 */
public interface SqlSegment {


    /**
     * 转为sql
     *
     * @param sqlBuilder sql构建器
     * @return
     */
    String toSql(SqlBuilder sqlBuilder);
}
