package cn.newphy.mate;

/**
 * 查询模板
 *
 * <p>setter操作为赋值查询，getter操作为include操作</p>
 *
 * @author Newphy
 * @date 2018/8/28
 **/
public interface QueryTemplate<T> {

    /**
     * 处理查询模板
     * @param template
     */
    void process(T template);
}
