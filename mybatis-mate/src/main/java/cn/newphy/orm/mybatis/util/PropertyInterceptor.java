package cn.newphy.orm.mybatis.util;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import org.springframework.beans.BeanUtils;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

/**
 * @author Newphy
 * @createTime 2018/8/29
 */
public class PropertyInterceptor<T> {

    private final Class<T> beanClass;
    private PropertyMethodInterceptor readMethodInterceptor;
    private PropertyMethodInterceptor writeMethodInterceptor;

    public PropertyInterceptor(Class<T> beanClass) {
        this.beanClass = beanClass;
    }

    public T createProxy() {
        MethodInterceptor interceptor = new MethodInterceptor() {
            @Override public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy)
                throws Throwable {
                if (method.getDeclaringClass().equals(Object.class)) {
                    return null;
                }
                PropertyDescriptor[] propertyDescriptors = BeanUtils.getPropertyDescriptors(beanClass);
                for (int i = 0; i < propertyDescriptors.length; i++) {
                    PropertyDescriptor propertyDescriptor = propertyDescriptors[i];
                    if(method.equals(propertyDescriptor.getWriteMethod()) && objects.length == 1) {
                        if (writeMethodInterceptor != null) {
                            writeMethodInterceptor.intercept(propertyDescriptor, method, objects);
                        }
                        break;
                    }
                    if(method.equals(propertyDescriptor.getReadMethod()) && objects.length == 1) {
                        if (readMethodInterceptor != null) {
                            readMethodInterceptor.intercept(propertyDescriptor, method, objects);
                        }
                        break;
                    }
                }
                return null;
            }
        };

        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(beanClass);
        enhancer.setCallback(interceptor);
        T proxy = (T)enhancer.create();
        return proxy;
    }

    public void setReadMethodInterceptor(PropertyMethodInterceptor readMethodInterceptor) {
        this.readMethodInterceptor = readMethodInterceptor;
    }

    public void setWriteMethodInterceptor(PropertyMethodInterceptor writeMethodInterceptor) {
        this.writeMethodInterceptor = writeMethodInterceptor;
    }


    public interface PropertyMethodInterceptor {
        /**
         * 属性方法拦截
         * @param propertyDescriptor
         * @param method
         * @param objects
         * @throws Throwable
         */
        void intercept(PropertyDescriptor propertyDescriptor, Method method, Object[] objects) throws Throwable;
    }
}
