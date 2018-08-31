package cn.newphy.mate.sql;

import java.util.Map;

/**
 * 原始sql表达式
 *
 * @author Newphy
 * @date 2018/7/30
 **/
public class RawSqlExpression implements QueryExpression {
	
	private final String sql;
	
	public RawSqlExpression(String sql) {
		this.sql = sql;
	}

	@Override
	public String toSql(SqlBuilder sqlBuilder) {
		return sql;
	}

	@Override public Map<String, Object> getParamValues() {
		return null;
	}
}
