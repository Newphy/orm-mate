package cn.newphy.orm.mybatis.mapping;

import org.springframework.util.StringUtils;

/**
 * 表元数据
 * @author Newphy
 * @createTime 2018/8/15
 */
public class TableMeta {

    /**
     * The catalog of the table
     */
    private String catalog = "";
    /**
     * The schema of the table
     */
    private String schema = "";
    /**
     * table name
     */
    private String name;

    /**
     * 获取完整表名
     * @return
     */
    public String getTableName() {
        return (StringUtils.hasText(catalog) ? catalog + "" : "")
            + (StringUtils.hasText(schema) ? schema + "." : "")
            + name;
    }



    public String getCatalog() {
        return catalog;
    }

    public void setCatalog(String catalog) {
        this.catalog = catalog;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
