package cn.newphy.mate.sql;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Mybatis Or操作表达式
 *
 * @author Newphy
 * @date 2018/7/30
 **/
public class Or implements QueryExpression {

	private List<QueryExpression> expressions = new ArrayList<QueryExpression>();

	public Or(QueryExpression exp1, QueryExpression exp2, QueryExpression...expN) {
		if(exp1 == null || exp2 == null) {
			throw new IllegalArgumentException("参与or运算的表达式不能为空");
		}
		expressions.add(exp1);
		expressions.add(exp2);
		if (expN != null) {
			for (int i = 0; i < expN.length; i++) {
				QueryExpression exp = expN[i];
				if (exp != null) {
					expressions.add(exp);
				}
			}
		}
	}


	@Override
	public String toSql(SqlBuilder sqlBuilder) {
		return sqlBuilder.buildOrSql(expressions);
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
