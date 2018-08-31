package cn.newphy.orm.mybatis.mapping;

import cn.newphy.mate.IdGenerator;
import cn.newphy.mate.util.ReflectionUtils;
import cn.newphy.orm.mybatis.MybatisConfiguration;
import cn.newphy.orm.mybatis.idgenerator.UUIDIdGenerator;
import java.beans.PropertyDescriptor;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

/**
 * JPA实际实现
 * @author Newphy
 * @createTime 2018/8/15
 */
public class JpaEntityClassResolver extends EntityClassResolver {

    private static final String GENERATOR_UUID = "uuid";

    JpaEntityClassResolver(MybatisConfiguration configuration) {
        super(configuration);
    }

    @Override public TableMeta getTableMeta(Class<?> entityClass) {
        TableMeta tableMeta = null;
        Table table = entityClass.getAnnotation(Table.class);
        if (table != null) {
            tableMeta = new TableMeta();
            tableMeta.setName(table.name());
            tableMeta.setCatalog(table.catalog());
            tableMeta.setSchema(table.schema());
        }
        if (tableMeta == null) {
            tableMeta = super.getTableMeta(entityClass);
        }
        return tableMeta;
    }

    @Override public MybatisPropertyMapping resolvePropertyMapping(Class<?> entityClass, PropertyDescriptor propertyDescriptor) {
        String propertyName = propertyDescriptor.getName();
        Transient ignored =ReflectionUtils.getAnnotationForProperty(entityClass, propertyName, Transient.class);
        if (ignored != null) {
            return null;
        }

        MybatisPropertyMapping mapping = super.resolvePropertyMapping(entityClass, propertyDescriptor);
        Column column = ReflectionUtils.getAnnotationForProperty(entityClass, propertyName, Column.class);
        if (column != null) {
            if (StringUtils.hasText(column.name())) {
                mapping.setColumn(column.name());
            }
            mapping.setInsertable(column.insertable());
            mapping.setUpdatable(column.updatable());
            mapping.setNullable(column.nullable());
        }

        Version version = ReflectionUtils.getAnnotationForProperty(entityClass, propertyName, Version.class);
        if (version != null) {
            if(!ReflectionUtils.isNumbericType(propertyDescriptor.getPropertyType())) {
                throw new IllegalStateException(String.format("[%s.%s]标注@Version属性类型只能为数字类型",
                    entityClass.getCanonicalName(), propertyDescriptor.getName()));
            }
            mapping.setVersionable(true);
            mapping.setVersionGuess(false);
            mapping.setInsertable(true);
            mapping.setUpdatable(true);
        }

        Id id = ReflectionUtils.getAnnotationForProperty(entityClass, propertyName, Id.class);
        if (id != null) {
            mapping.setPkable(true);
            mapping.setPkGuess(false);
            mapping.setNullable(false);
            mapping.setInsertable(false);
            mapping.setUpdatable(false);
            mapping.setVersionable(false);
            mapping.setVersionGuess(false);
        }
        return mapping;
    }

    @Override public IdPropertyMapping resolveIdPropertyMapping(Class<?> entityClass, MybatisPropertyMapping idMapping) {
        IdPropertyMapping idPropertyMapping = null;
        // id生成策略
        GeneratedValue generatedValue =
            ReflectionUtils.getAnnotationForProperty(entityClass, idMapping.getProperty(), GeneratedValue.class);
        if (generatedValue != null) {
            IdGenerationStrategy generationStrategy = null;
            GenerationType generationType = generatedValue.strategy();
            switch (generationType) {
                case AUTO: {
                    generationStrategy = resolveAutoIdGeneration(generatedValue);
                    break;
                }
                case IDENTITY: {
                    generationStrategy = new IdGenerationStrategy(IdGenerationStrategyType.valueOf(generationType.name()));
                    break;
                }
                case SEQUENCE: {
                    SequenceGenerator sequenceGenerator =
                        ReflectionUtils.getAnnotationForProperty(entityClass, idMapping.getProperty(), SequenceGenerator.class);
                    if (sequenceGenerator == null) {
                        throw new IllegalStateException(String.format("[%s.%s]未配置@SequenceGenerator",
                            entityClass.getCanonicalName(), idMapping.getProperty()));
                    }
                    generationStrategy = resolveSequenceIdGeneration(generatedValue, sequenceGenerator);
                    break;
                }
                case TABLE: {
                    // TODO add table strategy
                    generationStrategy = new IdGenerationStrategy(IdGenerationStrategyType.TABLE);
                    break;
                }
            }
            idPropertyMapping = new IdPropertyMapping(idMapping, generationStrategy);
        } else {
            idPropertyMapping = super.resolveIdPropertyMapping(entityClass, idMapping);
        }
        return idPropertyMapping;
    }

    private IdGenerationStrategy resolveAutoIdGeneration(GeneratedValue generatedValue) {
        IdGenerationStrategy idGenerationStrategy = new IdGenerationStrategy(IdGenerationStrategyType.AUTO);
        String generator = generatedValue.generator();
        if (StringUtils.hasText(generator)) {
            if (GENERATOR_UUID.equals(generator.toLowerCase())) {
                idGenerationStrategy.setIdGenerator(new UUIDIdGenerator());
            } else {
                try {
                    Class<?> generatorClass = ClassUtils.forName(generator, this.getClass().getClassLoader());
                    if (!IdGenerator.class.isAssignableFrom(generatorClass)) {
                        throw new IllegalStateException(String.format("[%s]不是%s的实现类", generator, IdGenerator.class.getCanonicalName()));
                    }
                    IdGenerator idGenerator = (IdGenerator) generatorClass.newInstance();
                    idGenerationStrategy.setIdGenerator(idGenerator);
                } catch (ClassNotFoundException e) {
                    throw new IllegalStateException(String.format("找不到[%s]的IdGenerator的实现类", generator));
                } catch (IllegalAccessException | InstantiationException e) {
                    throw new IllegalStateException(String.format("不能实例化[%s]的IdGenerator的实现类", generator));
                }
            }
        } else if(configuration.getGlobalIdGenerator() != null){
            idGenerationStrategy.setIdGenerator(configuration.getGlobalIdGenerator());
        }
        return idGenerationStrategy;
    }


    private IdGenerationStrategy resolveSequenceIdGeneration(GeneratedValue generatedValue, SequenceGenerator sequenceGenerator) {
        IdGenerationStrategy idGenerationStrategy = new IdGenerationStrategy(IdGenerationStrategyType.SEQUENCE);
        SequenceIdGeneration sequenceIdGeneration = new SequenceIdGeneration();
        sequenceIdGeneration.setName(sequenceGenerator.name());
        sequenceIdGeneration.setSequenceName(sequenceGenerator.sequenceName());
        idGenerationStrategy.setSequenceIdGeneration(sequenceIdGeneration);
        return idGenerationStrategy;
    }
}
