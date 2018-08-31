package cn.newphy.mate;

import cn.newphy.mate.sql.QueryExpression;
import java.util.Collection;

/**
 * where条件设置
 * @author Newphy
 * @createTime 2018/8/29
 */
public interface EntityWhere<E extends EntityWhere, T> {

    /**
     * 等于属性值
     *
     * @param propertyName
     * @param value
     * @return
     */
    E eq(String propertyName, Object value);

    /**
     * 不等于
     *
     * @param propertyName
     * @param value
     * @return
     */
    E ne(String propertyName, Object value);

    /**
     * 大于属性值
     *
     * @param propertyName
     * @param value
     * @return
     */
    E gt(String propertyName, Object value);

    /**
     * 大于等于属性值
     *
     * @param propertyName
     * @param value
     * @return
     */
    E ge(String propertyName, Object value);

    /**
     * 小于属性值
     *
     * @param propertyName
     * @param value
     * @return
     */
    E lt(String propertyName, Object value);

    /**
     * 小于等于属性值
     *
     * @param propertyName
     * @param value
     * @return
     */
    E le(String propertyName, Object value);

    /**
     * 在low和high属性值之间
     *
     * @param propertyName
     * @param low
     * @param high
     * @return
     */
    E between(String propertyName, Object low, Object high);

    /**
     * 模糊匹配属性值
     *
     * @param propertyName
     * @param value
     * @return
     */
    E like(String propertyName, String value);

    /**
     * in查询
     *
     * @param propertyName
     * @param values
     * @return
     */
    E in(String propertyName, Object[] values);

    /**
     * in查询
     *
     * @param propertyName
     * @param values
     * @return
     */
    E in(String propertyName, Collection<?> values);

    /**
     * is null
     *
     * @param propertyName
     * @return
     */
    E isNull(String propertyName);

    /**
     * not null
     *
     * @param propertyName
     * @return
     */
    E notNull(String propertyName);

    /**
     * 查询模板
     * @param queryTemplate
     * @return
     */
    E queryTemplate(QueryTemplate<T> queryTemplate);

    /**
     * and操作
     *
     * @param expression
     * @return
     */
    E and(QueryExpression... expression);

    /**
     * or操作
     * @param exp1
     * @param exp2
     * @param expN
     * @return
     */
    E or(QueryExpression exp1, QueryExpression exp2, QueryExpression... expN);

}
