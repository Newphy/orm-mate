package cn.newphy.mate;

/**
 * 分页模式
 * @author liuhui
 *
 */
public enum PageMode {

	/**
	 * 总数模式，需要获取记录总数
	 */
	TOTAL,

	/**
	 * 无尽模式,只需知道是否有下一页
	 */
	INFINITE;

}
