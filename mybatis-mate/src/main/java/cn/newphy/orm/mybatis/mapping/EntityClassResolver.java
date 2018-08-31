package cn.newphy.orm.mybatis.mapping;

import cn.newphy.mate.IdGenerator;
import cn.newphy.mate.util.ReflectionUtils;
import cn.newphy.orm.mybatis.MybatisConfiguration;
import cn.newphy.orm.mybatis.strategy.TableNameStrategy;
import cn.newphy.orm.mybatis.util.CamelCaseUtils;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.ResultMapping;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;
import org.apache.ibatis.type.TypeHandlerRegistry;

/**
 * @author Newphy
 * @createTime 2018/8/15
 */
public class EntityClassResolver {

    /**
     * 缺省字段名称
     */
    public static final String VERSION_DEFAULT_NAME = "version";
    public static final String ID_DEFAULT_NAME = "id";

    private static final Map<Class<?>, JdbcType> JAVA_TYPE_JDBC_TYPE_MAP = new HashMap<>();

    private static volatile EntityClassResolver inst = null;


    public static EntityClassResolver getInstance(MybatisConfiguration configuration) {
        if (inst == null) {
            synchronized (EntityClassResolver.class) {
                if (inst == null) {
                    JAVA_TYPE_JDBC_TYPE_MAP.put(boolean.class, JdbcType.BOOLEAN);
                    JAVA_TYPE_JDBC_TYPE_MAP.put(Boolean.class, JdbcType.BOOLEAN);

                    JAVA_TYPE_JDBC_TYPE_MAP.put(byte.class, JdbcType.TINYINT);
                    JAVA_TYPE_JDBC_TYPE_MAP.put(Byte.class, JdbcType.TINYINT);

                    JAVA_TYPE_JDBC_TYPE_MAP.put(short.class, JdbcType.SMALLINT);
                    JAVA_TYPE_JDBC_TYPE_MAP.put(Short.class, JdbcType.SMALLINT);

                    JAVA_TYPE_JDBC_TYPE_MAP.put(int.class, JdbcType.INTEGER);
                    JAVA_TYPE_JDBC_TYPE_MAP.put(Integer.class, JdbcType.INTEGER);

                    JAVA_TYPE_JDBC_TYPE_MAP.put(long.class, JdbcType.BIGINT);
                    JAVA_TYPE_JDBC_TYPE_MAP.put(Long.class, JdbcType.BIGINT);

                    JAVA_TYPE_JDBC_TYPE_MAP.put(float.class, JdbcType.FLOAT);
                    JAVA_TYPE_JDBC_TYPE_MAP.put(Float.class, JdbcType.FLOAT);

                    JAVA_TYPE_JDBC_TYPE_MAP.put(double.class, JdbcType.DOUBLE);
                    JAVA_TYPE_JDBC_TYPE_MAP.put(Double.class, JdbcType.DOUBLE);

                    JAVA_TYPE_JDBC_TYPE_MAP.put(String.class, JdbcType.VARCHAR);
                    JAVA_TYPE_JDBC_TYPE_MAP.put(char.class, JdbcType.CHAR);
                    JAVA_TYPE_JDBC_TYPE_MAP.put(Character.class, JdbcType.CHAR);

                    JAVA_TYPE_JDBC_TYPE_MAP.put(Date.class, JdbcType.TIMESTAMP);

                    JAVA_TYPE_JDBC_TYPE_MAP.put(BigInteger.class, JdbcType.BIGINT);
                    JAVA_TYPE_JDBC_TYPE_MAP.put(BigDecimal.class, JdbcType.DECIMAL);
                    try {
                        String className = "javax.persistence.Table";
                        Class<?> tableAnnotationClass = Class.forName(className);
                        if (tableAnnotationClass != null && Annotation.class.isAssignableFrom(tableAnnotationClass)) {
                            inst = new JpaEntityClassResolver(configuration);
                        } else {
                            inst = new EntityClassResolver(configuration);
                        }
                    } catch (ClassNotFoundException e) {
                        inst = new EntityClassResolver(configuration);
                    }
                }
            }
        }
        return inst;
    }


