package de.gravitex.accounting.filter.impl;

import de.gravitex.accounting.filter.impl.base.AbstractItemFilter;

public class BooleanFilter extends AbstractItemFilter<Boolean> {

	@Override
	public boolean accept(Object item) {
		Object attributeValue = getAttributeValue(item);
		return (attributeValue != null && (Boolean) attributeValue);
	}

	@Override	
	public boolean isNullValue(Boolean value) {
		return (!value);
	}
}