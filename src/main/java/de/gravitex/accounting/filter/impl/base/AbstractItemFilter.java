package de.gravitex.accounting.filter.impl.base;

import de.gravitex.accounting.filter.FilterUitl;
import lombok.Data;

@Data
public abstract class AbstractItemFilter<T> {

	private String attributeName;
	
	private boolean active = false;
	
	private T filterValue;

	public AbstractItemFilter(String attributeName) {
		super();
		this.attributeName = attributeName;
	}
	
	public abstract boolean accept(Object item);
	
	protected Object getAttributeValue(Object item) {
		return FilterUitl.getAttributeValue(getAttributeName(), item);
	}
}