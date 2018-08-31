package cn.newphy.mate.sql;

import cn.newphy.mate.dialect.DbType;
import java.util.List;

/**
 * sql构建器
 * @creater Newphy
 * @createTime 2018/7/25
 */
public interface SqlBuilder {

    String COMMA = ", ";
    String SPACE = " ";
    String UNDERSCODE = "_";
    String BRACKET_L = "(";
    String BRACKET_R = ")";
    String BRACE_L = "{";
    String BRACE_R = "}";
    String EQ = " = ";
    String GT = " > ";
    String LT = " < ";
    String GE = " >= ";
    String LE = " <= ";
    String NE = " <> ";


    String SELECT = "SELECT ";
    String SELECT_COUNT = " COUNT(1) ";
    String SELECT_DISTINC = " DISTINCT ";
    String SELECT_ALL = " * ";
    String FROM = " FROM ";
    String WHERE = " WHERE ";
    String AND = " AND ";
    String OR = " OR ";
    String ASC = " ASC ";
    String DESC = " DESC ";
    String ORDER_BY = " ORDER BY ";
    String LIMIT = " LIMIT ";
    String GROUP_BY = " GROUP BY ";
    String UPDATE = "UPDATE ";
    String SET = " SET ";
    String IN_START = " IN (";
    String IN_END = BRACKET_R;
    String BETWEEN = " BETWEEN ";
    String LIKE = " LIKE ";
    String IS_NULL = " IS NULL";
    String IS_NOT_NULL = " IS NOT NULL";



    /**
     * 构建Select
     * @param select
     * @return
     */
    String buildSelectSql(Select select);

    /**
     * 构建SelectQuery
     * @param selectQuery
     * @return
     */
    String buildSelectQuery(SelectQuery selectQuery);

    /**
     * 构建and sql
     * @param and
     * @return
     */
    String buildAndSql(And and);

    /**
     * 构建or sql
     * @param expressions
     * @return
     */
    String buildOrSql(List<QueryExpression> expressions);

    /**
     * 构建简单类sql
     * @param expression
     * @return
     */
    String buildSimpleSql(QueryExpression expression);

    /**
     * 构建操作类sql
     * @param expression
     * @return
     */
    String buildOperatorSql(QueryExpression expression);

    /**
     * 构建between sql
     * @param expression
     * @return
     */
    String buildInSql(QueryExpression expression);

    /**
     * 构建between sql
     * @param expression
     * @return
     */
    String buildBetweenSql(QueryExpression expression);

    /**
     * 构建where sql
     * @param where
     * @return
     */
    String buildWhereSql(Where where);

    /**
     * 构建sort sql
     * @param sort
     * @return
     */
    String buildSortSql(Sort sort);

    /**
     * 构建group by
     * @param groupBy
     * @return
     */
    String buildGroupBy(GroupBy groupBy);

    /**
     * 构建limit sql
     * @param limit
     * @return
     */
    String buildLimitSql(Limit limit);

    /**
     * 构建update column set sql
     * @param propertySet
     * @return
     */
    String buildPropertySetSql(PropertySet propertySet);

    /**
     * 构建update set sql
     * @param set
     * @return
     */
    String buildSetSql(UpdateSet set);

    /**
     * 构建update sql
     * @param update
     * @return
     */
    String buildUpdateSql(Update update);
}
