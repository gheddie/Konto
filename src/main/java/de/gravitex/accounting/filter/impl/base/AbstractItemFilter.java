package de.gravitex.accounting.filter.impl.base;

import java.lang.reflect.InvocationTargetException;

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
	
	protected Object getAttributeValue(Object item) {
		try {
			String getterName = constructGetterName();
			Object invoked = item.getClass().getMethod(getterName, null).invoke(item, null);
			return invoked;
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
				| SecurityException e) {
			e.printStackTrace();
			return null;
		}
	}

	protected String constructGetterName() {
		return "get" + firstToUpper();
	}

	private String firstToUpper() {
		return attributeName.substring(0, 1).toUpperCase() + attributeName.substring(1);
	}
	
	protected boolean doValuesEqual(Object value1, Object value2) {
		
		if (value1 == null && value2 == null) {
			return true;
		}
		if (value1 != null && value2 == null) {
			return false;
		}
		if (value1 == null && value2 != null) {
			return false;
		}
		return value1.equals(value2);
	}
}