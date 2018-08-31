package cn.newphy.orm.mybatis.mapping;

/**
 * 利用表格生成主键值
 *
 * @author Newphy
 * @createTime 2018/8/17
 */
public class TableIdGeneration {

    /**
     * (Required) A unique generator name that can be referenced
     * by one or more classes to be the generator for id values.
     */
    private String name;

    /**
     * (Optional) Name of table that stores the generated id values.
     * <p> Defaults to a name chosen by persistence provider.
     */
    private String table;

    /**
     * (Optional) The catalog of the table.
     * <p> Defaults to the default catalog.
     */
    private String catalog = "";

    /**
     * (Optional) The schema of the table.
     * <p> Defaults to the default schema for user.
     */
    private String schema = "";

    /**
     * (Optional) Name of the primary key column in the table.
     * <p> Defaults to a provider-chosen name.
     */
    private String pkColumnName;

    /**
     * (Optional) Name of the column that stores the last value generated.
     * <p> Defaults to a provider-chosen name.
     */
    private String valueColumnName;

    /**
     * (Optional) The primary key value in the generator table
     * that distinguishes this set of generated values from others
     * that may be stored in the table.
     * <p> Defaults to a provider-chosen value to store in the
     * primary key column of the generator table
     */
    private String pkColumnValue;

    /**
     * (Optional) The amount to increment by when allocating id
     * numbers from the generator.
     */
    private int allocationSize = 1;

}
