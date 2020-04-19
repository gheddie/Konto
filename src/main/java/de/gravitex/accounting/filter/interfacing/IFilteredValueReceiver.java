package de.gravitex.accounting.filter.interfacing;

import java.util.Set;

import de.gravitex.accounting.filter.FilterValue;

public interface IFilteredValueReceiver {

	void receiveFilterValue(FilterValue filterValue);

	Set<?> loadDistinctItems(String attributeName);
}