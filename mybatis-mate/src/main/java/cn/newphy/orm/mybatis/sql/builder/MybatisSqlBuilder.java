package cn.newphy.orm.mybatis.sql.builder;

import cn.newphy.mate.sql.And;
import cn.newphy.mate.sql.Between;
import cn.newphy.mate.sql.Direction;
import cn.newphy.mate.sql.GroupBy;
import cn.newphy.mate.sql.In;
import cn.newphy.mate.sql.Limit;
import cn.newphy.mate.sql.OperatorExpression;
import cn.newphy.mate.sql.Order;
import cn.newphy.mate.sql.PropertySet;
import cn.newphy.mate.sql.QueryExpression;
import cn.newphy.mate.sql.Select;
import cn.newphy.mate.sql.SelectQuery;
import cn.newphy.mate.sql.SimpleExpression;
import cn.newphy.mate.sql.Sort;
import cn.newphy.mate.sql.SqlBuilder;
import cn.newphy.mate.sql.Update;
import cn.newphy.mate.sql.UpdateSet;
import cn.newphy.mate.sql.Where;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

/**
 * sql builder基础类
 *
 * @author Newphy
 * @date 2018/8/22
 **/
public abstract class MybatisSqlBuilder implements SqlBuilder {

	public static final String POUND_PLACEHOLDER = "#{%s}";
	/**
	 * 参数计数器
	 */
	private AtomicInteger paramCounter = new AtomicInteger();
	/**
	 * 构建limit sql
	 * @param sql
	 * @param offsetParam
	 * @param limitParam
	 * @return
	 */
	public abstract String buildLimitSql(String sql, String offsetParam, String limitParam);

	/**
	 * 构建count sql
	 * @param sql
	 * @return
	 */
	public abstract String buildCountSql(String sql);


	@Override public String buildSelectSql(Select select) {
		String sql = SELECT + select.getSelectQuery().toSql(this) + FROM + select.getTable();
		if( select.getWhere() != null) {
			sql += select.getWhere().toSql(this);
		}
		if (select.getGroupBy() != null) {
			sql += select.getGroupBy().toSql(this);
		}
		if (select.getSort() != null) {
			sql += select.getSort().toSql(this);
		}
		if (select.getLimit() != null) {
			Limit limit = select.getLimit();
			sql = buildLimitSql(sql, getHoldplaceName(limit.getOffsetParamName()), getHoldplaceName(limit.getLimitParamName()));
		}
		return sql;
	}

	@Override public String buildSelectQuery(SelectQuery selectQuery) {
		if (selectQuery == null) {
			return "";
		}
		String sql = "";
		if (selectQuery.isSelectCount()) {
			sql = SELECT_COUNT;
		} else {
			if (selectQuery.isDistinct()) {
				sql += SELECT_DISTINC;
			}
			if (CollectionUtils.isEmpty(selectQuery.getColumns())) {
				sql += SELECT_ALL;
			} else {
				sql += StringUtils.collectionToDelimitedString(selectQuery.getColumns(), COMMA);
			}
		}
		return sql;
	}

	@Override public String buildAndSql(And and) {
		if (and == null || CollectionUtils.isEmpty(and.getExpressions())) {
			return "";
		}
		List<QueryExpression> expressions = and.getExpressions();
		String sql = expressions.get(0).toSql(this);
		for (int i = 1; i < expressions.size(); i++) {
			QueryExpression expression = expressions.get(i);
			sql += AND + expression.toSql(this);
		}
		return sql;
	}

	@Override public String buildOrSql(List<QueryExpression> expressions) {
		if (CollectionUtils.isEmpty(expressions)) {
			return "";
		}
		if (expressions.size() == 1) {
			throw new IllegalStateException("参与or表达式数量必须大于1个");
		}
		String sql = expressions.get(0).toSql(this);
		for (int i = 1; i < expressions.size(); i++) {
			QueryExpression expression = expressions.get(i);
			sql += OR + expression.toSql(this);
		}
		return sql;
	}

