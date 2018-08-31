package cn.newphy.orm.mybatis;

import cn.newphy.mate.EntityDao;
import cn.newphy.mate.EntityQuery;
import cn.newphy.mate.EntityUpdate;
import cn.newphy.mate.QueryTemplate;
import cn.newphy.mate.sql.Order;
import cn.newphy.mate.Page;
import cn.newphy.mate.PageMode;
import cn.newphy.mate.PageRequest;
import cn.newphy.mate.Pageable;
import cn.newphy.orm.mybatis.mapping.EntityMapping;
import cn.newphy.orm.mybatis.mapping.MybatisMapping;
import cn.newphy.orm.mybatis.mapping.MybatisPropertyMapping;
import cn.newphy.orm.mybatis.mapping.ResultMapMapping;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

/**
 * Mybatis EntityDao实现类
 * @author Newphy
 * @createTime 2018/7/30
 */
public class MybatisEntityDao<T> implements EntityDao<T> {

    private final SqlSessionTemplate sqlSessionTemplate;
    private final MybatisConfiguration configuration;
    private final MybatisMapping mybatisMapping;

    public MybatisEntityDao(MybatisConfiguration configuration, SqlSessionFactory sqlSessionFactory, Class<?> mapperClass, Class<T> entityClass) {
        this.configuration = configuration;
        this.sqlSessionTemplate = createSqlSessionTemplate(sqlSessionFactory);
        this.mybatisMapping = new EntityMapping(configuration, entityClass);
    }

    public MybatisEntityDao(MybatisConfiguration configuration, SqlSessionFactory sqlSessionFactory, Class<?> mapperClass, Class<T> entityClass, String id) {
        this.configuration = configuration;
        this.sqlSessionTemplate = createSqlSessionTemplate(sqlSessionFactory);
        ResultMap resultMap = configuration.getConfiguration().getResultMap(id);
        if (resultMap == null) {
            throw new IllegalStateException("找不到实体类id为[" + id + "]的ResultMap");
        }
        if (!entityClass.equals(resultMap.getType())) {
            throw new IllegalStateException(String.format("ResultMap[%s]的类型与实体类型[%s]不匹配", id, entityClass.getCanonicalName()));
        }
        this.mybatisMapping = new ResultMapMapping(configuration, resultMap);
    }

    /**
     * 创建SqlSessionTemplate
     *
     * @param sqlSessionFactory
     * @return
     */
    private SqlSessionTemplate createSqlSessionTemplate(SqlSessionFactory sqlSessionFactory) {
        if (sqlSessionFactory == null) {
            throw new IllegalArgumentException("sqlSessionFactory为空");
        }
        return new SqlSessionTemplate(sqlSessionFactory);
    }



    @Override public EntityQuery<T> query() {
        return new MybatisEntityQuery<>(configuration, this);
    }

    @Override public EntityUpdate<T> update() {
        return new MybatisEntityUpdate<>(configuration, this);
    }

    @Override public int save(T entity) {
        Assert.isTrue(entity != null , "保存对象不能为空");
        mybatisMapping.getIdMapping().initId(entity);
        return sqlSessionTemplate.insert(getStatementId("save"), entity);
    }

    @Override public int batchSave(Collection<T> entities) {
        Assert.isTrue(entities != null , "批量保存对象列表不能为空");
        if (entities.size() == 0) {
            return 0;
        }
        if (configuration.getDialect().supportBatch() && hasStatementId("batchSave")) {
            for (T entity : entities) {
                mybatisMapping.getIdMapping().initId(entity);
            }
            return sqlSessionTemplate.insert(getStatementId("batchSave"), entities);
        } else {
            int count = 0;
            for (T entity : entities) {
                count += save(entity);
            }
            return count;
        }
    }

    @Override public int batchSave(T[] entities) {
        Assert.isTrue(entities != null , "批量保存对象数组不能为空");
        return batchSave(Arrays.asList(entities));
    }

