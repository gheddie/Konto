package de.gravitex.accounting.filter;

import de.gravitex.accounting.filter.FilteredValueReceiver;

public interface FilterValueProvider {

	void acceptFilterReceiver(FilteredValueReceiver filteredValueReceiver);
	
	Object getSelectedFilterValue();
	
	void setAttributeName(String attributeName);
}