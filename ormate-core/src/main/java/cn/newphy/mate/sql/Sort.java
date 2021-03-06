package cn.newphy.mate.sql;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.util.StringUtils;

/**
 * 排序信息
 *
 * @author Newphy
 * @date 2018/8/21
 **/
public class Sort implements SqlSegment {
	private static final long serialVersionUID = 5737186511678863905L;

	private final List<Order> orders = new ArrayList<Order>() ;

	public Sort() {
	}

	public Sort(Order... orders) {
		this(Arrays.asList(orders));
	}

	public Sort(List<Order> orders) {
		if (null == orders || orders.isEmpty()) {
			throw new IllegalArgumentException("You have to provide at least one sort property to sort by!");
		}
		this.orders.addAll(orders);
	}


	public Sort(String... properties) {
		this(Order.DEFAULT_DIRECTION, properties);
	}


	public Sort(Direction direction, String... properties) {
		this(direction, properties == null ? new ArrayList<String>() : Arrays.asList(properties));
	}


	public Sort(Direction direction, List<String> properties) {
		if (properties == null || properties.isEmpty()) {
			throw new IllegalArgumentException("You have to provide at least one property to sort by!");
		}

		for (String property : properties) {
			this.orders.add(new Order(direction, property));
		}
	}
		
	
	/**
	 * @return the orders
	 */
	public List<Order> getOrders() {
		return orders;
	}


	/**
	 * Returns a new {@link Sort} consisting of the {@link Order}s of the current {@link Sort} combined with the given
	 * ones.
	 * 
	 * @param sort can be {@literal null}.
	 * @return
	 */
	public Sort and(Sort sort) {
		if (sort == null) {
			return this;
		}

		ArrayList<Order> these = new ArrayList<Order>(this.orders);

		for (Order order : sort.orders) {
			these.add(order);
		}

		return new Sort(these);
	}

	@Override public String toSql(SqlBuilder sqlBuilder) {
		return sqlBuilder.buildSortSql(this);
	}

	@Override
	public String toString() {
		return StringUtils.collectionToCommaDelimitedString(orders);
	}





}

