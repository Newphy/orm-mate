package cn.newphy.orm.mybatis.plugin.pager;

import cn.newphy.mate.Page;
import cn.newphy.mate.PageMode;
import cn.newphy.mate.Pageable;
import cn.newphy.orm.mybatis.MybatisConfiguration;
import cn.newphy.orm.mybatis.ParamConst;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;



@Intercepts({@Signature(
		type= Executor.class,
		method = "query",
		args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class})})
public class PagePlugin implements Interceptor {
	
    private final MybatisConfiguration configuration;

	public PagePlugin(MybatisConfiguration configuration) {
		this.configuration = configuration;
	}

	@Override
	public Object intercept(Invocation invocation) throws Throwable {
        Object[] args = invocation.getArgs();

        // 采用rowBounds进行分页, 不进行分页拦截
        RowBounds rowBounds = (RowBounds)args[ParamConst.IDX_ROWBOUNDS];
        if(rowBounds != null && rowBounds != RowBounds.DEFAULT) {
        	return invocation.proceed();
        }

        // 返回结果不为Page类型，也不进行分页拦截
        Class<?> resultType = invocation.getMethod().getReturnType();
        if(!List.class.isAssignableFrom(resultType)) {
        	return invocation.proceed();        	
        }
        
        // 没有分页参数，不进行分页拦截        
        final Object parameterObject = args[ParamConst.IDX_PARAMETER_OBJECT];
        Pageable pageable = getPageable(parameterObject);
        if(pageable == null) {
        	return invocation.proceed();        	
        }

        // 分页处理
        PageProcessor pageProcessor = null;
        if(pageable.getPageMode() == PageMode.TOTAL) {
        	pageProcessor = new TotalPageProcessor(invocation, pageable, configuration);
        } else {
        	pageProcessor = new InfinitePageProcessor(invocation, pageable, configuration);
        }
    	Page<?> page = pageProcessor.process();
    	return page;
	}

	@SuppressWarnings("unchecked")
	private Pageable getPageable(Object parameter) {
		if(parameter instanceof Pageable) {
			return (Pageable)parameter;
		} else if(parameter instanceof Map) {
			Map<String, Object> paramMap = (Map<String, Object>)parameter;
			for (Object value : paramMap.values()) {
				if(value instanceof Pageable) {
					return (Pageable)value;
				}
			}
		}
		return null;
	}
	
	
	@Override
	public Object plugin(Object target) {
		return Plugin.wrap(target, this);
	}

	@Override
	public void setProperties(Properties properties) {
		
	}
}