    protected final MybatisConfiguration configuration;

    EntityClassResolver(MybatisConfiguration configuration) {
        this.configuration = configuration;
    }

    /**
     * 获取表元数据
     * MybatisConfiguration configuration,
     * @param entityClass
     * @return
     */
    public TableMeta getTableMeta(Class<?> entityClass) {
        TableMeta tableMeta = null;
        TableNameStrategy strategy = configuration.getTableNameStrategy();
        if (strategy != null) {
            tableMeta = new TableMeta();
            String tableName = strategy.getTableName(configuration, entityClass);
            tableMeta.setName(tableName);
        }
        return tableMeta;
    }

    /**
     * 获取属性元数据
     * @param entityClass
     * @param propertyDescriptor
     * @return
     */
    public MybatisPropertyMapping resolvePropertyMapping(Class<?> entityClass, PropertyDescriptor propertyDescriptor) {
        String propertyName = propertyDescriptor.getName();

        MybatisPropertyMapping mapping = new MybatisPropertyMapping();
        mapping.setEntityClass(entityClass);
        mapping.setProperty(propertyName);
        mapping.setColumn(CamelCaseUtils.camelCase2Underline(propertyName));
        mapping.setJavaType(propertyDescriptor.getPropertyType());
        JdbcType jdbcType = JAVA_TYPE_JDBC_TYPE_MAP.get(mapping.getJavaType());
        TypeHandlerRegistry typeHandlerRegistry = configuration.getConfiguration().getTypeHandlerRegistry();
        if (jdbcType == null) {
            TypeHandler<?> typeHandler = typeHandlerRegistry.getTypeHandler(mapping.getJavaType());
            mapping.setTypeHandler(typeHandler);
        } else {
            mapping.setTypeHandler(typeHandlerRegistry.getTypeHandler(jdbcType));
        }

        // 判断是否版本字段
        if (!mapping.isVersionable()
            && VERSION_DEFAULT_NAME.equals(propertyName)
            && ReflectionUtils.isNumbericType(mapping.getJavaType())) {
            mapping.setVersionable(true);
            mapping.setVersionGuess(true);
        }

        // 判断是否主键
        if (!mapping.isPkable() && ID_DEFAULT_NAME.equals(propertyName)) {
            mapping.setPkable(true);
            mapping.setPkGuess(true);
        }
        return mapping;
    }

    /**
     * 解析主键映射
     * @param entityClass
     * @param idMapping
     * @return
     */
    public IdPropertyMapping resolveIdPropertyMapping(Class<?> entityClass, MybatisPropertyMapping idMapping) {
        IdGenerator idGenerator = configuration.getGlobalIdGenerator();
        IdGenerationStrategy generationStrategy = new IdGenerationStrategy(IdGenerationStrategyType.AUTO);
        generationStrategy.setIdGenerator(idGenerator);
        return new IdPropertyMapping(idMapping, generationStrategy);
    }


    private MybatisPropertyMapping getPropertyMappingFromResultMap(Class<?> entityClass, String propertyName) {
        List<ResultMap> resultMaps = configuration.getResultMapsByType(entityClass);
        if (resultMaps != null && resultMaps.size() > 0) {
            for (ResultMap resultMap : resultMaps) {
                List<ResultMapping> resultMappings = resultMap.getResultMappings();
                for (ResultMapping resultMapping : resultMappings) {
                    if (resultMapping.getProperty().equals(propertyName)) {
                        MybatisPropertyMapping mapping = new MybatisPropertyMapping();
                        mapping.setEntityClass(entityClass);
                        mapping.setProperty(resultMapping.getProperty());
                        mapping.setColumn(resultMapping.getColumn());
                        mapping.setJavaType(resultMapping.getJavaType());
                        mapping.setJdbcType(resultMapping.getJdbcType());
                        mapping.setTypeHandler(resultMapping.getTypeHandler());
                        if (resultMap.getIdResultMappings().contains(resultMapping)) {
                            mapping.setPkable(true);
                        }
                        return mapping;
                    }
                }
            }
        }
        return null;
    }




}
