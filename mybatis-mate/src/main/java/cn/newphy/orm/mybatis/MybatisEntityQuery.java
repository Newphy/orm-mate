package cn.newphy.orm.mybatis;

import cn.newphy.mate.EntityWhere;
import cn.newphy.mate.ExecutorContext;
import cn.newphy.mate.PropertyMapping;
import cn.newphy.mate.QueryTemplate;
import cn.newphy.mate.sql.Direction;
import cn.newphy.mate.EntityQuery;
import cn.newphy.mate.sql.GroupBy;
import cn.newphy.mate.sql.Order;
import cn.newphy.mate.Page;
import cn.newphy.mate.Pageable;
import cn.newphy.mate.sql.Select;
import cn.newphy.mate.sql.Select.Builder;
import cn.newphy.mate.sql.Sort;
import cn.newphy.mate.sql.Between;
import cn.newphy.mate.sql.In;
import cn.newphy.mate.sql.Limit;
import cn.newphy.mate.sql.OperatorExpression;
import cn.newphy.mate.sql.Or;
import cn.newphy.mate.sql.QueryExpression;
import cn.newphy.mate.sql.SimpleExpression;
import cn.newphy.mate.sql.SqlBuilder;
import cn.newphy.mate.sql.Where;
import cn.newphy.orm.mybatis.mapping.MybatisMapping;
import cn.newphy.orm.mybatis.mapping.MybatisPropertyMapping;

import cn.newphy.orm.mybatis.util.PropertyInterceptor;
import cn.newphy.orm.mybatis.util.PropertyInterceptor.PropertyMethodInterceptor;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.BeanUtils;
import org.springframework.cglib.proxy.Callback;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

/**
 * EntityQueryd的Mybatis实现
 * 
 * @author Newphy
 * @date 2018/7/30
 **/
public class MybatisEntityQuery<T> extends MybatisEntityWhere<EntityQuery<T>, T> implements EntityQuery<T> {

	private Set<String> includes = new LinkedHashSet<>();
	private Set<String> excludes = new LinkedHashSet<>();
	private Limit limit = null;
	private boolean distinct = false;
	private GroupBy groupBy = null;
	private Sort sort = new Sort();


	MybatisEntityQuery(MybatisConfiguration configuration, MybatisEntityDao<T> entityDao) {
		super(configuration, entityDao);
	}

	@Override public EntityQuery<T> include(String... propertyName) {
		addToSet(includes, propertyName);
		return this;
	}

	@Override public EntityQuery<T> exclude(String... propertyName) {
		addToSet(excludes, propertyName);
		return this;
	}

	private void addToSet(Set<String> columns, String[] propertyName) {
		if (propertyName != null && propertyName.length > 0) {
			for (int i = 0; i < propertyName.length; i++) {
				MybatisPropertyMapping propertyMapping = getPropertyMapping(propertyName[i]);
				if (!columns.contains(propertyMapping.getColumn())) {
					columns.add(propertyMapping.getColumn());
				}
			}
		}
	}

	@Override public EntityQuery<T> queryTemplate(QueryTemplate<T> queryTemplate) {
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
		propertyInterceptor.setReadMethodInterceptor(new PropertyMethodInterceptor() {
			@Override public void intercept(PropertyDescriptor propertyDescriptor, Method method, Object[] objects)
				throws Throwable {
				include(propertyDescriptor.getName());
			}
		});
		T proxy = propertyInterceptor.createProxy();
		queryTemplate.process(proxy);
		return this;
	}

	@Override public EntityQuery<T> orderAsc(String propertyName) {
		Assert.isTrue(StringUtils.hasText(propertyName), "属性名称不能为空");
		return orderBy(new Order(Direction.ASC, propertyName));
	}

	@Override public EntityQuery<T> orderDesc(String propertyName) {
		Assert.isTrue(StringUtils.hasText(propertyName), "属性名称不能为空");
		return orderBy(new Order(Direction.DESC, propertyName));
	}

	@Override public EntityQuery<T> limit(int limit) {
		this.limit = new Limit(limit);
		return this;
	}

