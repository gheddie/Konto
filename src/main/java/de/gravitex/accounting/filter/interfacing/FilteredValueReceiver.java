package de.gravitex.accounting.filter.interfacing;

import java.util.List;

import de.gravitex.accounting.filter.FilterValue;

public interface FilteredValueReceiver {

	void receiveFilterValue(FilterValue filterValue);

	List<?> loadDistinctItems(String attributeName);
}