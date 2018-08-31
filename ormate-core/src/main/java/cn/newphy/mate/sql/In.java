package cn.newphy.mate.sql;

import cn.newphy.mate.PropertyMapping;
import java.util.Map;

/**
 * In查询表达式
 * 
 * @author Newphy
 * @date 2018/7/30
 **/
public class In extends PropertyQueryExpression {
	private final Object[] values;

	
	public In(PropertyMapping propertyMapping, Object[] values) {
		super(propertyMapping);
		if(values == null || values.length == 0) {
			throw new IllegalArgumentException("in查询集为空");
		}
		this.values = values;
	}

	public String[] getParamNames() {
		String[] paramNames = new String[values.length];
		for (int i = 0; i < values.length; i++) {
			paramNames[i] = getProperty() + getIndex() + "_" + i;
		}
		return paramNames;
	}

	@Override
	public String toSql(SqlBuilder sqlBuilder) {
		return sqlBuilder.buildInSql(this);
	}
	

	@Override public Map<String, Object> getParamValues() {
		String[] paramNames = getParamNames();
		return getParameterMap(paramNames, values);
	}
}
