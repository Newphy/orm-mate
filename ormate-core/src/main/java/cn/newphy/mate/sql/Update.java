package cn.newphy.mate.sql;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * Update 表达式
 * @author Newphy
 * @createTime 2018/8/21
 */
public class Update implements WithParamExpression {

    private UpdateSet set = new UpdateSet();
    private String table;
    private Where where;

    public Update(String table, UpdateSet updateSet, Where where) {
        Assert.isTrue(StringUtils.hasText(table), "表名不能为空");
        Assert.isTrue(updateSet != null && !updateSet.getPropertySets().isEmpty(), "set条件集合不能为空");
        this.table = table;
        this.set = updateSet;
        this.where = where;
    }

    @Override public String toSql(SqlBuilder sqlBuilder) {
        return sqlBuilder.buildUpdateSql(this);
    }

    @Override public Map<String, Object> getParamValues() {
        Map<String, Object> paramMap = new LinkedHashMap<>();
        paramMap.putAll(set.getParamValues());
        if (where != null) {
            paramMap.putAll(where.getParamValues());
        }
        return paramMap;
    }

    public UpdateSet getSet() {
        return set;
    }

    public String getTable() {
        return table;
    }

    public Where getWhere() {
        return where;
    }
}
