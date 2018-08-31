package cn.newphy.mate.sql;

import java.util.Map;

/**
 * 带参数sql表达式
 *
 * @author Newphy
 * @createTime 2018/7/25
 */
public interface WithParamExpression extends SqlSegment {

    /**
     * 获取查询值
     * @return
     */
    Map<String, Object> getParamValues();

}
