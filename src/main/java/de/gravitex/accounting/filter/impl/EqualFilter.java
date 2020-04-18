package de.gravitex.accounting.filter.impl;

import de.gravitex.accounting.filter.impl.base.AbstractItemFilter;

public class EqualFilter extends AbstractItemFilter {

	public EqualFilter(String attributeName) {
		super(attributeName);
	}

	@Override
	public boolean accept(Object item) {
		Object attributeValue = getAttributeValue(item);
		// return attributeValue.equals(getFilterValue());
		return doValuesEqual(attributeValue, getFilterValue());
	}
}