package cn.newphy.mate.sql;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Newphy
 * @createTime 2018/8/29
 */
public class UpdateSet implements WithParamExpression {

    private List<PropertySet> propertySets = new ArrayList<>();

    @Override public String toSql(SqlBuilder sqlBuilder) {
        return sqlBuilder.buildSetSql(this);
    }

    public void addPropertySet(PropertySet propertySet) {
        if (propertySet != null) {
            propertySets.add(propertySet);
        }
    }

    public List<PropertySet> getPropertySets() {
        return propertySets;
    }

    @Override public Map<String, Object> getParamValues() {
        Map<String, Object> paramValues = new LinkedHashMap<>();
        for (PropertySet propertySet : propertySets) {
            Map<String, Object> paramMap = propertySet.getParamValues();
            if (paramMap != null) {
                paramValues.putAll(paramMap);
            }
        }
        return paramValues;
    }
}
