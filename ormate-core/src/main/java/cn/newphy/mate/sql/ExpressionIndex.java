package cn.newphy.mate.sql;

import cn.newphy.mate.ExecutorContext;

/**
 * @author Newphy
 * @createTime 2018/8/30
 */
public abstract class ExpressionIndex {

    private final int index;

    public ExpressionIndex() {
        this.index = ExecutorContext.getExecutorContext().getIndex();
    }

    public int getIndex() {
        return index;
    }
}
