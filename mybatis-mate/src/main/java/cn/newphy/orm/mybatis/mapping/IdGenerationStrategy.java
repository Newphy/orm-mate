package cn.newphy.orm.mybatis.mapping;

import cn.newphy.mate.IdGenerator;

/**
 * Id生成策略
 * @author Newphy
 * @createTime 2018/8/17
 */
public class IdGenerationStrategy {


    /**
     * 策略类型：AUTO,IDENTITY,SEQUENCE,TABLE
     */
    private final IdGenerationStrategyType strategyType;

    /**
     * id生成器 for AUTO
     */
    private IdGenerator idGenerator;
    /**
     * sequence Id生成器
     */
    private SequenceIdGeneration sequenceIdGeneration;


    public IdGenerationStrategy(IdGenerationStrategyType strategyType) {
        this.strategyType = strategyType;
    }

    public IdGenerationStrategyType getStrategyType() {
        return strategyType;
    }

    public IdGenerator getIdGenerator() {
        return idGenerator;
    }

    public void setIdGenerator(IdGenerator idGenerator) {
        this.idGenerator = idGenerator;
    }

    public SequenceIdGeneration getSequenceIdGeneration() {
        return sequenceIdGeneration;
    }

    public void setSequenceIdGeneration(SequenceIdGeneration sequenceIdGeneration) {
        this.sequenceIdGeneration = sequenceIdGeneration;
    }
}
