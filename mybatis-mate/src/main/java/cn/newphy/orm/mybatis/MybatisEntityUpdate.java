package cn.newphy.orm.mybatis;

import cn.newphy.mate.EntityUpdate;
import cn.newphy.mate.ExecutorContext;
import cn.newphy.mate.QueryTemplate;
import cn.newphy.mate.UpdateTemplate;
import cn.newphy.mate.sql.PropertySet;
import cn.newphy.mate.sql.Update;
import cn.newphy.mate.sql.UpdateSet;
import cn.newphy.orm.mybatis.util.PropertyInterceptor;
import cn.newphy.orm.mybatis.util.PropertyInterceptor.PropertyMethodInterceptor;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Map;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * Mybatis 实体更新操作
 *
 * @author Newphy
 * @date 2018/8/29
 **/
public class MybatisEntityUpdate<T> extends MybatisEntityWhere<EntityUpdate<T>, T> implements EntityUpdate<T> {

	private UpdateSet updateSet = new UpdateSet();
	
	MybatisEntityUpdate(MybatisConfiguration configuration, MybatisEntityDao<T> entityDao) {
		super(configuration, entityDao);
	}

	@Override public EntityUpdate<T> queryTemplate(QueryTemplate<T> queryTemplate) {
		if (queryTemplate == null) {
			return this;
		}
		PropertyInterceptor<T> propertyInterceptor = new PropertyInterceptor<>(entityClass);
		propertyInterceptor.setWriteMethodInterceptor(new PropertyMethodInterceptor() {
			@Override public void intercept(PropertyDescriptor propertyDescriptor, Method method, Object[] objects)
				throws Throwable {
				eq(propertyDescriptor.getName(), objects[0]);
			}
		});
		T proxy = propertyInterceptor.createProxy();
		queryTemplate.process(proxy);
		return this;
	}

	@Override public EntityUpdate<T> set(String propertyName, Object value) {
		Assert.isTrue(StringUtils.hasText(propertyName), "属性名称不能为空");
		updateSet.addPropertySet(new PropertySet(getPropertyMapping(propertyName), value));
		return this;
	}

	@Override public EntityUpdate<T> set(String[] propertyNames, Object[] values) {
		if(propertyNames != null && values != null ) {
			Assert.isTrue(propertyNames.length == values.length, "属性名称和值必须配对");
			for (int i = 0; i < propertyNames.length; i++) {
				set(propertyNames[i], values[i]);
			}
		}
		return this;
	}

	@Override public EntityUpdate<T> updateTemplate(UpdateTemplate<T> updateTemplate) {
		PropertyInterceptor<T> propertyInterceptor = new PropertyInterceptor<>(entityClass);
		propertyInterceptor.setWriteMethodInterceptor(new PropertyMethodInterceptor() {
			@Override public void intercept(PropertyDescriptor propertyDescriptor, Method method, Object[] objects)
				throws Throwable {
				set(propertyDescriptor.getName(), objects[0]);
			}
		});
		T proxy = propertyInterceptor.createProxy();
		updateTemplate.process(proxy);
		return this;
	}

	@Override public int update() {
		try {
			if (updateSet == null || updateSet.getPropertySets().isEmpty()) {
				return 0;
			}
			Update update = new Update(mybatisMapping.getTableName(), updateSet, where);
			String sql = update.toSql(configuration.getDialect().getSqlBuilder());
			Map<String, Object> paramMap = update.getParamValues();
			return entityDao.update(sql, paramMap);
		} finally {
			ExecutorContext.clear();
		}
	}

	@Override public int updateOptimistic(Serializable id, int currentVersion)
		throws OptimisticLockingFailureException {
		try {
			if (updateSet == null || updateSet.getPropertySets().isEmpty()) {
				return 0;
			}
			if (id == null) {
				throw new IllegalArgumentException("没有指定主键值");
			}
			eq(mybatisMapping.getIdMapping().getProperty(), id);
			if (mybatisMapping.getVersionMapping() == null) {
				throw new IllegalStateException(String.format("实体[%s]没有版本字段", entityClass.getCanonicalName()));
			}
			String verionProperty = mybatisMapping.getVersionMapping().getProperty();
			set(verionProperty, currentVersion+1);
			eq(verionProperty, currentVersion);
			int c = update();
			if (c == 0) {
				throw new OptimisticLockingFailureException("过期版本");
			}
			return c;
		} finally {
			ExecutorContext.clear();
		}
	}

}