    @Override public int update(T entity) {
        Assert.isTrue(entity != null , "更新对象不能为空");
        checkId(entity);
        return sqlSessionTemplate.update(getStatementId("update"), entity);
    }

    @Override public int updateOptimistic(T entity) throws OptimisticLockingFailureException {
        checkId(entity);
        MybatisPropertyMapping versionMapping = mybatisMapping.getVersionMapping();
        if (versionMapping == null) {
            throw new IllegalStateException(String.format("[%s]没有找到版本字段", mybatisMapping.getEntityClass()));
        }
        Object versionValue = versionMapping.getPropertyValue(entity);
        if (versionValue == null) {
            throw new IllegalArgumentException("获取的版本属性值为空");
        }
        int c = sqlSessionTemplate.update(getStatementId("updateOptimistic"), entity);
        if (c == 0) {
            throw new OptimisticLockingFailureException("过期版本");
        }
        return c;
    }

    @Override public int batchUpdate(Collection<T> entities) {
        Assert.isTrue(entities != null , "批量更新对象列表不能为空");
        if(entities.size() == 0) {
            return 0;
        }
        if (configuration.getDialect().supportBatch() && hasStatementId("batchUpdate")) {
            for (T entity : entities) {
                checkId(entity);
            }
            return sqlSessionTemplate.update(getStatementId("batchUpdate"), entities);
        }
        else {
            int count = 0;
            for (T entity : entities) {
                count += update(entity);
            }
            return count;
        }
    }

    @Override public int batchUpdate(T[] entities) {
        Assert.isTrue(entities != null , "批量更新对象数组不能为空");
        return batchUpdate(Arrays.asList(entities));
    }

    @Override public int delete(T entity) {
        Assert.isTrue(entity != null , "删除对象不能为空");
        checkId(entity);
        return sqlSessionTemplate.delete(getStatementId("delete"), entity);
    }


    @Override public int batchDelete(Collection<T> entities) {
        Assert.isTrue(entities != null , "批量删除对象列表不能为空");
        if(entities.size() == 0) {
            return 0;
        }
        if (configuration.getDialect().supportBatch() && hasStatementId("batchDelete")) {
            for (T entity : entities) {
                checkId(entity);
            }
            return sqlSessionTemplate.delete(getStatementId("batchDelete"), entities);
        } else {
            int count = 0;
            for (T entity : entities) {
                count += delete(entity);
            }
            return count;
        }
    }

    @Override public int batchDelete(T[] entities) {
        Assert.isTrue(entities != null , "批量删除对象数组不能为空");
        return batchDelete(Arrays.asList(entities));
    }

    @Override public int deleteById(Serializable id) {
        Assert.isTrue(id != null , "id不能为空");
        return sqlSessionTemplate.delete(getStatementId("deleteById"), id);
    }

    @Override public T get(Serializable id) {
        Assert.isTrue(id != null , "id不能为空");
        return sqlSessionTemplate.selectOne(getStatementId("get"), id);
    }

    @Override public T getOneBy(String propName, Object value) {
        Assert.isTrue(propName != null && propName.length() > 0 , "属性名称不能为空");
        return getOneBy(new String[]{propName}, new Object[]{value});
    }

    @Override public T getOneBy(String[] propNames, Object[] values) {
        Map<String, Object> param = concreteParamMap(null, propNames, values, null, null);
        return sqlSessionTemplate.selectOne(getStatementId("getOneBy"), param);
    }

    @Override public T getOneByTemplate(T template) {
        Assert.notNull(template, "模板对象不能为空");
        Map<String, Object> param = concreteParamMap(template, null, null, null, null);
        return sqlSessionTemplate.selectOne(getStatementId("getOneBy"), param);
    }

    @Override public T getOneByTemplate(QueryTemplate<T> template) {
        Assert.notNull(template, "模板接口不能为空");
        Map<String, Object> param = concreteParamMap(template, null, null);
        return sqlSessionTemplate.selectOne(getStatementId("getOneBy"), param);
    }

