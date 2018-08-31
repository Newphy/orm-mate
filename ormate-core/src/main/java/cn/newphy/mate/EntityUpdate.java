package cn.newphy.mate;

import cn.newphy.mate.sql.QueryExpression;
import java.io.Serializable;
import java.util.Collection;
import org.springframework.dao.OptimisticLockingFailureException;

/**
 * 更新器
 * @author Newphy
 * @createTime 2018/7/24
 */
public interface EntityUpdate<T> extends EntityWhere<EntityUpdate<T>, T> {


    /**
     * 设置值
     * @param propertyName
     * @param value
     * @return
     */
    EntityUpdate<T> set(String propertyName, Object value);

    /**
     * 设置值
     * @param propertyNames
     * @param values
     * @return
     */
    EntityUpdate<T> set(String[] propertyNames, Object[] values);

    /**
     * 使用更新模板设置值
     * @param updateTemplate
     * @return
     */
    EntityUpdate<T> updateTemplate(UpdateTemplate<T> updateTemplate);

    /**
     * 更新
     *
     * @return
     */
    int update();

    /**
     * 乐观锁更新
     * @param id
     * @param currentVersion
     * @return
     * @throws OptimisticLockingFailureException
     */
    int updateOptimistic(Serializable id, int currentVersion) throws OptimisticLockingFailureException;


}
