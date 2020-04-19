package de.gravitex.accounting.filter.interfacing;

public interface FilteredValuesHolder {

	void loadData();
	
	void acceptDataChagedListener(FilterDataChangedListener changeListener);
}