    @Override public T getUniqueBy(String propName, Object value)  throws IncorrectResultSizeDataAccessException {
        Assert.isTrue(propName != null && propName.length() > 0 , "属性名称不能为空");
        return getUniqueBy(new String[]{propName}, new Object[]{value});
    }

    @Override public T getUniqueBy(String[] propNames, Object[] values) throws IncorrectResultSizeDataAccessException {
        PageRequest pageRequest = new PageRequest(0, 2, PageMode.INFINITE);
        Map<String, Object> param = concreteParamMap(null, propNames, values, null, pageRequest);
        List<T> page = sqlSessionTemplate.selectList(getStatementId("listBy"), param);
        if (page != null && page.size() > 1) {
            throw new IncorrectResultSizeDataAccessException(1);
        }
        return page != null && page.size() > 0 ? page.get(0) : null;
    }

    @Override public T getUniqueByTemplate(T template) throws IncorrectResultSizeDataAccessException {
        Assert.notNull(template, "模板对象不能为空");
        PageRequest pageRequest = new PageRequest(0, 2, PageMode.INFINITE);
        Map<String, Object> param = concreteParamMap(template, null, null, null, pageRequest);
        List<T> page = sqlSessionTemplate.selectList(getStatementId("listBy"), param);
        if (page != null && page.size() > 1) {
            throw new IncorrectResultSizeDataAccessException(1);
        }
        return page != null && page.size() > 0 ? page.get(0) : null;
    }

    @Override public T getUniqueByTemplate(QueryTemplate template) throws IncorrectResultSizeDataAccessException {
        Assert.notNull(template, "模板接口不能为空");
        PageRequest pageRequest = new PageRequest(0, 2, PageMode.INFINITE);
        Map<String, Object> param = concreteParamMap(template, null, pageRequest);
        List<T> page = sqlSessionTemplate.selectList(getStatementId("listBy"), param);
        if (page != null && page.size() > 1) {
            throw new IncorrectResultSizeDataAccessException(1);
        }
        return page != null && page.size() > 0 ? page.get(0) : null;
    }

    @Override public List<T> listAll(Order... orders) {
        Map<String, Object> param = concreteParamMap(null, null, null, orders, null);
        return sqlSessionTemplate.selectList(getStatementId("listAll"), param);
    }

    @Override public List<T> listBy(String propName, Object value, Order... orders) {
        Assert.isTrue(propName != null && propName.length() > 0 , "属性名称不能为空");
        return listBy(new String[]{propName}, new Object[]{value}, orders);
    }

    @Override public List<T> listBy(String[] propNames, Object[] values, Order... orders) {
        Map<String, Object> param = concreteParamMap(null, propNames, values, orders, null);
        return sqlSessionTemplate.selectList(getStatementId("listBy"), param);
    }

    @Override public List<T> listByTemplate(T template, Order... orders) {
        Assert.notNull(template, "模板对象不能为空");
        Map<String, Object> param = concreteParamMap(template, null, null, orders, null);
        return sqlSessionTemplate.selectList(getStatementId("listBy"), param);
    }

    @Override public List<T> listByTemplate(QueryTemplate template, Order... orders) {
        Assert.notNull(template, "模板接口不能为空");
        Map<String, Object> param = concreteParamMap(template, orders, null);
        return null;
    }

    @Override public Page<T> page(Pageable pageable, Order...orders) {
        Assert.notNull(pageable, "分页参数不能为空");
        Map<String, Object> param = concreteParamMap(null, null, null, orders, pageable);
        return (Page<T>)sqlSessionTemplate.selectList(getStatementId("listBy"), param);
    }

    @Override public Page<T> pageByTemplate(Pageable pageable, T template, Order...orders) {
        Assert.notNull(template, "模板对象不能为空");
        Map<String, Object> param = concreteParamMap(template, null, null, orders, pageable);
        return (Page<T>)sqlSessionTemplate.selectList(getStatementId("listBy"), param);
    }

