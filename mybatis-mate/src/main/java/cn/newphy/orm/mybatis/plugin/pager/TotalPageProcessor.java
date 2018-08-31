package cn.newphy.orm.mybatis.plugin.pager;

import cn.newphy.mate.Page;
import cn.newphy.mate.Pageable;
import cn.newphy.mate.TotalPage;
import cn.newphy.orm.mybatis.MybatisConfiguration;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.plugin.Invocation;



public class TotalPageProcessor extends PageProcessor {

	public TotalPageProcessor(Invocation invocation, Pageable pageable, MybatisConfiguration configuration) {
		super(invocation, pageable, configuration);
	}

	@Override
	public Page<?> process() throws InvocationTargetException, IllegalAccessException {
		MappedStatement stmt = getMappedStatement();
		SqlSource sqlSource = stmt.getSqlSource();
		Object parameterObject = getParameterObjectWithPage(stmt.getConfiguration(), pageable);
		BoundSql boundSql = sqlSource.getBoundSql(parameterObject);
		
		// 查询总数
		long total = queryTotal(boundSql, parameterObject, pageable);
		// 查询分页
		List<?> content = queryPage(boundSql, parameterObject, pageable);
		return new TotalPage<>(content, pageable, total);
	}
}
