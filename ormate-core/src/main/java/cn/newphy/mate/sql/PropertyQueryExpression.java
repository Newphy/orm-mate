package cn.newphy.mate.sql;

import cn.newphy.mate.PropertyMapping;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 属性查询表达式
 *
 * @author Newphy
 * @createTime 2018/7/25
 */
public abstract class PropertyQueryExpression extends ExpressionIndex implements QueryExpression {

    protected PropertyMapping propertyMapping;

    public PropertyQueryExpression(PropertyMapping propertyMapping) {
        super();
        this.propertyMapping = propertyMapping;
    }

    /**
     * 获取属性名称
     * @return
     */
    public String getProperty() {
        return propertyMapping.getProperty();
    }

    /**
     * 获取列名
     * @return
     */
    public String getColumn() {
        return propertyMapping.getColumn();
    }

    /**
     * 获取参数名称
     * @return
     */
    public String getParamName() {
        return propertyMapping.getProperty() + getIndex();
    }


    protected Map<String, Object> getParameterMap(String key, Object value) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put(key, value);
        return map;
    }

    protected Map<String, Object> getParameterMap(String[] keys, Object[] values) {
        Map<String, Object> map = new LinkedHashMap<>();
        for(int i = 0; i < keys.length; i++) {
            map.put(keys[i], values[i]);
        }
        return map;
    }
}
