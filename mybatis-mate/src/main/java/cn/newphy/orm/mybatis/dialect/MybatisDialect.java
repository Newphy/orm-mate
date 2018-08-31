package cn.newphy.orm.mybatis.dialect;

import cn.newphy.mate.dialect.Dialect;
import cn.newphy.orm.mybatis.MybatisConfiguration;
import cn.newphy.orm.mybatis.dialect.mysql.MysqlDialect;
import cn.newphy.orm.mybatis.sql.builder.MybatisSqlBuilder;
import cn.newphy.orm.mybatis.sql.builder.PageSqlUtils;
import java.io.IOException;
import java.io.InputStream;
import javax.xml.transform.Templates;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;

/**
 * mybatis方言类
 *
 * @author Newphy
 * @createTime 2018/7/27
 */
public abstract class MybatisDialect implements Dialect {

    private volatile Templates xslTemplate;
    protected final MybatisConfiguration configuration;

    public MybatisDialect(MybatisConfiguration configuration) {
        this.configuration = configuration;
    }

    /**
     * 是否支持批量操作
     * @return
     */
    public abstract boolean supportBatch();

    /**
     * 获取模板地址
     * @return
     */
    protected abstract String getTemplatePath();

    /**
     * 获得模板
     * @return
     */
    public Templates getMapperTemplates() throws TransformerConfigurationException {
        if (xslTemplate == null) {
            synchronized (this) {
                if (xslTemplate == null) {
                    InputStream inputStream = null;
                    try {
                        inputStream = MysqlDialect.class.getResourceAsStream(getTemplatePath());
                        TransformerFactory factory = TransformerFactory.newInstance();
                        this.xslTemplate = factory.newTemplates(new StreamSource(inputStream));
                    } finally {
                        if (inputStream != null) {
                            try {
                                inputStream.close();
                            } catch (IOException e) {}
                        }
                    }
                }
            }
        }
        return xslTemplate;
    }

    /**
     * 获取sql构造器
     * @return
     */
    public MybatisSqlBuilder getSqlBuilder() {
        return new MybatisSqlBuilder() {

            @Override public String buildLimitSql(String sql, String offsetParam, String limitParam) {
                return PageSqlUtils.limit(sql, getDbType().name().toLowerCase(), offsetParam, limitParam);
            }

            @Override public String buildCountSql(String sql) {
                return PageSqlUtils.count(sql, getDbType().name().toLowerCase());
            }
        };
    }

}
