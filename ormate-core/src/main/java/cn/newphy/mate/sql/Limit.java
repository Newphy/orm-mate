package cn.newphy.mate.sql;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Newphy
 * @createTime 2018/8/20
 */
public class Limit implements QueryExpression {

    public static final String LIMIT_PARAM = "__limit";
    public static final String OFFSET_PARAM = "__offset";

    private Integer limit;
    private Integer offset = 0;

    public Limit(Integer limit, Integer offset) {
        this.limit = limit;
        this.offset = offset;
    }

    public Limit(Integer limit) {
        this.limit = limit;
    }

    public String getOffsetParamName() {
        return OFFSET_PARAM;
    }

    public String getLimitParamName() {
        return LIMIT_PARAM;
    }

    @Override public String toSql(SqlBuilder sqlBuilder) {
        return sqlBuilder.buildLimitSql(this);
    }

    @Override public Map<String, Object> getParamValues() {
        Map<String, Object> paramMap = new LinkedHashMap<>();
        if (offset != null && offset >= 0) {
            paramMap.put(OFFSET_PARAM, offset);
        }
        if (limit != null) {
            paramMap.put(LIMIT_PARAM, limit);
        }
        return paramMap;
    }

    public Integer getLimit() {
        return limit;
    }

    public Integer getOffset() {
        return offset;
    }
}
