package cn.newphy.mate;

import cn.newphy.mate.sql.QueryExpression;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import org.springframework.dao.IncorrectResultSizeDataAccessException;

/**
 * 查询器
 * @author Newphy
 * @createTime 2018/7/24
 */
public interface EntityQuery<T> extends EntityWhere<EntityQuery<T>, T> {

    /**
     * 只查询指定属性值
     * @param propertyName
     * @return
     */
    EntityQuery<T> include(String... propertyName);

    /**
     * 排除指定属性值
     * @param propertyName
     * @return
     */
    EntityQuery<T> exclude(String... propertyName);


    /**
     * limit操作
     * @param limit
     * @return
     */
    EntityQuery<T> limit(int limit);

    /**
     * limit操作
     * @param offset
     * @param limit
     * @return
     */
    EntityQuery<T> limit(int offset, int limit);

    /**
     * groupBy操作
     * @param propertyName
     * @return
     */
    EntityQuery<T> groupBy(String... propertyName);

    /**
     * distinct操作
     * @return
     */
    EntityQuery<T> distinct();

    /**
     * 正排序
     *
     * @param propertyName
     * @return
     */
    EntityQuery<T> orderAsc(String propertyName);

    /**
     * 倒排序
     *
     * @param propertyName
     * @return
     */
    EntityQuery<T> orderDesc(String propertyName);

    /**
     * 查询列表
     *
     * @return
     */
    List<T> list();

    /**
     * 分页查询
     *
     * @param pageable
     * @return
     */
    Page<T> page(Pageable pageable);

    /**
     * 获得任意一个对象
     * <p>
     * 如果结果集为空，返回null；如果结果集大于一个，返回第一个
     * </p>
     *
     * @return
     */
    T one();

    /**
     * 获得唯一一个对象
     * <p>
     * 如果结果集为空,返回null;如果结果集大于一个,抛IncorrectResultSizeDataAccessException异常
     * </p>
     *
     * @return
     */
    T unique() throws IncorrectResultSizeDataAccessException;


    /**
     * 获得数量
     * @return
     */
    long count();


}
