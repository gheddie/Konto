package de.gravitex.accounting.filter.interfacing;

public interface FilteredValuesHolder {

	void loadData();
	
	void acceptFilteredComponentListener(FilteredComponentListener changeListener);
}