package de.gravitex.accounting.filter.impl;

import de.gravitex.accounting.filter.EntityFilter;
import de.gravitex.accounting.filter.FilterUtil;
import de.gravitex.accounting.filter.impl.base.AbstractItemFilter;

public class EqualFilter extends AbstractItemFilter<Object> {
	
	public EqualFilter(String attributeName) {
		super(attributeName);
	}

	@Override
	public boolean accept(Object item) {
		return FilterUtil.doValuesEqual(getAttributeValue(item), getFilterValue());
	}

	@Override
	public boolean isNullValue(Object value) {
		return (value != null && value.equals(EntityFilter.NO_FILTER));
	}
}