    @Override public Page<T> pageByTemplate(Pageable pageable, QueryTemplate<T> template, Order... orders) {
        Assert.notNull(template, "模板接口不能为空");
        Map<String, Object> param = concreteParamMap(template, orders, pageable);
        return (Page<T>)sqlSessionTemplate.selectList(getStatementId("listBy"), param);
    }

    @Override public Page<T> pageBy(Pageable pageable, String[] propNames, Object[] values, Order... orders) {
        Map<String, Object> param = concreteParamMap(null, propNames, values, null, pageable);
        return (Page<T>)sqlSessionTemplate.selectList(getStatementId("listBy"), param);
    }

    @Override public int count() {
        return (int)sqlSessionTemplate.selectOne(getStatementId("count"), new HashMap<String, Object>());
    }

    @Override public int count(String propName, Object value) {
        Assert.isTrue(propName != null && propName.length() > 0 , "属性名称不能为空");
        return count(new String[]{propName}, new Object[]{value});
    }

    @Override public int count(String[] propNames, Object[] values) {
        Map<String, Object> param = concreteParamMap(null, propNames, values, null, null);
        return (int)sqlSessionTemplate.selectOne(getStatementId("count"), param);
    }

    @Override public int countByTemplate(T template) {
        Assert.notNull(template, "模板对象不能为空");
        Map<String, Object> param = concreteParamMap(template, null, null, null, null);
        return (int)sqlSessionTemplate.selectOne(getStatementId("count"), param);
    }

    @Override public int countByTemplate(QueryTemplate<T> template) {
        Assert.notNull(template, "模板接口不能为空");
        Map<String, Object> param = concreteParamMap(template, null, null);
        return (int)sqlSessionTemplate.selectOne(getStatementId("count"), param);
    }

    @Override public void flush() {
        // do nothing
    }


    /**
     * 通过原始sql进行查询
     * @param rawSql
     * @param paramMap
     * @return
     */
    public List<T> selectList(String rawSql, Map<String, Object> paramMap) {
        String statementId = configuration.registerDynamicSelectSql(
            rawSql, mybatisMapping.getNamespace(), mybatisMapping.getResultMap());
        return sqlSessionTemplate.selectList(statementId, paramMap);
    }

    /**
     * 查询对象
     * @param rawSql
     * @return
     */
    public <K> K selectObject(String rawSql, Map<String, Object> paramMap) {
        String statementId = configuration.registerDynamicSelectSql(
            rawSql, mybatisMapping.getNamespace(), mybatisMapping.getResultMap());
        return sqlSessionTemplate.selectOne(statementId, paramMap);
    }


    /**
     * 查询对象
     * @param rawSql
     * @return
     */
    public Long selectCount(String rawSql, Map<String, Object> paramMap) {
        String statementId = configuration.registerDynamicCountSql(rawSql, mybatisMapping.getNamespace());
        return sqlSessionTemplate.selectOne(statementId, paramMap);
    }


    /**
     * 更新对象
     * @param rawSql
     * @param paramMap
     * @return
     */
    public int update(String rawSql, Map<String, Object> paramMap) {
        String statementId = configuration.registerDynamicUpdateSql(
            rawSql, mybatisMapping.getNamespace());
        return sqlSessionTemplate.update(statementId, paramMap);
    }


    MybatisPropertyMapping getPropertyMapping(String propertyName) {
        MybatisPropertyMapping propertyMapping = mybatisMapping.getPropertyMapping(propertyName);
        if(propertyMapping == null) {
            throw new IllegalStateException(String.format("在实体类[%s]中找不到属性[%s]", mybatisMapping.getEntityClass(), propertyName));
        }
        return propertyMapping;
    }

    MybatisMapping getMybatisMapping() {
        return mybatisMapping;
    }

    private void checkId(Object entity) {
        Object id = mybatisMapping.getIdMapping().getPropertyValue(entity);
        if (id == null) {
            throw new IllegalArgumentException("主键值为空");
        }
    }

