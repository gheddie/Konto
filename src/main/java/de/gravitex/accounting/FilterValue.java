package de.gravitex.accounting;

import lombok.Data;

@Data
public class FilterValue {
	
	private String attributeName;
	
	private Object value;
	
	private FilterValue() {
		// ...
	}

	public static FilterValue fromValues(String anAttributeName, Object aValue) {
		FilterValue filterValue = new FilterValue();
		filterValue.setAttributeName(anAttributeName);
		filterValue.setValue(aValue);
		return filterValue;
	}
}