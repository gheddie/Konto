package de.gravitex.accounting.filter.impl.base;

import de.gravitex.accounting.filter.FilterUtil;
import lombok.Data;

@Data
public abstract class AbstractItemFilter<T> {

	private String attributeName;
	
	private boolean active = false;
	
	private T filterValue;

	public abstract boolean accept(Object item);
	
	protected Object getAttributeValue(Object item) {
		return FilterUtil.getAttributeValue(getAttributeName(), item);
	}

	public abstract boolean isNullValue(T value);
	
	public AbstractItemFilter<T> withAttributeName(String anAttributeName) {
		attributeName = anAttributeName;
		return this;
	}
}