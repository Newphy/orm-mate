package cn.newphy.orm.mybatis.spring;

import cn.newphy.mate.EntityDao;
import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import javax.swing.text.html.parser.Entity;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.beans.factory.support.MergedBeanDefinitionPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.core.ResolvableType;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

/**
 * 标注Dao注解的注入类
 * 
 * @author Newphy
 * @date 2018/7/27
 **/
public class EntityDaoAutowiredBeanProcessor extends InstantiationAwareBeanPostProcessorAdapter
	implements MergedBeanDefinitionPostProcessor, BeanDefinitionRegistryPostProcessor  {

	private Log log = LogFactory.getLog(EntityDaoAutowiredBeanProcessor.class);

	private static final String BEAN_NAME_PREFIX = "entityDao_";

	private BeanDefinitionRegistry registry;

	private final Set<Class<? extends Annotation>> autowiredAnnotationTypes = new LinkedHashSet<Class<? extends Annotation>>();

	private Map<String, EntityDaoBeanRegister> registerMap = new HashMap<>();

	public EntityDaoAutowiredBeanProcessor() {
		this.autowiredAnnotationTypes.add(Autowired.class);
		this.autowiredAnnotationTypes.add(Value.class);
		try {
			this.autowiredAnnotationTypes.add((Class<? extends Annotation>)
				ClassUtils.forName("javax.inject.Inject", AutowiredAnnotationBeanPostProcessor.class.getClassLoader()));
		}
		catch (ClassNotFoundException ex) {
			// JSR-330 API not available - simply skip.
		}
	}

	@Override public Object postProcessBeforeInstantiation(final Class<?> beanClass, String beanName) throws BeansException {

		return null;
	}

	@Override
	public void postProcessMergedBeanDefinition(RootBeanDefinition beanDefinition, final Class<?> beanType, String beanName) {
		Class<?> targetClass = beanType;
		do {
			ReflectionUtils.doWithLocalFields(targetClass, new ReflectionUtils.FieldCallback() {
				@Override
				public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
					AnnotationAttributes ann = findAutowiredAnnotation(field);
					if (ann != null) {
						if (Modifier.isStatic(field.getModifiers())) {
							return;
						}
						if (field.getType().equals(EntityDao.class)) {
							doFiledElmenet(field);
						}
					}
				}
			});

			ReflectionUtils.doWithLocalMethods(targetClass, new ReflectionUtils.MethodCallback() {
				@Override
				public void doWith(Method method) throws IllegalArgumentException, IllegalAccessException {
					Method bridgedMethod = BridgeMethodResolver.findBridgedMethod(method);
					if (!BridgeMethodResolver.isVisibilityBridgeMethodPair(method, bridgedMethod)) {
						return;
					}
					AnnotationAttributes ann = findAutowiredAnnotation(bridgedMethod);
					if (ann != null && method.equals(ClassUtils.getMostSpecificMethod(method, beanType))) {
						if (Modifier.isStatic(method.getModifiers())) {
							return;
						}
						Class<?>[] parameterTypes = method.getParameterTypes();
						if (parameterTypes.length == 0) {
							return;
						}
						for (int i = 0; i < parameterTypes.length; i++) {
							Class<?> parameterType = parameterTypes[i];
							if (parameterType.equals(EntityDao.class)) {
								doMethodElement(method);
								break;
							}
						}
					}
				}
			});
			targetClass = targetClass.getSuperclass();
		}
		while (targetClass != null && targetClass != Object.class);
	}

	private void doFiledElmenet(Field field) {
		Type type = field.getGenericType();
		if (type instanceof ParameterizedType) {
			ParameterizedType parameterizedType = (ParameterizedType) type;
			Type[] argumentTypes = parameterizedType.getActualTypeArguments();
			if (argumentTypes.length == 1) {
				Class<?> entityClass = (Class<?>) argumentTypes[0];
				addEntityDaoRegister(entityClass, null);
			}
		}
	}

	private void doMethodElement(Method method) {
		Type[] genericParameterTypes = method.getGenericParameterTypes();
		Class<?>[] parameterTypes = method.getParameterTypes();
		for (int i = 0; i < parameterTypes.length; i++) {
			Class<?> parameterType = parameterTypes[i];
			if (parameterType.equals(EntityDao.class) && genericParameterTypes[i] instanceof ParameterizedType) {
				ParameterizedType parameterizedType = (ParameterizedType)genericParameterTypes[i];
				Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
				if (actualTypeArguments.length == 1) {
					Class<?> entityClass = (Class<?>)actualTypeArguments[0];
					addEntityDaoRegister(entityClass, null);
				}
			}
		}
	}


	private void addEntityDaoRegister(Class<?> entityClass, String resultMapId) {
		if (Object.class.equals(entityClass)) {
			return;
		}
		if (!registerMap.containsKey(entityClass.getCanonicalName())) {
			EntityDaoBeanRegister register = new EntityDaoBeanRegister(entityClass, null);
			registerMap.put(entityClass.getCanonicalName(), register);
			registerEntityDao(register);
			log.debug(String.format("完成实体类[%s]EntityDao的注册", entityClass.getCanonicalName()));
		}
	}


	private AnnotationAttributes findAutowiredAnnotation(AccessibleObject ao) {
		if (ao.getAnnotations().length > 0) {
			for (Class<? extends Annotation> type : this.autowiredAnnotationTypes) {
				AnnotationAttributes attributes = AnnotatedElementUtils.getMergedAnnotationAttributes(ao, type);
				if (attributes != null) {
					return attributes;
				}
			}
		}
		return null;
	}

	@Override public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
		this.registry = registry;
	}

	@Override public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

	}

	private void registerEntityDao(EntityDaoBeanRegister entityDaoRegister) throws BeansException {
		ResolvableType resolvableType = ResolvableType.forClassWithGenerics(EntityDao.class, entityDaoRegister.entityClass);

		RootBeanDefinition rootBeanDefinition = new RootBeanDefinition();
		rootBeanDefinition.setTargetType(resolvableType);
		rootBeanDefinition.setBeanClass(EntityDaoFactoryBean.class);
		rootBeanDefinition.getPropertyValues().add("entityClass", entityDaoRegister.entityClass);
		rootBeanDefinition.getPropertyValues().add("resultMapId", entityDaoRegister.resultMapId);
		rootBeanDefinition.getPropertyValues().add("entityDaoFactory", new RuntimeBeanReference(MapperScannerConfigurer.ENTITY_DAO_FACTORY_BEAN_NAME));

		registry.registerBeanDefinition(entityDaoRegister.getBeanName(), rootBeanDefinition);
	}


	private class EntityDaoBeanRegister {
		private Class<?> entityClass;
		private String resultMapId;

		public EntityDaoBeanRegister(Class<?> entityClass, String resultMapId) {
			this.entityClass = entityClass;
			this.resultMapId = resultMapId;
		}

		public String getBeanName() {
			if (StringUtils.hasText(resultMapId)) {
				return BEAN_NAME_PREFIX + resultMapId.replaceAll("\\.", "_");
			} else {
				return BEAN_NAME_PREFIX + entityClass.getCanonicalName().replaceAll("\\.", "_");
			}
		}
	}
}
