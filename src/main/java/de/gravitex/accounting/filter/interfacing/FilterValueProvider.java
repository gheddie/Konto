package de.gravitex.accounting.filter.interfacing;

public interface FilterValueProvider<T> {

	void setMvcData(IFilteredValueReceiver filteredValueReceiver, FilteredValuesHolder filteredValuesHolder, String attributeName);
	
	T getSelectedFilterValue();
	
	void initData();
}