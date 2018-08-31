package cn.newphy.orm.mybatis.plugin.pager;

import cn.newphy.mate.InfinitePage;
import cn.newphy.mate.Page;
import cn.newphy.mate.PageRequest;
import cn.newphy.mate.Pageable;
import cn.newphy.orm.mybatis.MybatisConfiguration;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.plugin.Invocation;



public class InfinitePageProcessor extends PageProcessor {

	public InfinitePageProcessor(Invocation invocation, Pageable pageable, MybatisConfiguration configuration) {
		super(invocation, pageable, configuration);
	}

	@Override
	public Page<?> process() throws InvocationTargetException, IllegalAccessException {
		MappedStatement stmt = getMappedStatement();
		Pageable pageablePlus = getPageablePlus(pageable);
		SqlSource sqlSource = stmt.getSqlSource();
		Object parameterObject = getParameterObjectWithPage(stmt.getConfiguration(), pageablePlus);
		BoundSql boundSql = sqlSource.getBoundSql(parameterObject);		

		// 查询分页
		List<?> content = queryPage(boundSql, parameterObject, pageablePlus);
		Page<?> page = new InfinitePage<>(content, pageable, content != null && content.size() > pageable.getPageSize());
		return page;
	}
	
	private Pageable getPageablePlus(final Pageable pageable) {
		@SuppressWarnings("serial") PageRequest
			pageablePlus = new PageRequest(pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort(), pageable.getPageMode()){
			@Override
			public int getPageSize() {
				return pageable.getPageSize() + 1;
			}
		};
		return pageablePlus;
	}
}
