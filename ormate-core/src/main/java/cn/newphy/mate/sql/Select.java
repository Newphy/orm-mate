package cn.newphy.mate.sql;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Newphy
 * @createTime 2018/8/21
 */
public class Select implements WithParamExpression {

    private SelectQuery selectQuery = new SelectQuery();
    private String table;
    private Where where;
    private Sort sort;
    private Limit limit;
    private GroupBy groupBy;


    public static class Builder {
        private Select select = new Select();

        public Builder(String table, Set<String> selectColumns, Where where, GroupBy groupBy, Sort sort, Limit limit) {
            select.table = table;
            select.selectQuery.addColumns(selectColumns);
            select.where = where;
            select.sort = sort;
            select.limit = limit;
            select.groupBy = groupBy;
        }

        public Builder(String table) {
            this(table, null, null, null, null, null);
        }

        public Builder(String table, Set<String> selectColumns, Where where) {
            this(table, selectColumns, where, null, null, null);
        }

        public Builder select(String... column) {
            if (column != null) {
                for (int i = 0; i < column.length; i++) {
                    String s = column[i];
                    select.selectQuery.addColumn(s);
                }
            }
            return this;
        }

        public Builder selectCount() {
            select.selectQuery.setSelectCount(true);
            return this;
        }

        public Builder selectDistinct() {
            select.selectQuery.setDistinct(true);
            return this;
        }

        public Builder where(Where where) {
            if (where != null) {
                select.where = where;
            }
            return this;
        }

        public Builder sort(Sort sort) {
            if (sort != null) {
                select.sort = sort;
            }
            return this;
        }

        public Builder limit(Limit limit) {
            if (limit != null) {
                select.limit = limit;
            }
            return this;
        }

        public Select build() {
           return select;
        }
    }

    @Override public String toSql(SqlBuilder sqlBuilder) {
        return sqlBuilder.buildSelectSql(this);
    }

    @Override public Map<String, Object> getParamValues() {
        Map<String, Object> paramMap = new LinkedHashMap<>();
        if (where != null) {
            paramMap.putAll(where.getParamValues());
        }
        if (limit != null) {
            paramMap.putAll(limit.getParamValues());
        }
        return paramMap;
    }

    public SelectQuery getSelectQuery() {
        return selectQuery;
    }

    public String getTable() {
        return table;
    }

    public Where getWhere() {
        return where;
    }

    public Sort getSort() {
        return sort;
    }

    public Limit getLimit() {
        return limit;
    }

    public GroupBy getGroupBy() {
        return groupBy;
    }
}
