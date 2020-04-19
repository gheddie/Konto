package de.gravitex.accounting.util;

import java.util.List;

import de.gravitex.accounting.FilterTestItem;
import de.gravitex.accounting.filter.FilterValue;
import de.gravitex.accounting.filter.interfacing.FilteredValueReceiver;
import lombok.Data;

@Data
public class FilterTestData implements FilteredValueReceiver {
	
	public static final String ATTR_STRING = "string";
	
	public static final String ATTR_INTEGER = "integer";
	
	public static final String ATTR_LOCAL_DATE = "localDate";

	private List<FilterTestItem> filterTestItems;

	@Override
	public void receiveFilterValue(FilterValue filterValue) {
		// TODO Auto-generated method stub
	}

	@Override
	public List<?> loadDistinctItems(String attributeName) {
		return null;
	}
}