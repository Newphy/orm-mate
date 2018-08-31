package cn.newphy.mate;

import cn.newphy.mate.sql.Direction;
import cn.newphy.mate.sql.Sort;
import java.util.Map;

/**
 * 分页查询
 * 
 * @author liuhui
 *
 */
public interface Pageable {

	/**
	 * 获得排序模式
	 * 
	 * @return
	 */
	PageMode getPageMode();

	/**
	 * 获得当前页码
	 * 
	 * @return
	 */
	int getPageNumber();

	/**
	 * 获得页大小
	 * 
	 * @return
	 */
	int getPageSize();

	/**
	 * 获得总数据中的偏移值
	 * 
	 * @return
	 */
	int getOffset();

	/**
	 * 获得排序信息
	 * 
	 * @return
	 */
	Sort getSort();

	/**
	 * 设置排序信息
	 * 
	 * @param sort
	 */
	void setSort(Sort sort);

	/**
	 * 获得以下页信息
	 * 
	 * @return
	 */
	Pageable getNext();

	/**
	 * 获得上一页或第一页信息
	 * 
	 * @return
	 */
	Pageable getPreviousOrFirst();

	/**
	 * 获得第一页信息
	 * 
	 * @return
	 */
	Pageable getFirst();

	/**
	 * 是否有上一页
	 * 
	 * @return
	 */
	boolean isHasPrevious();

	/**
	 * 根据<code>property</code>排序
	 * 
	 * @param property
	 * @param direction
	 */
	void orderBy(Direction direction, String property);

	/**
	 * 根据<code>property</code>正序排列
	 * 
	 * @param property
	 */
	void orderAsc(String property);

	/**
	 * 根据<code>property</code>倒序排列
	 * 
	 * @param property
	 */
	void orderDesc(String property);

	/**
	 * 增加查询参数
	 *
	 * @param key
	 * @param value
	 */
	void addParameter(String key, Object value);

	/**
	 * 增加查询参数Map
	 *
	 * @param map
	 */
	void addParameters(Map<String, ?> map);

	/**
	 * 获得查询参数
	 *
	 * @return
	 */
	Map<String, Object> getParamMap();
}
