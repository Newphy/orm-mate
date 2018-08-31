package cn.newphy.orm.mybatis.mapping;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlTransient;
import org.springframework.beans.BeanUtils;

/**
 * @author Newphy
 * @createTime 2018/8/17
 */
public class IdPropertyMapping extends MybatisPropertyMapping {

    /**
     * 主键生成策略
     */
    private IdGenerationStrategy idStrategy;

    public IdPropertyMapping(MybatisPropertyMapping propertyMapping, IdGenerationStrategy idStrategy) {
        BeanUtils.copyProperties(propertyMapping, this);
        this.idStrategy = idStrategy;
    }

    /**
     * 初始化主键
     * @param entity
     */
    public void initId(Object entity) {
        if(entity != null
            && idStrategy != null
            && IdGenerationStrategyType.AUTO == idStrategy.getStrategyType()
            && idStrategy.getIdGenerator() != null) {
            Serializable id = idStrategy.getIdGenerator().generate(entity);
            setPropertyValue(entity, id);
        }
    }

    @XmlTransient
    public IdGenerationStrategy getIdStrategy() {
        return idStrategy;
    }

    public void setIdStrategy(IdGenerationStrategy idStrategy) {
        this.idStrategy = idStrategy;
    }
}
