package de.gravitex.accounting.filter.interfacing;

public interface FilterValueProvider {

	void setMvcData(IFilteredValueReceiver filteredValueReceiver, FilteredValuesHolder filteredValuesHolder, String attributeName);
	
	Object getSelectedFilterValue();
	
	void initData();
}