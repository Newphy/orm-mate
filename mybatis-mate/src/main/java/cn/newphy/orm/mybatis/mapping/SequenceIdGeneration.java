package cn.newphy.orm.mybatis.mapping;

/**
 * @author Newphy
 * @createTime 2018/8/17
 */
public class SequenceIdGeneration {

    /**
     * 名称
     */
    private String name;
    /**
     * 序列名称
     */
    private String sequenceName;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSequenceName() {
        return sequenceName;
    }

    public void setSequenceName(String sequenceName) {
        this.sequenceName = sequenceName;
    }
}
