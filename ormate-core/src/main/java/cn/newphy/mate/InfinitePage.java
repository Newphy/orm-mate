package cn.newphy.mate;

import cn.newphy.mate.sql.Sort;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.core.convert.converter.Converter;
import org.springframework.util.Assert;

public class InfinitePage<T> extends ArrayList<T> implements Page<T> {
	private static final long serialVersionUID = 867755909294344406L;

	private final Pageable pageable;
	private final boolean hasNext;


	public InfinitePage(List<T> content, Pageable pageable, boolean hasNext) {
		Assert.notNull(pageable, "分页信息不能为空");
		if(content != null && pageable != null) {
			addAll(content.subList(0, Math.min(content.size(), pageable.getPageSize())));
		}
		this.pageable = pageable;
		this.hasNext = hasNext;
	}

	

	@Override
	public PageMode getMode() {
		return PageMode.INFINITE;
	}


	@Override
	public int getOffset() {
		return getPageNumber()*getPageSize();
	}

	@Override
	public int getTotalPages() {
		throw new UnsupportedOperationException();
	}


	@Override
	public long getTotalElements() {
		throw new UnsupportedOperationException();
	}


	@Override
	public boolean isHasNextPage() {
		return hasNext;
	}


	@Override
	public int getPageNumber() {
		return pageable == null ? 0 : pageable.getPageNumber();
	}

	@Override
	public int getPageSize() {
		return pageable == null ? 0 : pageable.getPageSize();
	}

	@Override
	public int getNumberOfElements() {
		return size();
	}


	@Override
	public boolean isHasPreviousPage() {
		return getPageNumber() > 0;
	}

	@Override
	public boolean isFirstPage() {
		return !isHasPreviousPage();
	}

	@Override
	public boolean isLastPage() {
		return !isHasNextPage();
	}

	@Override
	public Pageable getNextPageable() {
		return isHasNextPage() ? pageable.getNext() : null;
	}

	@Override
	public Pageable getPreviousPageable() {
		if (isHasPreviousPage()) {
			return pageable.getPreviousOrFirst();
		}
		return null;
	}


	@Override
	public Sort getSort() {
		return pageable == null ? null : pageable.getSort();
	}


	@Override
	public Map<String, Object> getParamMap() {
		return pageable == null ? null : pageable.getParamMap();
	}


	@Override
	public <S> Page<S> map(Converter<? super T, ? extends S> converter) {
		return new InfinitePage<S>(getConvertedContent(converter), pageable, isHasNextPage());
	}

	protected <S> List<S> getConvertedContent(Converter<? super T, ? extends S> converter) {
		Assert.notNull(converter, "Converter must not be null!");
		List<S> result = new ArrayList<S>(this.size());
		for (T element : this) {
			result.add(converter.convert(element));
		}
		return result;
	}


	@Override
	public String toString() {
		String contentType = "UNKNOWN";
		if (this.size() > 0) {
			contentType = get(0).getClass().getName();
		}
		return String.format("InfinitePage[pageNumber=%d, pageSize=%d, pageCount=%d, hasNext=%s, type=%s]",
				getPageNumber(), getPageSize(), getNumberOfElements(), isHasNextPage(), contentType);
	}

}
