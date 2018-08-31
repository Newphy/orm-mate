package cn.newphy.orm.mybatis.strategy;

import cn.newphy.orm.mybatis.MybatisConfiguration;
import cn.newphy.orm.mybatis.mapping.TableMeta;
import cn.newphy.orm.mybatis.util.CamelCaseUtils;
import org.springframework.util.StringUtils;

/**
 * 带前缀驼峰表名命名
 * @author Newphy
 * @createTime 2018/8/8
 */
public class PrefixCamelCaseTableNameStrategy implements TableNameStrategy{

    private final String prefix;

    public PrefixCamelCaseTableNameStrategy(String prefix) {
        this.prefix = StringUtils.hasText(prefix) ? prefix : "";
    }

    @Override
    public String getTableName(MybatisConfiguration globalConfig, Class<?> entityClass) {
        String className = entityClass.getSimpleName();
        String tableName = CamelCaseUtils.camelCase2Underline(className);
        return getPrefix() + tableName;
    }

    /**
     * @return the prefix
     */
    public String getPrefix() {
        return prefix;
    }

}
