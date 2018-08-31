package cn.newphy.orm.mybatis.mapping;

import cn.newphy.mate.PropertyMapping;
import java.util.Arrays;
import javax.xml.bind.annotation.XmlTransient;
import org.apache.ibatis.mapping.ResultFlag;
import org.apache.ibatis.mapping.ResultMapping;
import org.apache.ibatis.mapping.ResultMapping.Builder;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

/**
 * 属性映射类
 *
 * @author Newphy
 * @createTime 2018/7/31
 */
public class MybatisPropertyMapping implements PropertyMapping {

    private Class<?> entityClass;
    private String property;
    private String column;
    private Class<?> javaType;
    private JdbcType jdbcType;
    private TypeHandler<?> typeHandler;
    private boolean pkable = false;
    private boolean pkGuess = false;
    private boolean versionable = false;
    private boolean versionGuess = false;
    private boolean updatable = true;
    private boolean insertable = true;
    private boolean nullable = true;

    /**
     * 转为ResultMapping
     * @param configuration
     * @return
     */
    public ResultMapping toResultMapping(Configuration configuration) {
        ResultMapping.Builder builder = new Builder(configuration, property, column, javaType);
        if (typeHandler != null) {
            builder.typeHandler(typeHandler);
        }
        if (this instanceof IdPropertyMapping) {
            builder.flags(Arrays.asList(ResultFlag.ID));
        }
        return builder.build();
    }

    /**
     * 获取属性值
     * @param entity
     * @return
     */
    public Object getPropertyValue(Object entity) {
        if (entity == null) {
            return null;
        }
        BeanWrapper beanWrapper = new BeanWrapperImpl(entity);
        return beanWrapper.getPropertyValue(getProperty());
    }

    /**
     * 设置属性值
     * @param entity
     * @param value
     */
    public void setPropertyValue(Object entity, Object value) {
        if (entity == null) {
            return;
        }
        BeanWrapper beanWrapper = new BeanWrapperImpl(entity);
        beanWrapper.setPropertyValue(getProperty(), value);
    }



    public Class<?> getEntityClass() {
        return entityClass;
    }

    public void setEntityClass(Class<?> entityClass) {
        this.entityClass = entityClass;
    }

    @Override
    public String getProperty() {
        return property;
    }

    public void setProperty(String name) {
        this.property = name;
    }

    @Override
    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public Class<?> getJavaType() {
        return javaType;
    }

    public void setJavaType(Class<?> javaType) {
        this.javaType = javaType;
    }

    public JdbcType getJdbcType() {
        return jdbcType;
    }

    public void setJdbcType(JdbcType jdbcType) {
        this.jdbcType = jdbcType;
    }

    public boolean isPkable() {
        return pkable;
    }

    public void setPkable(boolean pkable) {
        this.pkable = pkable;
    }

    public boolean isVersionable() {
        return versionable;
    }

    public void setVersionable(boolean versionable) {
        this.versionable = versionable;
    }

    public boolean isPkGuess() {
        return pkGuess;
    }

    public void setPkGuess(boolean pkGuess) {
        this.pkGuess = pkGuess;
    }

    public boolean isVersionGuess() {
        return versionGuess;
    }

    public void setVersionGuess(boolean versionGuess) {
        this.versionGuess = versionGuess;
    }

    public boolean isUpdatable() {
        return updatable;
    }

    public void setUpdatable(boolean updatable) {
        this.updatable = updatable;
    }

    public boolean isInsertable() {
        return insertable;
    }

    public void setInsertable(boolean insertable) {
        this.insertable = insertable;
    }

    public boolean isNullable() {
        return nullable;
    }

    public void setNullable(boolean nullable) {
        this.nullable = nullable;
    }
    @XmlTransient
    public TypeHandler<?> getTypeHandler() {
        return typeHandler;
    }

    public void setTypeHandler(TypeHandler<?> typeHandler) {
        this.typeHandler = typeHandler;
    }


}
