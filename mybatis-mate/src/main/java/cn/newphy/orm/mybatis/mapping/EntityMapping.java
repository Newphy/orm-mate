package cn.newphy.orm.mybatis.mapping;

import cn.newphy.orm.mybatis.MybatisConfiguration;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.ResultMapping;
import org.springframework.beans.BeanUtils;

/**
 * 实体类映射
 * @author Newphy
 * @createTime 2018/8/15
 */
@XmlRootElement(name="mybatisMapping")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class EntityMapping<T> extends MybatisMapping<T> {

    public EntityMapping() {

    }

    public EntityMapping(MybatisConfiguration configuration, Class<T> entityClass) {
        super(configuration, entityClass);
    }

    @Override protected String getNamespacePostfix() {
        return entityClass.getCanonicalName();
    }

    @Override protected ResultMap buildResultMap(String resultMapId) {
        List<ResultMapping> resultMappings = new ArrayList<>();
        for (MybatisPropertyMapping propertyMapping : this.propertyMappings) {
            ResultMapping resultMapping = propertyMapping.toResultMapping(configuration.getConfiguration());
            resultMappings.add(resultMapping);
        }
        ResultMap resultMap = new ResultMap.Builder(configuration.getConfiguration(), resultMapId, entityClass,
            resultMappings).build();
        return resultMap;
    }

    @Override protected void initPropertyMappings() {
        MybatisPropertyMapping idPropertyMapping = null;
        MybatisPropertyMapping versionPropertyMapping = null;
        PropertyDescriptor[] propertyDescriptors = BeanUtils.getPropertyDescriptors(entityClass);
        for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
            if (propertyDescriptor.getName().equals("class")) {
                continue;
            }
            MybatisPropertyMapping
                propertyMapping = entityClassResolver.resolvePropertyMapping(entityClass, propertyDescriptor);
            if (propertyMapping == null) {
                continue;
            }

            if (propertyMapping.isVersionable() && propertyMapping.isPkable()) {
                throw new IllegalStateException(String.format("[%s.%s]不能同时是主键属性和版本属性",
                    entityClass.getCanonicalName(), idMapping.getProperty(), propertyMapping.getProperty()));
            }

            // 如果是版本属性
            if (propertyMapping.isVersionable()) {
                if (versionPropertyMapping != null) {
                    if (!versionPropertyMapping.isVersionGuess() && !propertyMapping.isVersionGuess()) {
                        throw new IllegalStateException(String.format("[%s]存在两个明确指定的版本属性[%s]和[%s]",
                            entityClass.getCanonicalName(), versionPropertyMapping.getProperty(), propertyMapping.getProperty()));
                    }
                    versionPropertyMapping = versionPropertyMapping.isVersionGuess() ? propertyMapping : versionPropertyMapping;
                } else {
                    versionPropertyMapping = propertyMapping;
                }
            }

            // 如果是主键属性
            if (propertyMapping.isPkable()) {
                if (idPropertyMapping != null) {
                    if (!idPropertyMapping.isPkGuess() && !propertyMapping.isPkGuess()) {
                        throw new IllegalStateException(String.format("[%s]存在两个明确指定的主键属性[%s]和[%s]",
                            entityClass.getCanonicalName(), idPropertyMapping.getProperty(), propertyMapping.getProperty()));
                    }
                    idPropertyMapping = idPropertyMapping.isPkGuess() ? propertyMapping : idPropertyMapping;
                } else {
                    idPropertyMapping = propertyMapping;
                }
            }
            addPropertyMapping(propertyMapping);
        }


        if (idPropertyMapping != null) {
            idPropertyMapping.setUpdatable(false);
            this.idMapping = entityClassResolver.resolveIdPropertyMapping(entityClass, idPropertyMapping);
        } else {
            throw new IllegalStateException(String.format("Entity[%s]没有找到主键信息", entityClass.getCanonicalName()));
        }

        if (versionPropertyMapping != null) {
            this.versionMapping = versionPropertyMapping;
        }
    }
}