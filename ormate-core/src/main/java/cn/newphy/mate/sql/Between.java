package cn.newphy.mate.sql;


import cn.newphy.mate.PropertyMapping;
import java.util.Map;

/**
 * Between表达式
 *
 * @author Newphy
 * @date 2018/7/30
 **/
public class Between extends PropertyQueryExpression {
	private final Object lo;
	private final Object hi;

	public Between(PropertyMapping propertyMapping, Object lo, Object hi) {
		super(propertyMapping);
		this.lo = lo;
		this.hi = hi;
	}

	@Override
	public String toSql(SqlBuilder sqlBuilder) {
		return sqlBuilder.buildBetweenSql(this);
	}


	@Override public Map<String, Object> getParamValues() {
		return getParameterMap(new String[]{getLowParamName(), getHighParamName() }, new Object[]{lo, hi});
	}

	public String getLowParamName() {
		return getProperty() + getIndex() + "_lo";
	}

	public String getHighParamName() {
		return getProperty() + getIndex() + "_hi";
	}


}
