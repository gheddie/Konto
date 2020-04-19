package de.gravitex.accounting.filter.impl.base;

import lombok.Data;

@Data
public abstract class AbstractItemFilter {

	private String attributeName;
	
	private boolean active = false;
	
	private Object filterValue;

	public AbstractItemFilter(String attributeName) {
		super();
		this.attributeName = attributeName;
	}
	
	public abstract boolean accept(Object item);
}