package cn.newphy.orm.mybatis;

import cn.newphy.mate.EntityDao;
import cn.newphy.mate.EntityDaoFactory;
import java.util.HashMap;
import java.util.Map;

import javax.swing.text.html.parser.Entity;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.InitializingBean;

/**
 * EntityDao 工厂类
 *
 * @author Newphy
 * @date 2018/7/27
 **/
public class MybatisEntityDaoFactory extends EntityDaoFactory implements InitializingBean {

	private SqlSessionFactory sqlSessionFactory;
	private MybatisConfiguration configuration;

	@Override
	public <T> EntityDao<T> createEntityDao(Class<?> mapperClass, Class<T> entityClass) {
		if (entityClass == null) {
			return null;
		}
		EntityDao<T> entityDao = new MybatisEntityDao<T>(configuration, sqlSessionFactory, mapperClass, entityClass);
		return entityDao;
	}


	public <T> EntityDao<T> createEntityDaoByResultMap(Class<?> mapperClass, Class<T> entityClass, String resultMapId) {
		if (entityClass == null) {
			return null;
		}
		EntityDao<T> entityDao = new MybatisEntityDao<T>(configuration, sqlSessionFactory, mapperClass, entityClass, resultMapId);
		return entityDao;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
//		Configuration configuration = sqlSessionFactory.getConfiguration();
//		configuration.addInterceptor(new PagePlugin(this.configuration));
	}




	/**
	 * @return the sqlSessionFactory
	 */
	public SqlSessionFactory getSqlSessionFactory() {
		return sqlSessionFactory;
	}

	/**
	 * @param sqlSessionFactory
	 *            the sqlSessionFactory to set
	 */
	public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
		this.sqlSessionFactory = sqlSessionFactory;
	}

	/**
	 * @return the configuration
	 */
	public MybatisConfiguration getConfiguration() {
		return configuration;
	}

	/**
	 * @param configuration
	 *            the configuration to set
	 */
	public void setConfiguration(MybatisConfiguration configuration) {
		this.configuration = configuration;
	}

}