	@Override public EntityQuery<T> limit(int offset, int limit) {
		this.limit = new Limit(offset, limit);
		return this;
	}

	@Override public EntityQuery<T> groupBy(String... propertyName) {
		if (propertyName != null && propertyName.length > 0) {
			if (groupBy == null) {
				groupBy = new GroupBy();
			}
			for (int i = 0; i < propertyName.length; i++) {
				String pn = propertyName[i];
				groupBy.groupBy(getPropertyMapping(pn));
			}
		}
		return this;
	}

	@Override public EntityQuery<T> distinct() {
		this.distinct = true;
		return this;
	}

	@Override public List<T> list() {
		try {
			Select select = selectBuilder().build();
			String sql = select.toSql(configuration.getDialect().getSqlBuilder());
			Map<String, Object> paramMap = select.getParamValues();
			return entityDao.selectList(sql, paramMap);
		} finally {
			ExecutorContext.clear();
		}
	}

	@Override public Page<T> page(Pageable pageable) {
		try {
			if (pageable == null) {
				throw new IllegalArgumentException("分页对象为空");
			}
			Select select = selectBuilder().build();
			String sql = select.toSql(configuration.getDialect().getSqlBuilder());
			Map<String, Object> paramMap = select.getParamValues();
			paramMap.put(ParamConst.PARAM_NAME_PAGE, pageable);
			return (Page<T>)entityDao.selectList(sql, paramMap);
		} finally {
			ExecutorContext.clear();
		}
	}

	@Override public T one() {
		try {
			limit(1);
			Select select = selectBuilder().build();
			String sql = select.toSql(configuration.getDialect().getSqlBuilder());
			Map<String, Object> paramMap = select.getParamValues();
			return entityDao.selectObject(sql, paramMap);
		} finally {
			ExecutorContext.clear();
		}
	}

	@Override public T unique() throws IncorrectResultSizeDataAccessException {
		try {
			limit(2);
			Select select = selectBuilder().build();
			String sql = select.toSql(configuration.getDialect().getSqlBuilder());
			Map<String, Object> paramMap = select.getParamValues();
			List<T> entities = entityDao.selectList(sql, paramMap);
			if (entities != null && entities.size() > 1) {
				throw new IncorrectResultSizeDataAccessException(1);
			}
			return CollectionUtils.isEmpty(entities) ? null : entities.get(0);
		} finally {
			ExecutorContext.clear();
		}
	}

	@Override public long count() {
		try {
			Select select = selectBuilder().build();
			String sql = select.toSql(configuration.getDialect().getSqlBuilder());
			String countSql = configuration.getDialect().getSqlBuilder().buildCountSql(sql);
			Map<String, Object> paramMap = select.getParamValues();
			long count = entityDao.selectCount(countSql, paramMap);
			return count;
		} finally {
			ExecutorContext.clear();
		}
	}

	private Select.Builder selectBuilder() {
		String table = mybatisMapping.getTableName();

		Set<String> columns = new LinkedHashSet<>(includes);
		if (CollectionUtils.isEmpty(columns)) {
			List<MybatisPropertyMapping> propertyMappings = mybatisMapping.getPropertyMappings();
			for (MybatisPropertyMapping propertyMapping : propertyMappings) {
				columns.add(propertyMapping.getColumn());
			}
		}

		if (!CollectionUtils.isEmpty(excludes)) {
			for (String exclude : excludes) {
				columns.remove(exclude);
			}
		}
		Select.Builder builder = new Builder(table, columns, where, groupBy, sort, limit);

		if (distinct) {
			builder.selectDistinct();
		}
		return builder;
	}

	private EntityQuery<T> orderBy(Order order) {
		if(order != null) {
			String propertyName = order.getProperty();
			MybatisPropertyMapping propertyMapping = getPropertyMapping(propertyName);
			order.setColumn(propertyMapping.getColumn());
			this.sort = sort.and(new Sort(order));
		}
		return MybatisEntityQuery.this;
	}

}
