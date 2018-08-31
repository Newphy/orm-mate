package cn.newphy.mate;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * 属性映射类
 *
 * @author Newphy
 * @createTime 2018/7/31
 */
public interface PropertyMapping {

    /**
     * 获取属性名称
     * @return
     */
    String getProperty();

    /**
     * 获取列名
     * @return
     */
    String getColumn();



}
