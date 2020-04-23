package de.gravitex.accounting.filter;

import de.gravitex.accounting.filter.impl.base.AbstractItemFilter;
import lombok.Data;

@Data
public class FilterDefinition {

	private String attributeName;
	
	private Class<? extends AbstractItemFilter<?>> filterClass;
	
	private FilterDefinition() {
		super();
	}
	
	public static FilterDefinition fromValues(String anAttributeName, Class<? extends AbstractItemFilter<?>> aFilterClass) {
		FilterDefinition filterDefinition = new FilterDefinition();
		filterDefinition.setAttributeName(anAttributeName);
		filterDefinition.setFilterClass(aFilterClass);
		return filterDefinition;
	}
}