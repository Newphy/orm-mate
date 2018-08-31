package cn.newphy.mate;

/**
 * 更新模板
 *
 * <p>setter操作为赋值操作</p>
 *
 * @author Newphy
 * @date 2018/8/28
 **/
public interface UpdateTemplate<T> {

    /**
     * 处理查询模板
     * @param updateTemplate
     */
    void process(T updateTemplate);
}
