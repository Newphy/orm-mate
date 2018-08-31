package cn.newphy.mate.sql;

import cn.newphy.mate.PropertyMapping;
import java.util.Map;

/**
 * 简单表达式
 *
 * @author Newphy
 * @date 2018/7/30
 **/
public class SimpleExpression extends PropertyQueryExpression {

	private final String expression;

	public SimpleExpression(PropertyMapping propertyMapping, String expression) {
		super(propertyMapping);
		this.expression = expression;
	}

	@Override
	public String toSql(SqlBuilder sqlBuilder) {
		return sqlBuilder.buildSimpleSql(this);
	}

	@Override public Map<String, Object> getParamValues() {
		return null;
	}

	public String getExpression() {
		return expression;
	}
}