	@Override public String buildSimpleSql(QueryExpression expression) {
		SimpleExpression simpleExpression = (SimpleExpression)expression;
		return simpleExpression.getColumn() + simpleExpression.getExpression();
	}

	@Override public String buildOperatorSql(QueryExpression expression) {
		OperatorExpression operatorExpression = (OperatorExpression)expression;
		return operatorExpression.getColumn()
			+ operatorExpression.getOp()
			+ getHoldplaceName(operatorExpression.getParamName());
	}

	@Override public String buildInSql(QueryExpression expression) {
		In inExpression = (In)expression;
		String[] paramNames = inExpression.getParamNames();
		List<String> holdplaces = new ArrayList<>();
		for (int i = 0; i < paramNames.length; i++) {
			holdplaces.add(getHoldplaceName(paramNames[i]));
		}
		String sql = inExpression.getColumn() + IN_START;
		sql += StringUtils.collectionToDelimitedString(holdplaces, COMMA);
		sql += IN_END;
		return sql;
	}

	@Override public String buildBetweenSql(QueryExpression expression) {
		Between betweenExpression = (Between)expression;
		String sql = BRACKET_L
			+ betweenExpression.getColumn()
			+ BETWEEN
			+ getHoldplaceName(betweenExpression.getLowParamName())
			+ AND
			+ getHoldplaceName(betweenExpression.getHighParamName())
			+ BRACKET_R;
		return sql;
	}

	@Override public String buildWhereSql(Where where) {
		if (where == null || CollectionUtils.isEmpty(where.getExpressions())) {
			return "";
		}
		String sql = WHERE + buildAndSql(where);
		return sql;
	}

	@Override public String buildSortSql(Sort sort) {
		if (sort == null || CollectionUtils.isEmpty(sort.getOrders())) {
			return "";
		}
		List<Order> orders = sort.getOrders();
		String sql = ORDER_BY;
		for (Order order : orders) {
			sql += order.getColumn() + (order.getDirection() == Direction.ASC ? ASC : DESC);
		}
		return sql;
	}


	@Override public String buildLimitSql(Limit limit) {
		if (limit == null || limit.getLimit() == null) {
			return "";
		}
		String sql = "";
		if (limit.getOffset() != null) {
			sql += getHoldplaceName(limit.getOffsetParamName());
		}
		if (limit.getLimit() != null) {
			sql += sql.length() > 0 ? COMMA : "" + getHoldplaceName(limit.getLimitParamName());
		}
		return sql;
	}


	@Override public String buildGroupBy(GroupBy groupBy) {
		if(groupBy == null || CollectionUtils.isEmpty(groupBy.getGroupByColumns())){
			return "";
		}
		String sql = GROUP_BY + StringUtils.collectionToDelimitedString(groupBy.getGroupByColumns(), COMMA);
		return sql;
	}

	@Override public String buildPropertySetSql(PropertySet propertySet) {
		if (propertySet == null) {
			return "";
		}
		String sql = propertySet.getColumn() + EQ + getHoldplaceName(propertySet.getParamName());
		return sql;
	}

	@Override public String buildSetSql(UpdateSet set) {
		if (set == null || set.getPropertySets().isEmpty()) {
			return "";
		}
		List<PropertySet> propertySets = set.getPropertySets();
		String sql = SET;
		for (int i = 0; i < propertySets.size(); i++) {
			sql += propertySets.get(i).toSql(this);
			if(i != propertySets.size()-1) {
				sql += COMMA;
			}
		}
		return sql;
	}

	@Override public String buildUpdateSql(Update update) {
		if (update == null) {
			return "";
		}
		String sql = UPDATE + update.getTable();
		sql += update.getSet().toSql(this);
		if (update.getWhere() != null) {
			sql += update.getWhere().toSql(this);
		}
		return sql;
	}

	protected String getHoldplaceName(String paramName) {
		return String.format(POUND_PLACEHOLDER, paramName);
	}

}
