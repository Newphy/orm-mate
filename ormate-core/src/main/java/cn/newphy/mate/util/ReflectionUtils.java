package cn.newphy.mate.util;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import org.springframework.beans.BeanUtils;
import org.springframework.core.annotation.AnnotationUtils;

/**
 * 反射工具类
 *
 * @author Newphy
 * @createTime 2018/8/2
 */
public abstract class ReflectionUtils {

    /**
     * 获取属性的Annotation值
     * @param beanClass
     * @param propertyName
     * @param annoType
     * @param <T>
     * @return
     */
    public static <T extends Annotation> T getAnnotationForProperty(
        Class<?> beanClass, String propertyName, Class<T> annoType) {

        T anno = null;
        Field field = org.springframework.util.ReflectionUtils.findField(beanClass, propertyName);
        if (field != null) {
            anno = AnnotationUtils.getAnnotation(field, annoType);
        }

        PropertyDescriptor propertyDescriptor = BeanUtils.getPropertyDescriptor(beanClass, propertyName);
        if (anno == null) {
            Method readMethod = propertyDescriptor.getReadMethod();
            if (readMethod != null) {
                anno = AnnotationUtils.findAnnotation(readMethod, annoType);
            }
        }

        if (anno == null) {
            Method writeMethod = propertyDescriptor.getWriteMethod();
            if (writeMethod != null) {
                anno = AnnotationUtils.findAnnotation(writeMethod, annoType);
            }
        }
        return anno;
    }

    /**
     * 是否数字类型
     * @param javaType
     * @return
     */
    public static boolean isNumbericType(Class<?> javaType) {
        if(Number.class.isAssignableFrom(javaType)
            || javaType.equals(byte.class)
            || javaType.equals(short.class)
            || javaType.equals(int.class)
            || javaType.equals(long.class)
            || javaType.equals(float.class)
            || javaType.equals(double.class)) {
            return true;
        }
        return false;
    }

}
