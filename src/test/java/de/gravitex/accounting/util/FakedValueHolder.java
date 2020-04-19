package de.gravitex.accounting.util;

import de.gravitex.accounting.filter.interfacing.FilterDataChangedListener;
import de.gravitex.accounting.filter.interfacing.FilteredValuesHolder;

public class FakedValueHolder implements FilteredValuesHolder {

	private FilterDataChangedListener changeListener;

	@Override
	public void loadData() {
		// TODO Auto-generated method stub
	}
	
	@Override
	public void acceptDataChagedListener(FilterDataChangedListener changeListener) {
		this.changeListener = changeListener;
	}
}