package de.gravitex.accounting.filter.impl;

import java.time.LocalDate;

import de.gravitex.accounting.filter.impl.base.AbstractItemFilter;
import de.gravitex.accounting.filter.util.LocalDateRange;

public class DateRangeFilter extends AbstractItemFilter<LocalDateRange> {

	@Override
	public boolean accept(Object item) {
		return getFilterValue().contains((LocalDate) getAttributeValue(item));
	}

	@Override
	public boolean isNullValue(LocalDateRange value) {
		return false;
	}
}