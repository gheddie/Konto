package de.gravitex.accounting.filter.impl;

import de.gravitex.accounting.filter.FilterUitl;
import de.gravitex.accounting.filter.impl.base.AbstractItemFilter;

public class EqualFilter extends AbstractItemFilter<Object> {

	public EqualFilter(String attributeName) {
		super(attributeName);
	}

	@Override
	public boolean accept(Object item) {
		return FilterUitl.doValuesEqual(getAttributeValue(item), getFilterValue());
	}
}