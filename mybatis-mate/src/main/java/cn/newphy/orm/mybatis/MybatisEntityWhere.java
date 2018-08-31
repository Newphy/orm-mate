package cn.newphy.orm.mybatis;

import cn.newphy.mate.EntityWhere;
import cn.newphy.mate.sql.Between;
import cn.newphy.mate.sql.In;
import cn.newphy.mate.sql.OperatorExpression;
import cn.newphy.mate.sql.Or;
import cn.newphy.mate.sql.QueryExpression;
import cn.newphy.mate.sql.SimpleExpression;
import cn.newphy.mate.sql.SqlBuilder;
import cn.newphy.mate.sql.Where;
import cn.newphy.orm.mybatis.mapping.MybatisMapping;
import cn.newphy.orm.mybatis.mapping.MybatisPropertyMapping;
import java.util.Collection;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * @author Newphy
 * @createTime 2018/8/29
 */
public abstract class MybatisEntityWhere<E extends EntityWhere, T> implements EntityWhere<E, T> {

    protected final MybatisEntityDao<T> entityDao;
    protected final MybatisConfiguration configuration;
    protected final MybatisMapping mybatisMapping;
    protected final Class<T> entityClass;

    protected Where where = new Where();

    public MybatisEntityWhere(MybatisConfiguration configuration, MybatisEntityDao<T> entityDao) {
        this.entityDao = entityDao;
        this.configuration = configuration;
        this.entityClass = entityDao.getMybatisMapping().getEntityClass();
        this.mybatisMapping = entityDao.getMybatisMapping();
    }

    @Override public E eq(String propertyName, Object value) {
        return operator(propertyName, SqlBuilder.EQ, value);
    }

    @Override public E ne(String propertyName, Object value) {
        return operator(propertyName, SqlBuilder.NE, value);
    }

    @Override public E gt(String propertyName, Object value) {
        return operator(propertyName, SqlBuilder.GT, value);
    }

    @Override public E ge(String propertyName, Object value) {
        return operator(propertyName, SqlBuilder.GE, value);
    }

    @Override public E lt(String propertyName, Object value) {
        return operator(propertyName, SqlBuilder.LT, value);
    }

    @Override public E le(String propertyName, Object value) {
        return operator(propertyName, SqlBuilder.LE, value);
    }

    @Override public E between(String propertyName, Object low, Object high) {
        Assert.isTrue(StringUtils.hasText(propertyName), "属性名称不能为空");
        return and(new Between(getPropertyMapping(propertyName), low, high));
    }

    @Override public E like(String propertyName, String value) {
        return operator(propertyName, SqlBuilder.LIKE, value);
    }

    @Override public E in(String propertyName, Object[] values) {
        return and(new In(getPropertyMapping(propertyName), values));
    }

    @Override public E in(String propertyName, Collection<?> values) {
        return in(propertyName, values.toArray());
    }

    @Override public E isNull(String propertyName) {
        return simple(propertyName, SqlBuilder.IS_NULL);
    }

    @Override public E notNull(String propertyName) {
        return simple(propertyName, SqlBuilder.IS_NOT_NULL);
    }

    @Override public E and(QueryExpression... expression) {
        where.and(expression);
        return (E)this;
    }

    @Override public E or(QueryExpression exp1, QueryExpression exp2, QueryExpression... expN) {
        return and(new Or(exp1, exp2, expN));
    }

    private E operator(String propertyName, String op, Object value) {
        Assert.isTrue(StringUtils.hasText(propertyName), "属性名称不能为空");
        return and(new OperatorExpression(entityDao.getPropertyMapping(propertyName), op, value));
    }

    private E simple(String propertyName, String simpleExpression) {
        Assert.isTrue(StringUtils.hasText(propertyName), "属性名称不能为空");
        return and(new SimpleExpression(entityDao.getPropertyMapping(propertyName), simpleExpression));
    }

    protected MybatisPropertyMapping getPropertyMapping(String properyName) {
        return entityDao.getPropertyMapping(properyName);
    }

}
