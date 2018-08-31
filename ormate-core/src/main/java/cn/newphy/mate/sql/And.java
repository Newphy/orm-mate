package cn.newphy.mate.sql;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Mybatis And操作表达式
 *
 * @author Newphy
 * @date 2018/7/30
 **/
public class And implements QueryExpression {

	private List<QueryExpression> expressions = new ArrayList<>();

	public And() {
	}

	public And(QueryExpression exp1, QueryExpression exp2, QueryExpression...expN) {
		if(exp1 == null || exp2 == null) {
			throw new IllegalArgumentException("参与and运算的表达式不能为空");
		}
		expressions.add(exp1);
		expressions.add(exp2);
		and(expN);
	}

	public void and(QueryExpression... expression) {
		if (expression != null && expression.length > 0) {
			for (int i = 0; i < expression.length; i++) {
				expressions.add(expression[i]);
			};
		}
	}

	public List<QueryExpression> getExpressions() {
		return expressions;
	}

	@Override
	public String toSql(SqlBuilder sqlBuilder) {
		return sqlBuilder.buildAndSql(this);
	}

	@Override public Map<String, Object> getParamValues() {
		Map<String, Object> paramValues = new LinkedHashMap<>();
		for (QueryExpression expression : expressions) {
			Map<String, Object> paramMap = expression.getParamValues();
			if (paramMap != null) {
				paramValues.putAll(paramMap);
			}
		}
		return paramValues;
	}
}
