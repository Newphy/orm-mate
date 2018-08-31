package cn.newphy.mate;

import cn.newphy.mate.sql.Order;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.dao.OptimisticLockingFailureException;

/**
 * Dao处理器
 * @creater Newphy
 * @createTime 2018/7/24
 */
public interface EntityDao<T> {

    /**
     * 构建查询器
     *
     * @return
     */
	EntityQuery<T> query();

    /**
     * 构建更新器
     *
     * @return
     */
    EntityUpdate<T> update();

    /**
     * 保存实体
     *
     * @param entity
     */
    int save(T entity);

    /**
     * 批量保存实体
     *
     * @param entities
     */
    int batchSave(Collection<T> entities);

    /**
     * 批量保存实体
     *
     * @param entities
     */
    int batchSave(T[] entities);

    /**
     * 修改实体
     *
     * @param entity
     */
    int update(T entity);

    /**
     * 乐观更新
     *
     * @param entity
     * @return
     */
    int updateOptimistic(T entity) throws OptimisticLockingFailureException;

    /**
     * 批量更新实体
     *
     * @param entities
     */
    int batchUpdate(Collection<T> entities);

    /**
     * 批量更新实体
     *
     * @param entities
     */
    int batchUpdate(T[] entities);

    /**
     * 删除实体
     *
     * @param entity
     */
    int delete(T entity);

    /**
     * 批量删除实体
     *
     * @param entities
     */
    int batchDelete(Collection<T> entities);

    /**
     * 批量删除实体
     * @param entities
     * @return
     */
    int batchDelete(T[] entities);

    /**
     * 删除实体
     *
     * @param id
     */
    int deleteById(Serializable id);

    /**
     * 获得实体
     *
     * @param id
     * @return
     */
    T get(Serializable id);

    /**
     * 根据属性获得实体对象
     *
     * @param propName
     * @param value
     * @return
     */
    T getOneBy(String propName, Object value);

    /**
     * 根据属性获得实体对象
     *
     * @param propNames
     * @param values
     * @return
     */
    T getOneBy(String[] propNames, Object[] values);

    /**
     * 获得任意一个
     *
     * @param template
     * @return
     */
    T getOneByTemplate(T template);

    /**
     * 获取任意一个
     * @param template
     * @return
     */
    T getOneByTemplate(QueryTemplate<T> template);

    /**
     * 根据属性获得唯一的一个实体对象
     *
     * @param propName
     * @param value
     * @return
     */
    T getUniqueBy(String propName, Object value) throws IncorrectResultSizeDataAccessException;

    /**
     * 根据属性获得唯一的一个实体对象
     *
     * @param propNames
     * @param values
     * @return
     * @throws IncorrectResultSizeDataAccessException
     */
    T getUniqueBy(String[] propNames, Object[] values) throws IncorrectResultSizeDataAccessException;

    /**
     * 根据模板获得唯一的一个实体对象
     *
     * @param template
     * @return
     * @throws IncorrectResultSizeDataAccessException
     */
    T getUniqueByTemplate(T template) throws IncorrectResultSizeDataAccessException;

    /**
     * 根据模板获得唯一的一个实体对象
     * @param template
     * @return
     * @throws IncorrectResultSizeDataAccessException
     */
    T getUniqueByTemplate(QueryTemplate template) throws IncorrectResultSizeDataAccessException;

    /**
     * 获得所有实体列表
     *
     * @param orders
     * @return
     */
    List<T> listAll(Order... orders);

    /**
     * 根据属性获得实体对象列表
     *
     * @param propName
     * @param value
     * @param orders
     * @return
     */
    List<T> listBy(String propName, Object value, Order... orders);

    /**
     * 根据属性获得实体对象列表
     *
     * @param propNames
     * @param values
     * @param orders
     * @return
     */
    List<T> listBy(String[] propNames, Object[] values, Order... orders);

    /**
     * 根据模板获得列表
     *
     * @param template
     * @param orders
     * @return
     */
    List<T> listByTemplate(T template, Order... orders);


    /**
     * 根据模板获得列表
     *
     * @param template
     * @param orders
     * @return
     */
    List<T> listByTemplate(QueryTemplate template, Order... orders);

    /**
     * 获得实体的分页列表
     *
     * @param pageable
     * @return
     */
    Page<T> page(Pageable pageable, Order...orders);

    /**
     * 根据模板获得分页数据
     *
     * @param pageable
     * @param template
     * @param orders
     * @return
     */
    Page<T> pageByTemplate(Pageable pageable, T template, Order...orders);

    /**
     * 根据模板获得分页数据
     *
     * @param pageable
     * @param template
     * @param orders
     * @return
     */
    Page<T> pageByTemplate(Pageable pageable, QueryTemplate<T> template, Order...orders);

    /**
     * 根据属性获得实体对象分页列表
     *
     * @param pageable
     * @param propNames
     * @param values
     * @param orders
     * @return
     */
    Page<T> pageBy(Pageable pageable, String[] propNames, Object[] values, Order... orders);

    /**
     * 获得所有实体对象数量
     *
     * @return
     */
    int count();

    /**
     * 根据属性获得记录数量
     *
     * @param propName
     * @param value
     * @return
     */
    int count(String propName, Object value);

    /**
     * 根据属性获得实体对象数量
     *
     * @param propNames
     * @param values
     * @return
     */
    int count(String[] propNames, Object[] values);

    /**
     * 根据模板获得记录数量
     *
     * @param template
     * @return
     */
    int countByTemplate(T template);

    /**
     * 根据模板获得记录数量
     *
     * @param template
     * @return
     */
    int countByTemplate(QueryTemplate<T> template);

    /**
     * flush
     */
    void flush();


}
