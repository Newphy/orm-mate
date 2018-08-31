package cn.newphy.mate;

import java.io.Serializable;

/**
 * @author Newphy
 * @createTime 2018/8/8
 */
public class EmptyIdGenerator implements IdGenerator {
    @Override public Serializable generate(Object entity) {
        return null;
    }
}
