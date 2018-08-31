package cn.newphy.orm.mybatis.idgenerator;

import cn.newphy.mate.IdGenerator;
import java.io.Serializable;
import java.util.UUID;

/**
 * @author Newphy
 * @createTime 2018/8/17
 */
public class UUIDIdGenerator implements IdGenerator {

    @Override public Serializable generate(Object entity) {
        return UUID.randomUUID().toString();
    }
}
