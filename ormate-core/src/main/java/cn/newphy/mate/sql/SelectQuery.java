package cn.newphy.mate.sql;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Newphy
 * @createTime 2018/8/21
 */
public class SelectQuery implements SqlSegment {

    private Set<String> columns = new LinkedHashSet<>();
    private boolean selectCount = false;
    private boolean distinct = false;

    public SelectQuery() {

    }

    @Override public String toSql(SqlBuilder sqlBuilder) {
        return sqlBuilder.buildSelectQuery(this);
    }

    public void addColumns(Collection<String> columns) {
        this.columns.addAll(columns);
    }

    public void addColumn(String column) {
        this.columns.add(column);
    }

    public void setSelectCount(boolean selectCount) {
        this.selectCount = selectCount;
    }

    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    public Set<String> getColumns() {
        return Collections.unmodifiableSet(columns);
    }

    public boolean isSelectCount() {
        return selectCount;
    }

    public boolean isDistinct() {
        return distinct;
    }
}
