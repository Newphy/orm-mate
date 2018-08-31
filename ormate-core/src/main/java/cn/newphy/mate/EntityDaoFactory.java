package cn.newphy.mate;

/**
 * EntityDao工厂类
 * 
 * @author Newphy
 * @date 2018/7/27
 **/
public abstract class EntityDaoFactory {
	
	/**
	 * 创建EntityDao
	 * @param daoClass
	 * @param entityClass
	 * @return
	 */
	public abstract <T> EntityDao<T> createEntityDao(Class<?> daoClass, Class<T> entityClass);
}
