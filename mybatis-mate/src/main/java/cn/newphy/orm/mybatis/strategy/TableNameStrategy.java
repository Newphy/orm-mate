package cn.newphy.orm.mybatis.strategy;

import cn.newphy.orm.mybatis.MybatisConfiguration;
import cn.newphy.orm.mybatis.util.CamelCaseUtils;

/**
 * 表名策略
 * 
 * @author Newphy
 * @date 2018/7/31
 **/
public interface TableNameStrategy {

	
	/**
	 * 获得表名
	 * @param configuration
	 * @param entityClass
	 * @return
	 */
	String getTableName(MybatisConfiguration configuration, Class<?> entityClass);

}
