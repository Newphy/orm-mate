package cn.newphy.mate;

import java.io.Serializable;

/**
 * id生成器
 *
 * @author Newphy
 * @date 2018/8/3
 **/
public interface IdGenerator {

	/**
	 * 生成主键
	 * 
	 * @param entity
	 * @return
	 */
	Serializable generate(Object entity);

}