    /**
     * 获取statementId
     * @param methodName
     * @return
     */
    private String getStatementId(String methodName) {
        return mybatisMapping.getNamespace() + "." + methodName;
    }

    private boolean hasStatementId(String methodName) {
        String statementId = getStatementId(methodName);
        return configuration.getConfiguration().hasStatement(statementId);
    }

    private Map<String, Object> concreteParamMap(T template, String[] propNames, Object[] propValues, Order[] orders, Pageable pageable) {
        Map<String, Object> param = new HashMap<>();
        Configuration configuration = sqlSessionTemplate.getConfiguration();
        if(template != null) {
            MetaObject metaObject = configuration.newMetaObject(template);
            for (String propertyName : metaObject.getGetterNames()) {
                param.put(propertyName, metaObject.getValue(propertyName));
            }
        }

        // 设置属性值
        if(propNames != null || propValues != null) {
            if(propNames == null || propValues == null || propNames.length != propValues.length) {
                throw new IllegalArgumentException("属性名称和属性值传值不正确");
            }
            for(int i = 0; i < propNames.length; i++) {
                if(propNames[i] != null) {
                    param.put(propNames[i], propValues[i]);
                }
            }
        }

        return addOrdersAndPage(param, orders, pageable);
    }


    private Map<String, Object> addOrdersAndPage(Map<String, Object> param, Order[] orders, Pageable pageable) {
        // 设置排序
        Map<String, Order> orderMap = new LinkedHashMap<>();
        if(orders != null) {
            initOrders(orders);
            for (int i = 0; i < orders.length; i++) {
                Order order = orders[i];
                if (!orderMap.containsKey(order.getProperty())) {
                    orderMap.put(order.getProperty(), order);
                }
            }
        }
        if (pageable != null && pageable.getSort() != null) {
            Order[] pageOrders = pageable.getSort().getOrders().toArray(new Order[0]);
            initOrders(orders);
            for (int i = 0; i < pageOrders.length; i++) {
                Order order = orders[i];
                if (!orderMap.containsKey(order.getProperty())) {
                    orderMap.put(order.getProperty(), order);
                }
            }
        }

        if (!CollectionUtils.isEmpty(orderMap)) {
            param.put(ParamConst.PARAM_NAME_ORDERS, orderMap.values().toArray(new Order[0]));
        }


        // 设置分页
        if(pageable != null) {
            param.put(ParamConst.PARAM_NAME_PAGE, pageable);
        }
        return param;
    }

    private void initOrders(Order[] orders) {
        if(orders != null) {
            for(int i = 0; i < orders.length; i++) {
                if(orders[i] != null) {
                    String propertyName = orders[i].getProperty();
                    if(StringUtils.hasText(propertyName)) {
                        MybatisPropertyMapping propertyMapping = getPropertyMapping(propertyName);
                        orders[i].setColumn(propertyMapping.getColumn());
                    }
                }
            }
        }
    }

    private Map<String, Object> concreteParamMap(QueryTemplate<T> template, Order[] orders, Pageable pageable) {
        final Map<String, Object> paramMap = new LinkedHashMap<>();
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(mybatisMapping.getEntityClass());
        enhancer.setCallback(new MethodInterceptor() {
            @Override public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy)
                throws Throwable {
                if (method.getDeclaringClass().equals(Object.class)) {
                    return null;
                }
                PropertyDescriptor[] propertyDescriptors = BeanUtils.getPropertyDescriptors(mybatisMapping.getEntityClass());
                for (int i = 0; i < propertyDescriptors.length; i++) {
                    PropertyDescriptor propertyDescriptor = propertyDescriptors[i];
                    if(method.equals(propertyDescriptor.getWriteMethod()) && objects.length == 1) {
                        paramMap.put(propertyDescriptor.getName(), objects[0]);
                        break;
                    }
                }
                return null;
            }
        });
        T proxy = (T)enhancer.create();
        template.process(proxy);
        return paramMap;
    }

}
