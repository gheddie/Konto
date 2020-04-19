package de.gravitex.accounting.filter.impl;

import de.gravitex.accounting.filter.FilterUitl;
import de.gravitex.accounting.filter.impl.base.AbstractItemFilter;

public class EqualFilter extends AbstractItemFilter {

	public EqualFilter(String attributeName) {
		super(attributeName);
	}

	@Override
	public boolean accept(Object item) {
		return FilterUitl.doValuesEqual(FilterUitl.getAttributeValue(getAttributeName(), item), getFilterValue());
	}
}