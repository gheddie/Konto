package de.gravitex.accounting.util;

import de.gravitex.accounting.filter.interfacing.FilteredComponentListener;
import de.gravitex.accounting.filter.interfacing.FilteredValuesHolder;

public class FakedValueHolder implements FilteredValuesHolder {

	private FilteredComponentListener changeListener;

	@Override
	public void loadData() {
		// TODO Auto-generated method stub
	}
	
	@Override
	public void acceptFilteredComponentListener(FilteredComponentListener changeListener) {
		this.changeListener = changeListener;
	}
}