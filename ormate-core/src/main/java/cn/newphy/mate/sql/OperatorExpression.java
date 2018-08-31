package cn.newphy.mate.sql;

import cn.newphy.mate.PropertyMapping;
import java.util.Map;

/**
 * 操作类表达式
 * 
 * @author Newphy
 * @date 2018/7/30
 **/
public class OperatorExpression extends PropertyQueryExpression {
	private final Object value;
	private final String op;

	public OperatorExpression(PropertyMapping propertyMapping, String op, Object value) {
		super(propertyMapping);
		this.value = value;
		this.op = op;
	}

	@Override public String toSql(SqlBuilder sqlBuilder) {
		return sqlBuilder.buildOperatorSql(this);
	}

	@Override
	public String toString() {
		return getColumn() + " " + getOp() + " " + value;
	}

	@Override public Map<String, Object> getParamValues() {
		return getParameterMap(getParamName(), value);
	}
	
	public final String getOp() {
		return op;
	}

}
