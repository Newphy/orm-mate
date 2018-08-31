package cn.newphy.orm.mybatis;

import cn.newphy.mate.EntityDao;
import cn.newphy.mate.IdGenerator;
import cn.newphy.orm.mybatis.dialect.MybatisDialect;
import cn.newphy.orm.mybatis.dialect.mysql.MysqlDialect;
import cn.newphy.orm.mybatis.dialect.oracle.OracleDialect;
import cn.newphy.orm.mybatis.plugin.pager.PagePlugin;
import cn.newphy.orm.mybatis.strategy.PrefixCamelCaseTableNameStrategy;
import cn.newphy.orm.mybatis.strategy.TableNameStrategy;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.sql.DataSource;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.ResultMapping;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.scripting.LanguageDriver;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

/**
 * @author Newphy
 * @createTime 2018/7/31
 */
public class MybatisConfiguration implements ApplicationContextAware, InitializingBean {

    private static final String RESULT_MAP_SUFFIX = "ResultMap";
    /**
     * 全局配置对象
     */
    private Configuration configuration;
    /**
     * sqlSessionFactory
     */
    private SqlSessionFactory sqlSessionFactory;
    /**
     * 方言对象
     */
    private MybatisDialect dialect;
    /**
     * 表名策略
     */
    private TableNameStrategy tableNameStrategy;
    /**
     * ResultMap类型Map
     */
    private MultiValueMap<Class<?>, ResultMap> resultMapsByType = new LinkedMultiValueMap<>();
    /**
     * 全局id生成器
     */
    private IdGenerator globalIdGenerator;
    /**
     * Spring Context
     */
    private ApplicationContext applicationContext;


    private void init() {

    }

    @Override public void afterPropertiesSet() throws Exception {
        this.configuration = sqlSessionFactory.getConfiguration();
        // ResultMap根据类型分类
        Collection<ResultMap> resultMaps = configuration.getResultMaps();
        if(!CollectionUtils.isEmpty(resultMaps)) {
            for (ResultMap resultMap : resultMaps) {
                resultMapsByType.add(resultMap.getType(), resultMap);
            }
        }

        Environment env = sqlSessionFactory.getConfiguration().getEnvironment();
        DataSource ds = env.getDataSource();
        Connection conn = ds.getConnection();
        try {
            if(conn != null) {
                String driverName = conn.getMetaData().getDriverName();
                if(driverName != null) {
                    String sdriver = driverName.toLowerCase();
                    if(sdriver.contains("mysql")) {
                        dialect = new MysqlDialect(this, conn);
                    } else if (sdriver.contains("oracle")) {
                        dialect = new OracleDialect(this);
                    }
                }
            }
        } finally {
            if(conn != null) {
                conn.close();
            }
        }

        // 增加插件
        configuration.addInterceptor(new PagePlugin(this));
    }

    /**
     * 根据实体类型获取配置的ResultMap列表
     * @param entityType
     * @return
     */
    public List<ResultMap> getResultMapsByType(Class<?> entityType) {
        return resultMapsByType.get(entityType);
    }

    public MappedStatement.Builder createMappedStatementBuilder(MappedStatement origin, String id, SqlSource sqlSource) {
        MappedStatement.Builder builder = new MappedStatement.Builder(origin.getConfiguration(), id,
            sqlSource, origin.getSqlCommandType());
        builder.resource(origin.getResource());
        builder.fetchSize(origin.getFetchSize());
        builder.statementType(origin.getStatementType());
        builder.keyGenerator(origin.getKeyGenerator());
        if (origin.getKeyProperties() != null && origin.getKeyProperties().length != 0) {
            StringBuffer keyProperties = new StringBuffer();
            for (String keyProperty : origin.getKeyProperties()) {
                keyProperties.append(keyProperty).append(",");
            }
            keyProperties.delete(keyProperties.length() - 1, keyProperties.length());
            builder.keyProperty(keyProperties.toString());
        }

        // setStatementTimeout()
        builder.timeout(origin.getTimeout());

        // setStatementResultMap()
        builder.parameterMap(origin.getParameterMap());

        // setStatementResultMap()
        builder.resultMaps(origin.getResultMaps());
        builder.resultSetType(origin.getResultSetType());

        // setStatementCache()
        builder.cache(origin.getCache());
        builder.flushCacheRequired(origin.isFlushCacheRequired());
        builder.useCache(origin.isUseCache());
        return builder;
    }

