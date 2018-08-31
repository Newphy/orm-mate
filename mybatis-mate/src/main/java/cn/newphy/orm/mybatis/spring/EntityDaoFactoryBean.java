package cn.newphy.orm.mybatis.spring;

import cn.newphy.mate.EntityDao;
import cn.newphy.mate.EntityDaoFactory;
import cn.newphy.orm.mybatis.MybatisEntityDaoFactory;
import javax.swing.text.html.parser.Entity;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ResolvableType;
import org.springframework.util.StringUtils;

/**
 * @author Newphy
 * @createTime 2018/8/24
 */
public class EntityDaoFactoryBean<T> implements FactoryBean<EntityDao<T>> {

    private MybatisEntityDaoFactory entityDaoFactory;
    /**
     * 实体类型
     */
    private Class<T> entityClass;
    /**
     * 映射的resultMapId
     */
    private String resultMapId;


    @Override public EntityDao<T> getObject() throws Exception {
        if (StringUtils.hasText(resultMapId)) {
            return entityDaoFactory.createEntityDaoByResultMap(entityClass.getClass(), entityClass, resultMapId);
        } else {
            return entityDaoFactory.createEntityDao(EntityDao.class, entityClass);
        }
    }

    @Override public Class<?> getObjectType() {
        ResolvableType resolvableType = ResolvableType.forClassWithGenerics(EntityDao.class, entityClass);
        return resolvableType.resolve();
    }

    @Override public boolean isSingleton() {
        return true;
    }

    public void setEntityClass(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    public void setResultMapId(String resultMapId) {
        this.resultMapId = resultMapId;
    }

    public void setEntityDaoFactory(MybatisEntityDaoFactory entityDaoFactory) {
        this.entityDaoFactory = entityDaoFactory;
    }
}
