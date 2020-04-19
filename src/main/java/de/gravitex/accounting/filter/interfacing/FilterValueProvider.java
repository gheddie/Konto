package de.gravitex.accounting.filter.interfacing;

import java.util.List;

public interface FilterValueProvider {

	void setMvcData(FilteredValueReceiver filteredValueReceiver, FilteredValuesHolder filteredValuesHolder, String attributeName);
	
	Object getSelectedFilterValue();
	
	List<?> loadData();
}