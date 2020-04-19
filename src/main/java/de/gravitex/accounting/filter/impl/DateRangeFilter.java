package de.gravitex.accounting.filter.impl;

import java.time.LocalDate;

import de.gravitex.accounting.filter.impl.base.AbstractItemFilter;
import de.gravitex.accounting.filter.util.LocalDateRange;

public class DateRangeFilter extends AbstractItemFilter<LocalDateRange> {

	public DateRangeFilter(String attributeName) {
		super(attributeName);
	}

	@Override
	public boolean accept(Object item) {
		return getFilterValue().contains((LocalDate) getAttributeValue(item));
	}
}