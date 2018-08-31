package cn.newphy.mate;

import cn.newphy.mate.sql.Sort;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.springframework.core.convert.converter.Converter;

/**
 * 分页结果
 * @author liuhui
 *
 * @param <T>
 */
public interface Page<T> extends List<T>, Serializable {

	/**
	 * 获得分页模式
	 * 
	 * @return
	 */
	PageMode getMode();

	/**
	 * 获得总页数
	 * 
	 * @return
	 */
	int getTotalPages();

	/**
	 * 获得总数据条数
	 * 
	 * @return
	 */
	long getTotalElements();

	/**
	 * 获得当前页的页码
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
	 * 获得当前页数据数量，数量一定会小于等于pageSize
	 * 
	 * @return
	 */
	int getNumberOfElements();

	/**
	 * 获得在总数据数中的排位
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
	 * 是否是第一页
	 * 
	 * @return
	 */
	boolean isFirstPage();

	/**
	 * 是否是最后一页
	 * 
	 * @return
	 */
	boolean isLastPage();

	/**
	 * 是否有下一页
	 * 
	 * @return
	 */
	boolean isHasNextPage();

	/**
	 * 是否有上一页
	 * 
	 * @return
	 */
	boolean isHasPreviousPage();

	/**
	 * 获得下一页
	 * 
	 * @return
	 */
	Pageable getNextPageable();

	/**
	 * 获得上一页
	 * 
	 * @return
	 */
	Pageable getPreviousPageable();

	/**
	 * 获得查询参数
	 *
	 * @return
	 */
	Map<String, Object> getParamMap();

	/**
	 * 转换
	 * 
	 * @param converter
	 * @return
	 */
	<S> Page<S> map(Converter<? super T, ? extends S> converter);
}
