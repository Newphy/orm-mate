/**
 *    Copyright 2010-2015 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package cn.newphy.orm.mybatis.spring;

import static org.springframework.util.Assert.notNull;

import cn.newphy.mate.EntityDao;
import cn.newphy.mate.EntityDaoFactory;
import cn.newphy.orm.mybatis.MybatisEntityDaoFactory;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;

import org.apache.ibatis.executor.ErrorContext;
import org.apache.ibatis.session.Configuration;
import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.springframework.beans.factory.FactoryBean;


/**
 * 扩展的MapperFactoryBean
 * <p>
 *     加载所有Mapper，如果有继承EntityDao，将加载对应的代理类
 * </p>
 * @author Newphy
 * @date 2018/7/27
 **/
public class MapperFactoryBean<T> extends SqlSessionDaoSupport implements FactoryBean<T> {

	private Class<T> mapperInterface;

	private boolean addToConfig = true;
	
	private EntityDaoFactory entityDaoFactory = null;
	
	private EntityDao<?> entityDao = null;

	public MapperFactoryBean() {
	}

	public MapperFactoryBean(Class<T> mapperInterface) {
		this.mapperInterface = mapperInterface;

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void checkDaoConfig() {
		super.checkDaoConfig();

		notNull(this.mapperInterface, "Property 'mapperInterface' is required");

		Configuration configuration = getSqlSession().getConfiguration();
		if (this.addToConfig && !configuration.hasMapper(this.mapperInterface)) {
			try {
				configuration.addMapper(this.mapperInterface);
			} catch (Exception e) {
				logger.error("Error while adding the mapper '" + this.mapperInterface + "' to configuration.", e);
				throw new IllegalArgumentException(e);
			} finally {
				ErrorContext.instance().reset();
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public T getObject() throws Exception {
		final T mapper = getSqlSession().getMapper(this.mapperInterface);
		Class<?> entityClass = getEntityClass();
		if(entityClass == null) {
			return mapper;
		} else {
			this.entityDao = entityDaoFactory.createEntityDao(mapperInterface, entityClass);
			return (T)Proxy.newProxyInstance(MapperFactoryBean.class.getClassLoader(), new Class<?>[]{mapperInterface}, new InvocationHandler() {
				@Override
				public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
					Class<?> targetClass = method.getDeclaringClass();
					try {
						if(EntityDao.class.equals(targetClass)) {
                            return method.invoke(entityDao, args);
                        } else {
                            return method.invoke(mapper, args);
                        }
					} catch (Exception e) {
						if (e instanceof InvocationTargetException) {
							Throwable t = ((InvocationTargetException)e).getTargetException();
							throw t;
						} else {
							throw e;
						}
					}
				}
			});
		}
	}

	
	private Class<?> getEntityClass() {
		if(EntityDao.class.isAssignableFrom(mapperInterface)) {
			Type[] genericInterfaces = mapperInterface.getGenericInterfaces();
			for (Type genericInterface : genericInterfaces) {
				if(genericInterface instanceof ParameterizedType && ((ParameterizedType) genericInterface).getRawType().equals(EntityDao.class)) {
					Type entityType = ((ParameterizedType)genericInterface).getActualTypeArguments()[0];
					if(entityType == null || Object.class.equals(entityType)) {
						throw new IllegalStateException("mapper接口[" + mapperInterface.getName() + "]没有指定EntityDao的实体类型");
					}
					return (Class<?>)entityType;
				}
			}
		}
		return null;
	}
	

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Class<T> getObjectType() {
		return this.mapperInterface;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isSingleton() {
		return true;
	}

	// ------------- mutators --------------

	/**
	 * Sets the mapper interface of the MyBatis mapper
	 *
	 * @param mapperInterface
	 *            class of the interface
	 */
	public void setMapperInterface(Class<T> mapperInterface) {
		this.mapperInterface = mapperInterface;
	}

	/**
	 * Return the mapper interface of the MyBatis mapper
	 *
	 * @return class of the interface
	 */
	public Class<T> getMapperInterface() {
		return mapperInterface;
	}

	/**
	 * If addToConfig is false the mapper will not be added to MyBatis. This
	 * means it must have been included in mybatis-config.xml.
	 * <p/>
	 * If it is true, the mapper will be added to MyBatis in the case it is not
	 * already registered.
	 * <p/>
	 * By default addToCofig is true.
	 *
	 * @param addToConfig
	 */
	public void setAddToConfig(boolean addToConfig) {
		this.addToConfig = addToConfig;
	}

	/**
	 * Return the flag for addition into MyBatis config.
	 *
	 * @return true if the mapper will be added to MyBatis in the case it is not
	 *         already registered.
	 */
	public boolean isAddToConfig() {
		return addToConfig;
	}

	/**
	 * @return the entityDaoFactory
	 */
	public EntityDaoFactory getEntityDaoFactory() {
		return entityDaoFactory;
	}

	/**
	 * @param entityDaoFactory the entityDaoFactory to set
	 */
	public void setEntityDaoFactory(EntityDaoFactory entityDaoFactory) {
		this.entityDaoFactory = entityDaoFactory;
	}
	
	
	
}
