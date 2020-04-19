package de.gravitex.accounting.util;

import java.util.List;

import org.apache.log4j.Logger;

import de.gravitex.accounting.FilterTestItem;
import de.gravitex.accounting.filter.FilterValue;
import de.gravitex.accounting.filter.FilteredValueReceiver;
import lombok.Data;

@Data
public class FilterTestDataProvider extends FilteredValueReceiver<FilterTestItem> {
	
	private static final Logger logger = Logger.getLogger(FilterTestDataProvider.class);
	
	public static final String ATTR_STRING = "string";
	
	public static final String ATTR_INTEGER = "integer";
	
	public static final String ATTR_LOCAL_DATE = "localDate";

	private List<FilterTestItem> filterTestItems;

	@Override
	public void receiveFilterValue(FilterValue filterValue) {
		logger.info("receiveFilterValue: " + filterValue);
	}

	@Override
	public List<FilterTestItem> loadFilteredItems() {
		return null;
	}

	@Override
	protected List<FilterTestItem> loadAllItems() {
		return filterTestItems;
	}
}