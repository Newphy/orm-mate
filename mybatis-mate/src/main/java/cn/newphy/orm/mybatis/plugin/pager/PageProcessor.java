package cn.newphy.orm.mybatis.plugin.pager;

import cn.newphy.mate.sql.Order;
import cn.newphy.mate.Page;
import cn.newphy.mate.Pageable;
import cn.newphy.mate.sql.Sort;
import cn.newphy.orm.mybatis.MybatisConfiguration;
import cn.newphy.orm.mybatis.ParamConst;
import cn.newphy.orm.mybatis.sql.builder.MybatisSqlBuilder;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.builder.StaticSqlSource;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.ResultMapping;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;


public abstract class PageProcessor {

	protected static final List<ResultMapping> EMPTY_RESULTMAPPING = new ArrayList<>(0);
	protected static Map<String, MappedStatement> stmtMap = new HashMap<>();

	protected final Invocation invocation;
	protected final Pageable pageable;
	protected final MybatisConfiguration configuration;
	protected final MappedStatement stmt;

	public PageProcessor(Invocation invocation, Pageable pageable, MybatisConfiguration configuration) {
		this.invocation = invocation;
		this.pageable = pageable;
		this.configuration = configuration;
		this.stmt = (MappedStatement)invocation.getArgs()[ParamConst.IDX_MAPPED_STATEMENT];
	}

	public abstract Page<?> process() throws InvocationTargetException, IllegalAccessException;

	protected MappedStatement getMappedStatement() {
		return stmt;
	}

	protected Object getParameterObject() {
		return invocation.getArgs()[ParamConst.IDX_PARAMETER_OBJECT];
	}

	protected List<ResultMap> getCountResultMaps(MappedStatement stmt) {
		List<ResultMap> resultMaps = new ArrayList<ResultMap>();
		ResultMap resultMap = new ResultMap.Builder(stmt.getConfiguration(), stmt.getId(), long.class,
				EMPTY_RESULTMAPPING).build();
		resultMaps.add(resultMap);
		return resultMaps;

	}

	/**
	 * 处理分页参数，将pageable对象放到map参数中
	 * 
	 * @param configuration
	 * @param pageable
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected Object getParameterObjectWithPage(Configuration configuration, Pageable pageable) {
		Object parameterObject = getParameterObject();
		Map<String, Object> paramMap = new LinkedHashMap<>();
		// 增加自带参数
		if (parameterObject instanceof Map) {
			paramMap.putAll((Map<String, Object>) parameterObject);
		} else {
			MetaObject metaObject = configuration.newMetaObject(parameterObject);
			for (String name : metaObject.getGetterNames()) {
				paramMap.put(name, metaObject.getValue(name));
			}
		}
		// 增加分页对象
		paramMap.put(ParamConst.PARAM_NAME_PAGE, pageable);
		// 增加排序
		if(pageable.getSort() != null) {
			Sort sort = pageable.getSort();
			List<Order> orders = sort.getOrders();
			paramMap.put(ParamConst.PARAM_NAME_ORDERS, orders.toArray(new Order[0]));
		}
		return paramMap;
	}

	@SuppressWarnings("unchecked")
	protected long queryTotal(BoundSql boundSql, Object parameterObject, Pageable pageable)
			throws InvocationTargetException, IllegalAccessException {
		MappedStatement stmt = getMappedStatement();
		String sql = boundSql.getSql();

		MybatisSqlBuilder sqlBuilder = configuration.getDialect().getSqlBuilder();
		String countSql = sqlBuilder.buildCountSql(sql);
		StaticSqlSource countSqlSource = new StaticSqlSource(stmt.getConfiguration(), countSql,
				boundSql.getParameterMappings());
		MappedStatement.Builder countStmtBuilder = configuration.createMappedStatementBuilder(stmt,
				stmt.getId() + "_Count", countSqlSource);
		countStmtBuilder.resultMaps(getCountResultMaps(stmt));
		invocation.getArgs()[ParamConst.IDX_MAPPED_STATEMENT] = countStmtBuilder.build();
		invocation.getArgs()[ParamConst.IDX_PARAMETER_OBJECT] = parameterObject;
		Object result = invocation.proceed();
		long total = ((List<Long>) result).get(0);
		return total;
	}

	protected List<?> queryPage(BoundSql boundSql, Object parameterObject, Pageable pageable)
			throws InvocationTargetException, IllegalAccessException {
		MappedStatement stmt = getMappedStatement();
		String sql = boundSql.getSql();
		MybatisSqlBuilder sqlBuilder = configuration.getDialect().getSqlBuilder();
		String pageSql = sqlBuilder.buildLimitSql(sql, "?", "?");
		List<ParameterMapping> parameterMappings = getParameterMappingWithPage(stmt.getConfiguration(), boundSql,
				pageable);
		StaticSqlSource pageSqlSource = new StaticSqlSource(stmt.getConfiguration(), pageSql, parameterMappings);
		MappedStatement.Builder pageStmtBuilder = configuration.createMappedStatementBuilder(stmt,
				stmt.getId() + "_Page", pageSqlSource);
		MappedStatement pageStmt = pageStmtBuilder.build();
		invocation.getArgs()[ParamConst.IDX_MAPPED_STATEMENT] = pageStmt;
		invocation.getArgs()[ParamConst.IDX_PARAMETER_OBJECT] = parameterObject;
		Object result = invocation.proceed();
		List<?> content = (List<?>) result;
		return content;
	}

	protected List<ParameterMapping> getParameterMappingWithPage(Configuration configuration, BoundSql boundSql,
			Pageable pageable) {
		List<ParameterMapping> parameterMappings = new ArrayList<ParameterMapping>();
		parameterMappings.addAll(boundSql.getParameterMappings());
		// 增加分页参数的parameterMapping
		ParameterMapping offsetParameterMapping = new ParameterMapping.Builder(configuration,
				ParamConst.PLACEHOLDER_OFFSET, Integer.class).build();
		parameterMappings.add(offsetParameterMapping);

		ParameterMapping limitParameterMapping = new ParameterMapping.Builder(configuration,
				ParamConst.PLACEHOLDER_LIMIT, Integer.class).build();
		parameterMappings.add(limitParameterMapping);
		return parameterMappings;
	}

}
