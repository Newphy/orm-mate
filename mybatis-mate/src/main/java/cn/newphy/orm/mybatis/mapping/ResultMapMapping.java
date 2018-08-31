package cn.newphy.orm.mybatis.mapping;

import cn.newphy.mate.util.ReflectionUtils;
import cn.newphy.orm.mybatis.MybatisConfiguration;
import cn.newphy.orm.mybatis.exception.MybatisMateException;
import java.beans.PropertyDescriptor;
import javax.persistence.Column;
import javax.persistence.Version;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.ResultMapping;
import org.apache.ibatis.type.JdbcType;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

/**
 * @author Newphy
 * @createTime 2018/8/15
 */
@XmlRootElement(name="mybatisMapping")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class ResultMapMapping extends MybatisMapping {

    private final ResultMap sourceResultMap;

    public ResultMapMapping(MybatisConfiguration configuration, ResultMap resultMap) {
        super(configuration, resultMap.getType());
        this.sourceResultMap = resultMap;
    }

    public ResultMapMapping() {
        this.sourceResultMap = null;
    }

    @Override
    protected void initPropertyMappings() {
        if (CollectionUtils.isEmpty(sourceResultMap.getIdResultMappings())) {
            throw new IllegalStateException(String.format("ResultMap[%s]未指定主键字段", sourceResultMap.getId()));
        }
        if (sourceResultMap.getIdResultMappings().size() > 1) {
            throw new MybatisMateException(String.format("ResultMap[%s]指定了多个主键字段", sourceResultMap.getId()));
        }
        // 主键
        ResultMapping idResultMapping = sourceResultMap.getIdResultMappings().get(0);
        MybatisPropertyMapping
            idPropertyMapping = mappingProperty(entityClass, idResultMapping.getProperty(), idResultMapping);
        idPropertyMapping.setPkable(true);
        idPropertyMapping.setUpdatable(false);
        idPropertyMapping.setInsertable(false);
        idPropertyMapping.setVersionable(false);

        this.idMapping = entityClassResolver.resolveIdPropertyMapping(entityClass, idPropertyMapping);
        addPropertyMapping(this.idMapping);

        for (ResultMapping resultMapping : sourceResultMap.getResultMappings()) {
            if (idMapping != null && idMapping.getProperty().equals(resultMapping.getProperty())) {
                continue;
            }
            String property = resultMapping.getProperty();
            MybatisPropertyMapping propertyMapping = mappingProperty(entityClass, property, resultMapping);
            propertyMapping.setPkable(false);

            Column column = ReflectionUtils.getAnnotationForProperty(entityClass, resultMapping.getProperty(), Column.class);
            if (column != null) {
                propertyMapping.setInsertable(column.insertable());
                propertyMapping.setUpdatable(column.updatable());
            }

            // 是否是版本字段
            Version version = ReflectionUtils.getAnnotationForProperty(entityClass, resultMapping.getProperty(), Version.class);
            if (version != null) {
                if (!isVersionType(propertyMapping.getJdbcType())) {
                    throw new IllegalStateException(String.format("ResultMap[%s]版本字段[%s]只支持数字类型", sourceResultMap.getId(), property));
                }
                if (versionMapping != null && !versionMapping.isVersionGuess()) {
                    throw new IllegalStateException(String.format("实体类型[%s]存在多个版本控制字段", getEntityClass()));
                }
                propertyMapping.setVersionable(true);
                propertyMapping.setVersionGuess(false);
                this.versionMapping = propertyMapping;
            } else if(EntityClassResolver.VERSION_DEFAULT_NAME.equals(property)
                && isVersionType(propertyMapping.getJdbcType())
                && this.versionMapping != null) {
                propertyMapping.setVersionable(true);
                propertyMapping.setVersionGuess(true);
                this.versionMapping = propertyMapping;
            }
            addPropertyMapping(propertyMapping);
        }
    }

    @Override protected String getNamespacePostfix() {
        return sourceResultMap.getId();
    }

    @Override protected ResultMap buildResultMap(String resultMapId) {
        ResultMap resultMap = new ResultMap.Builder(configuration.getConfiguration(), resultMapId, entityClass,
            this.sourceResultMap.getResultMappings(), this.sourceResultMap.getAutoMapping()).build();
        return resultMap;
    }

    private MybatisPropertyMapping mappingProperty(Class<?> entityClass, String propertyName, ResultMapping resultMapping) {
        PropertyDescriptor propertyDescriptor = BeanUtils.getPropertyDescriptor(entityClass, propertyName);
        if (propertyDescriptor == null) {
            throw new MybatisMateException(
                String.format("映射ResultMap[%s]时,在[%s]找不到[%s]的属性",
                    sourceResultMap.getId(),
                    entityClass.getCanonicalName(),
                    resultMapping.getProperty()));
        }
        MybatisPropertyMapping propertyMapping = new MybatisPropertyMapping();
        propertyMapping.setColumn(resultMapping.getColumn());
        propertyMapping.setJdbcType(resultMapping.getJdbcType());
        propertyMapping.setProperty(resultMapping.getProperty());
        propertyMapping.setEntityClass(entityClass);
        propertyMapping.setJavaType(propertyDescriptor.getPropertyType());
        return propertyMapping;
    }

    private boolean isVersionType(JdbcType jdbcType) {
        return JdbcType.INTEGER == jdbcType || JdbcType.BIGINT == jdbcType || JdbcType.TINYINT == jdbcType
            || JdbcType.FLOAT == jdbcType || JdbcType.DOUBLE == jdbcType || JdbcType.DECIMAL == jdbcType;
    }
}
