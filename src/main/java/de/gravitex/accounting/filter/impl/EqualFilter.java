package de.gravitex.accounting.filter.impl;

public class EqualFilter extends AbstractItemFilter {

	public EqualFilter(String attributeName) {
		super(attributeName);
	}

	@Override
	public boolean accept(Object item) {
		return getAttributeValue(item).equals(getFilterValue());
	}
}