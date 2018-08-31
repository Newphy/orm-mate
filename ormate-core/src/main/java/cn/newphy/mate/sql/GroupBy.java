package cn.newphy.mate.sql;

import cn.newphy.mate.PropertyMapping;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * groupBy表达式
 * @author Newphy
 * @createTime 2018/8/20
 */
public class GroupBy implements SqlSegment {

    private List<String> groupByColumns = new ArrayList<>();

    public GroupBy(PropertyMapping... propertyMappings) {
        groupBy(propertyMappings);
    }

    public void groupBy(PropertyMapping... propertyMappings) {
        if (propertyMappings != null) {
            for (int i = 0; i < propertyMappings.length; i++) {
                PropertyMapping propertyMapping = propertyMappings[i];
                groupByColumns.add(propertyMapping.getColumn());
            }
        }
    }

    public List<String> getGroupByColumns() {
        return groupByColumns;
    }


    @Override public String toSql(SqlBuilder sqlBuilder) {
        return sqlBuilder.buildGroupBy(this);
    }


}