    /**
     * 注册select sql
     * @param sql
     * @param namespace
     * @param resultMap
     * @return
     */
    public String registerDynamicSelectSql(String sql, String namespace, final ResultMap resultMap) {
        String statementId = getSqlId(namespace, SqlCommandType.SELECT, sql);
        if (!configuration.hasStatement(statementId)) {
            LanguageDriver languageDriver = configuration.getDefaultScriptingLanuageInstance();
            SqlSource sqlSource = languageDriver.createSqlSource(configuration, sql, Map.class);
            @SuppressWarnings("serial")
            MappedStatement ms = new MappedStatement.Builder(configuration, statementId, sqlSource,
                SqlCommandType.SELECT).resultMaps(new ArrayList<ResultMap>() {
                {
                    add(resultMap);
                }
            }).build();
            // 缓存
            configuration.addMappedStatement(ms);
        }
        return statementId;
    }

    public String registerDynamicCountSql(String sql, String namespace) {
        String statementId = getSqlId(namespace, SqlCommandType.SELECT, sql);
        if (!configuration.hasStatement(statementId)) {
            LanguageDriver languageDriver = configuration.getDefaultScriptingLanuageInstance();
            SqlSource sqlSource = languageDriver.createSqlSource(configuration, sql, Map.class);
            @SuppressWarnings("serial")
            MappedStatement ms = new MappedStatement.Builder(configuration, statementId, sqlSource,
                SqlCommandType.SELECT).resultMaps(new ArrayList<ResultMap>() {
                {
                    add(new ResultMap.Builder(configuration, "countResultMap", long.class,
                        new ArrayList<ResultMapping>(0)).build());
                }
            }).build();
            // 缓存
            configuration.addMappedStatement(ms);
        }
        return statementId;
    }

    /**
     * 注册update sql
     * @param sql
     * @param namespace
     * @return
     */
    public String registerDynamicUpdateSql(String sql, String namespace) {
        final String statementId = getSqlId(namespace, SqlCommandType.UPDATE, sql);
        if (!configuration.hasStatement(statementId)) {
            LanguageDriver languageDriver = configuration.getDefaultScriptingLanuageInstance();
            SqlSource sqlSource = languageDriver.createSqlSource(configuration, sql, Map.class);
            @SuppressWarnings("serial")
            MappedStatement ms = new MappedStatement.Builder(configuration, statementId, sqlSource,
                SqlCommandType.UPDATE).resultMaps(new ArrayList<ResultMap>() {
                {
                    add(new ResultMap.Builder(configuration, statementId + "_updateResultMap", int.class,
                        new ArrayList<ResultMapping>(0)).build());
                }
            }).build();
            // 缓存
            configuration.addMappedStatement(ms);
        }
        return statementId;
    }


    public static String getSqlId(String namespace, SqlCommandType sqlType, String sql) {
        return namespace + "." + sqlType + "_" + DigestUtils.md5DigestAsHex(sql.getBytes());
    }

    private static long hashCode(String sql) {
        long h = 0L;
        if (sql == null) {
            return h;
        }
        char[] val = sql.toCharArray();
        if (h == 0 && val.length > 0) {
            h = 17L;
            for (int i = 0; i < val.length; i++) {
                h = 31L * h + (long) val[i];
            }
        }
        return Math.abs(h);
    }

    private List<ResultMap> filterById(List<ResultMap> resultMaps, String id) {
        List<ResultMap> result = new ArrayList<>();
        String suffixName = "." + id;
        for (ResultMap resultMap : resultMaps) {
            if (resultMap.getId().endsWith(suffixName)) {
                result.add(resultMap);
            }
        }
        return result;
    }

    private ResultMap findByFullId(List<ResultMap> resultMaps, String fullId) {
        for (ResultMap resultMap : resultMaps) {
            if (resultMap.getId().equals(fullId)) {
                return resultMap;
            }
        }
        return null;
    }

    @Override public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    public TableNameStrategy getTableNameStrategy() {
        return tableNameStrategy;
    }

    public void setTableNameStrategy(TableNameStrategy tableNameStrategy) {
        this.tableNameStrategy = tableNameStrategy;
    }

    public IdGenerator getGlobalIdGenerator() {
        return globalIdGenerator;
    }

    public void setGlobalIdGenerator(IdGenerator globalIdGenerator) {
        this.globalIdGenerator = globalIdGenerator;
    }

    public MybatisDialect getDialect() {
        return dialect;
    }

    public SqlSessionFactory getSqlSessionFactory() {
        return sqlSessionFactory;
    }

    public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
        this.sqlSessionFactory = sqlSessionFactory;
    }
}
