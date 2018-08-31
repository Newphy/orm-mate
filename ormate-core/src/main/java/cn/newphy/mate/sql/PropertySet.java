package cn.newphy.mate.sql;

import cn.newphy.mate.PropertyMapping;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Newphy
 * @createTime 2018/8/29
 */
public class PropertySet extends ExpressionIndex implements WithParamExpression {

    private final PropertyMapping propertyMapping;
    private final Object value;

    public PropertySet(PropertyMapping propertyMapping, Object value) {
        this.propertyMapping = propertyMapping;
        this.value = value;
    }

    /**
     * 获取属性名称
     */
    public String getProperty() {
        return propertyMapping.getProperty();
    }

    /**
     * 获取列名
     */
    public String getColumn() {
        return propertyMapping.getColumn();
    }

    /**
     * 获取参数名称
     */
    public String getParamName() {
        return propertyMapping.getProperty() + getIndex();
    }

    @Override public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        PropertySet that = (PropertySet)o;

        if (propertyMapping != null ? !propertyMapping.equals(that.propertyMapping) : that.propertyMapping != null) {
            return false;
        }
        return value != null ? value.equals(that.value) : that.value == null;
    }

    @Override public int hashCode() {
        int result = propertyMapping != null ? propertyMapping.hashCode() : 0;
        result = 31 * result + (value != null ? value.hashCode() : 0);
        return result;
    }

    @Override public String toSql(SqlBuilder sqlBuilder) {
        return sqlBuilder.buildPropertySetSql(this);
    }

    @Override public Map<String, Object> getParamValues() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put(getParamName(), value);
        return map;
    }

}
